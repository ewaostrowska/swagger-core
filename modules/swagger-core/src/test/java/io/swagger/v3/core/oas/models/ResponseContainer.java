package io.swagger.v3.core.oas.models;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Container class that uses ConcretionResponseA as a property
 * This triggers property resolution BEFORE subtype resolution
 */
@Schema(description = "Container with polymorphic response property")
public class ResponseContainer {
    
    @Schema(description = "A polymorphic response property")
    private ConcretionResponseA response;

    public ConcretionResponseA getResponse() {
        return response;
    }

    public void setResponse(ConcretionResponseA response) {
        this.response = response;
    }
}
