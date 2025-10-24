package io.swagger.v3.core.oas.models;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Model for Issue #4967 - RequiredMode.NOT_REQUIRED override test
 * 
 * Tests that @Schema(requiredMode = RequiredMode.NOT_REQUIRED) correctly
 * overrides the inferred requirement from @NotBlank and @NotEmpty annotations.
 */
public class Issue4967Model {
    
    /**
     * Field with @NotBlank and explicit NOT_REQUIRED override.
     * Expected: Should NOT be required in OpenAPI spec.
     * Bug: Currently marked as required, ignoring the override.
     */
    @Schema(description = "NotBlank with NOT_REQUIRED override", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @NotBlank
    public String notBlankNotRequired;

    /**
     * Field with @NotEmpty and explicit NOT_REQUIRED override.
     * Expected: Should NOT be required in OpenAPI spec.
     * Bug: Currently marked as required, ignoring the override.
     */
    @Schema(description = "NotEmpty with NOT_REQUIRED override", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @NotEmpty
    public List<String> notEmptyNotRequired;
}
