package io.swagger.v3.core.oas.models;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Concrete subtype for Issue #5003 polymorphism test
 */
@Schema(description = "Concrete response A extending BaseResponse")
public class ConcretionResponseA extends BaseResponse {
    
    @Schema(description = "Concretion A specific field")
    private String concretionAField;

    public String getConcretionAField() {
        return concretionAField;
    }

    public void setConcretionAField(String concretionAField) {
        this.concretionAField = concretionAField;
    }
}
