/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 *
 */

package com.microsoft.azure.management.network.v2019_11_01.implementation;

import com.microsoft.azure.arm.model.implementation.WrapperImpl;
import com.microsoft.azure.management.network.v2019_11_01.FlowLogs;
import rx.Completable;
import rx.Observable;
import rx.functions.Func1;
import com.microsoft.azure.Page;
import com.microsoft.azure.management.network.v2019_11_01.FlowLog;

class FlowLogsImpl extends WrapperImpl<FlowLogsInner> implements FlowLogs {
    private final NetworkManager manager;

    FlowLogsImpl(NetworkManager manager) {
        super(manager.inner().flowLogs());
        this.manager = manager;
    }

    public NetworkManager manager() {
        return this.manager;
    }

    @Override
    public FlowLogImpl define(String name) {
        return wrapModel(name);
    }

    private FlowLogImpl wrapModel(FlowLogInner inner) {
        return  new FlowLogImpl(inner, manager());
    }

    private FlowLogImpl wrapModel(String name) {
        return new FlowLogImpl(name, this.manager());
    }

    @Override
    public Observable<FlowLog> listAsync(final String resourceGroupName, final String networkWatcherName) {
        FlowLogsInner client = this.inner();
        return client.listAsync(resourceGroupName, networkWatcherName)
        .flatMapIterable(new Func1<Page<FlowLogInner>, Iterable<FlowLogInner>>() {
            @Override
            public Iterable<FlowLogInner> call(Page<FlowLogInner> page) {
                return page.items();
            }
        })
        .map(new Func1<FlowLogInner, FlowLog>() {
            @Override
            public FlowLog call(FlowLogInner inner) {
                return wrapModel(inner);
            }
        });
    }

    @Override
    public Observable<FlowLog> getAsync(String resourceGroupName, String networkWatcherName, String flowLogName) {
        FlowLogsInner client = this.inner();
        return client.getAsync(resourceGroupName, networkWatcherName, flowLogName)
        .flatMap(new Func1<FlowLogInner, Observable<FlowLog>>() {
            @Override
            public Observable<FlowLog> call(FlowLogInner inner) {
                if (inner == null) {
                    return Observable.empty();
                } else {
                    return Observable.just((FlowLog)wrapModel(inner));
                }
            }
       });
    }

    @Override
    public Completable deleteAsync(String resourceGroupName, String networkWatcherName, String flowLogName) {
        FlowLogsInner client = this.inner();
        return client.deleteAsync(resourceGroupName, networkWatcherName, flowLogName).toCompletable();
    }

}
