/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.management.network.v2019_11_01;

import java.util.Collection;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.microsoft.rest.ExpandableStringEnum;

/**
 * Defines values for ConnectionMonitorType.
 */
public final class ConnectionMonitorType extends ExpandableStringEnum<ConnectionMonitorType> {
    /** Static value MultiEndpoint for ConnectionMonitorType. */
    public static final ConnectionMonitorType MULTI_ENDPOINT = fromString("MultiEndpoint");

    /** Static value SingleSourceDestination for ConnectionMonitorType. */
    public static final ConnectionMonitorType SINGLE_SOURCE_DESTINATION = fromString("SingleSourceDestination");

    /**
     * Creates or finds a ConnectionMonitorType from its string representation.
     * @param name a name to look for
     * @return the corresponding ConnectionMonitorType
     */
    @JsonCreator
    public static ConnectionMonitorType fromString(String name) {
        return fromString(name, ConnectionMonitorType.class);
    }

    /**
     * @return known ConnectionMonitorType values
     */
    public static Collection<ConnectionMonitorType> values() {
        return values(ConnectionMonitorType.class);
    }
}
