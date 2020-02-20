/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.management.deploymentmanager.v2019_11_01_preview;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The properties of a service.
 */
public class ServiceProperties {
    /**
     * The Azure location to which the resources in the service belong to or
     * should be deployed to.
     */
    @JsonProperty(value = "targetLocation", required = true)
    private String targetLocation;

    /**
     * The subscription to which the resources in the service belong to or
     * should be deployed to.
     */
    @JsonProperty(value = "targetSubscriptionId", required = true)
    private String targetSubscriptionId;

    /**
     * Get the Azure location to which the resources in the service belong to or should be deployed to.
     *
     * @return the targetLocation value
     */
    public String targetLocation() {
        return this.targetLocation;
    }

    /**
     * Set the Azure location to which the resources in the service belong to or should be deployed to.
     *
     * @param targetLocation the targetLocation value to set
     * @return the ServiceProperties object itself.
     */
    public ServiceProperties withTargetLocation(String targetLocation) {
        this.targetLocation = targetLocation;
        return this;
    }

    /**
     * Get the subscription to which the resources in the service belong to or should be deployed to.
     *
     * @return the targetSubscriptionId value
     */
    public String targetSubscriptionId() {
        return this.targetSubscriptionId;
    }

    /**
     * Set the subscription to which the resources in the service belong to or should be deployed to.
     *
     * @param targetSubscriptionId the targetSubscriptionId value to set
     * @return the ServiceProperties object itself.
     */
    public ServiceProperties withTargetSubscriptionId(String targetSubscriptionId) {
        this.targetSubscriptionId = targetSubscriptionId;
        return this;
    }

}
