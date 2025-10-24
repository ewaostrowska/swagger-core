package io.swagger.v3.core.oas.models;

import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Base response class for Issue #5003 polymorphism test
 */
@Schema(
    description = "Base response with discriminator",
    discriminatorProperty = "type",
    discriminatorMapping = {
        @DiscriminatorMapping(value = "CONCRETION_A", schema = ConcretionResponseA.class)
    }
)
public class BaseResponse {
    @Schema(description = "Response type", required = true)
    private String type;
    
    @Schema(description = "Base field")
    private String baseField;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBaseField() {
        return baseField;
    }

    public void setBaseField(String baseField) {
        this.baseField = baseField;
    }
}
