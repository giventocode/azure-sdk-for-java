// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.management.appservice.implementation;

import com.azure.core.annotation.BodyParam;
import com.azure.core.annotation.Get;
import com.azure.core.annotation.Headers;
import com.azure.core.annotation.Host;
import com.azure.core.annotation.HostParam;
import com.azure.core.annotation.Post;
import com.azure.core.annotation.QueryParam;
import com.azure.core.annotation.ServiceInterface;
import com.azure.core.http.rest.RestProxy;
import com.azure.core.http.rest.StreamResponse;
import com.azure.core.management.CloudException;
import com.azure.core.util.FluxUtil;
import com.azure.core.util.logging.ClientLogger;
import com.azure.management.RestClient;
import com.azure.management.appservice.KuduAuthenticationPolicy;
import com.azure.management.appservice.WebAppBase;
import com.fasterxml.jackson.core.JsonParseException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/** A client which interacts with Kudu service. */
class KuduClient {

    private final ClientLogger logger = new ClientLogger(getClass());

    private String host;
    private KuduService service;

    KuduClient(WebAppBase webAppBase) {
        if (webAppBase.defaultHostName() == null) {
            throw logger.logExceptionAsError(
                new UnsupportedOperationException("Cannot initialize kudu client before web app is created"));
        }
        String host = webAppBase.defaultHostName().toLowerCase().replace("http://", "").replace("https://", "");
        String[] parts = host.split("\\.", 2);
        host = parts[0] + ".scm." + parts[1];
        this.host = "https://" + host;
        RestClient client =
            webAppBase
                .manager()
                .restClient()
                .newBuilder()
                .withBaseUrl(this.host)
                .withPolicy(new KuduAuthenticationPolicy(webAppBase))
                // TODO (weidxu) support timeout
                //.withConnectionTimeout(3, TimeUnit.MINUTES)
                //.withReadTimeout(3, TimeUnit.MINUTES)
                .buildClient();

        service = RestProxy.create(KuduService.class, client.getHttpPipeline(), client.getSerializerAdapter());
    }

    @Host("{$host}")
    @ServiceInterface(name = "KuduService")
    private interface KuduService {
        @Headers({
            "x-ms-logging-context: com.microsoft.azure.management.appservice.WebApps streamApplicationLogs",
            "x-ms-body-logging: false"
        })
        @Get("api/logstream/application")
        Mono<StreamResponse> streamApplicationLogs(@HostParam("$host") String host);

        @Headers({
            "x-ms-logging-context: com.microsoft.azure.management.appservice.WebApps streamHttpLogs",
            "x-ms-body-logging: false"
        })
        @Get("api/logstream/http")
        Mono<StreamResponse> streamHttpLogs(@HostParam("$host") String host);

        @Headers({
            "x-ms-logging-context: com.microsoft.azure.management.appservice.WebApps streamTraceLogs",
            "x-ms-body-logging: false"
        })
        @Get("api/logstream/kudu/trace")
        Mono<StreamResponse> streamTraceLogs(@HostParam("$host") String host);

        @Headers({
            "x-ms-logging-context: com.microsoft.azure.management.appservice.WebApps streamDeploymentLogs",
            "x-ms-body-logging: false"
        })
        @Get("api/logstream/kudu/deployment")
        Mono<StreamResponse> streamDeploymentLogs(@HostParam("$host") String host);

        @Headers({
            "x-ms-logging-context: com.microsoft.azure.management.appservice.WebApps streamAllLogs",
            "x-ms-body-logging: false"
        })
        @Get("api/logstream")
        Mono<StreamResponse> streamAllLogs(@HostParam("$host") String host);

        @Headers({
            "Content-Type: application/octet-stream",
            "x-ms-logging-context: com.microsoft.azure.management.appservice.WebApps warDeploy",
            "x-ms-body-logging: false"
        })
        @Post("api/wardeploy")
        Mono<Void> warDeploy(
            @HostParam("$host") String host,
            @BodyParam("application/octet-stream") byte[] warFile,
            @QueryParam("name") String appName);

        @Headers({
            "Content-Type: application/octet-stream",
            "x-ms-logging-context: com.microsoft.azure.management.appservice.WebApps zipDeploy",
            "x-ms-body-logging: false"
        })
        @Post("api/zipdeploy")
        Mono<Void> zipDeploy(@HostParam("$host") String host, @BodyParam("application/octet-stream") byte[] zipFile);
    }

