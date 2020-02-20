// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.
// Changes may cause incorrect behavior and will be lost if the code is
// regenerated.

package com.azure.search.models;

import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Information about a token returned by an analyzer.
 */
@Fluent
public final class TokenInfo {
    /*
     * The token returned by the analyzer.
     */
    @JsonProperty(value = "token", access = JsonProperty.Access.WRITE_ONLY)
    private String token;

    /*
     * The index of the first character of the token in the input text.
     */
    @JsonProperty(value = "startOffset", access = JsonProperty.Access.WRITE_ONLY)
    private Integer startOffset;

    /*
     * The index of the last character of the token in the input text.
     */
    @JsonProperty(value = "endOffset", access = JsonProperty.Access.WRITE_ONLY)
    private Integer endOffset;

    /*
     * The position of the token in the input text relative to other tokens.
     * The first token in the input text has position 0, the next has position
     * 1, and so on. Depending on the analyzer used, some tokens might have the
     * same position, for example if they are synonyms of each other.
     */
    @JsonProperty(value = "position", access = JsonProperty.Access.WRITE_ONLY)
    private Integer position;

    /**
     * Get the token property: The token returned by the analyzer.
     *
     * @return the token value.
     */
    public String getToken() {
        return this.token;
    }

    /**
     * Get the startOffset property: The index of the first character of the
     * token in the input text.
     *
     * @return the startOffset value.
     */
    public Integer getStartOffset() {
        return this.startOffset;
    }

    /**
     * Get the endOffset property: The index of the last character of the token
     * in the input text.
     *
     * @return the endOffset value.
     */
    public Integer getEndOffset() {
        return this.endOffset;
    }

    /**
     * Get the position property: The position of the token in the input text
     * relative to other tokens. The first token in the input text has position
     * 0, the next has position 1, and so on. Depending on the analyzer used,
     * some tokens might have the same position, for example if they are
     * synonyms of each other.
     *
     * @return the position value.
     */
    public Integer getPosition() {
        return this.position;
    }
}
