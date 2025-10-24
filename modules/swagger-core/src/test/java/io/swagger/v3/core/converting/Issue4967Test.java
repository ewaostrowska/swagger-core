package io.swagger.v3.core.converting;

import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.oas.models.Issue4967Model;
import io.swagger.v3.oas.models.media.Schema;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

/**
 * Minimal reproduction tests for Issue #4967
 * 
 * Bug: RequiredMode.NOT_REQUIRED is not respected by properties 
 * annotated with @NotBlank and @NotEmpty
 */
public class Issue4967Test {

    /**
     * Test 1: Verify @NotBlank with RequiredMode.NOT_REQUIRED is not required
     * 
     * Expected: Field should NOT be in required list
     * Actual: Field IS in required list (BUG)
     */
    @Test
    public void testNotBlankWithNotRequiredMode() {
        final Map<String, Schema> models = ModelConverters.getInstance().readAll(Issue4967Model.class);
        Schema model = models.get("Issue4967Model");
        
        assertNotNull(model, "Model should exist");
        assertNotNull(model.getRequired(), "Required list should exist");
        
        // BUG: This fails because notBlankNotRequired IS in the required list
        // even though it has explicit @Schema(requiredMode = RequiredMode.NOT_REQUIRED)
        assertFalse(model.getRequired().contains("notBlankNotRequired"),
            "Field with @NotBlank and RequiredMode.NOT_REQUIRED should NOT be required");
    }

    /**
     * Test 2: Verify @NotEmpty with RequiredMode.NOT_REQUIRED is not required
     * 
     * Expected: Field should NOT be in required list
     * Actual: Field IS in required list (BUG)
     */
    @Test
    public void testNotEmptyWithNotRequiredMode() {
        final Map<String, Schema> models = ModelConverters.getInstance().readAll(Issue4967Model.class);
        Schema model = models.get("Issue4967Model");
        
        assertNotNull(model, "Model should exist");
        assertNotNull(model.getRequired(), "Required list should exist");
        
        // BUG: This fails because notEmptyNotRequired IS in the required list
        // even though it has explicit @Schema(requiredMode = RequiredMode.NOT_REQUIRED)
        assertFalse(model.getRequired().contains("notEmptyNotRequired"),
            "Field with @NotEmpty and RequiredMode.NOT_REQUIRED should NOT be required");
    }
}