    Flux<String> streamApplicationLogsAsync() {
        return streamFromFluxBytes(service.streamApplicationLogs(host).flatMapMany(StreamResponse::getValue));
    }

    Flux<String> streamHttpLogsAsync() {
        return streamFromFluxBytes(service.streamHttpLogs(host).flatMapMany(StreamResponse::getValue));
    }

    Flux<String> streamTraceLogsAsync() {
        return streamFromFluxBytes(service.streamTraceLogs(host).flatMapMany(StreamResponse::getValue));
    }

    Flux<String> streamDeploymentLogsAsync() {
        return streamFromFluxBytes(service.streamDeploymentLogs(host).flatMapMany(StreamResponse::getValue));
    }

    Flux<String> streamAllLogsAsync() {
        return streamFromFluxBytes(service.streamAllLogs(host).flatMapMany(StreamResponse::getValue));
    }

    static Flux<String> streamFromFluxBytes(final Flux<ByteBuffer> source) {
        final byte newLine = '\n';
        final byte newLineR = '\r';

        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        return source
            .concatMap(
                byteBuffer -> {
                    int index = findByte(byteBuffer, newLine);
                    if (index == -1) {
                        // no newLine byte found, not a line, put it into stream
                        try {
                            stream.write(FluxUtil.byteBufferToArray(byteBuffer));
                            return Flux.empty();
                        } catch (IOException e) {
                            return Flux.error(e);
                        }
                    } else {
                        // newLine byte found, at least 1 line
                        List<String> lines = new ArrayList<>();
                        while ((index = findByte(byteBuffer, newLine)) != -1) {
                            byte[] byteArray = new byte[index + 1];
                            byteBuffer.get(byteArray);
                            try {
                                stream.write(byteArray);
                                String line = new String(stream.toByteArray(), StandardCharsets.UTF_8);
                                if (!line.isEmpty() && line.charAt(line.length() - 1) == newLine) {
                                    // OK this is a line, end with newLine char
                                    line = line.substring(0, line.length() - 1);
                                    if (!line.isEmpty() && line.charAt(line.length() - 1) == newLineR) {
                                        line = line.substring(0, line.length() - 1);
                                    }
                                    lines.add(line);
                                    stream.reset();
                                }
                            } catch (IOException e) {
                                return Flux.error(e);
                            }
                        }
                        if (byteBuffer.hasRemaining()) {
                            // put rest into stream
                            try {
                                stream.write(FluxUtil.byteBufferToArray(byteBuffer));
                            } catch (IOException e) {
                                return Flux.error(e);
                            }
                        }
                        if (lines.isEmpty()) {
                            return Flux.empty();
                        } else {
                            return Flux.fromIterable(lines);
                        }
                    }
                });
    }

    private static int findByte(ByteBuffer byteBuffer, byte b) {
        final int position = byteBuffer.position();
        int index = -1;
        for (int i = 0; i < byteBuffer.remaining(); ++i) {
            if (byteBuffer.get(position + i) == b) {
                index = i;
                break;
            }
        }
        return index;
    }

    Mono<Void> warDeployAsync(InputStream warFile, String appName) {
        return withRetry(service.warDeploy(host, byteArrayFromInputStream(warFile), appName));
    }

    Mono<Void> zipDeployAsync(InputStream zipFile) {
        return withRetry(service.zipDeploy(host, byteArrayFromInputStream(zipFile)));
    }

    private byte[] byteArrayFromInputStream(InputStream inputStream) {
        // TODO (weidxu) core does not yet support InputStream as @BodyParam
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[16384];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            return buffer.toByteArray();
        } catch (IOException e) {
            throw logger.logExceptionAsError(new IllegalStateException(e));
        }
    }

    private Mono<Void> withRetry(Mono<Void> observable) {
        return observable
            .retryWhen(
                flux ->
                    flux
                        .zipWith(
                            Flux.range(1, 30),
                            (Throwable throwable, Integer integer) -> {
                                if (throwable instanceof CloudException
                                        && ((CloudException) throwable).getResponse().getStatusCode() == 502
                                    || throwable instanceof JsonParseException) {
                                    return integer;
                                } else {
                                    throw logger.logExceptionAsError(Exceptions.propagate(throwable));
                                }
                            })
                        .flatMap(i -> Mono.delay(Duration.ofSeconds(i))));
    }

}
