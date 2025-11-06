package io.swagger.v3.jaxrs2;

import io.swagger.v3.jaxrs2.resources.Issue4341Resource;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

/**
 * Reproduces GitHub Issue #4341
 * ArraySchema.arraySchema.requiredMode should control whether array field is required
 * 
 * @see https://github.com/swagger-api/swagger-core/issues/4341
 */
public class Issue4341Test {
    
    @Test(description = "Reproduces issue #4341: arraySchema.requiredMode should mark field as required")
    public void testArraySchemaRequiredMode() {
        Reader reader = new Reader(new OpenAPI());
        OpenAPI openAPI = reader.read(Issue4341Resource.class);
        
        Schema personSchema = openAPI.getComponents().getSchemas().get("Person");
        assertNotNull(personSchema, "Person schema should exist");
        
        List<String> required = personSchema.getRequired();
        
        // BUG: This should pass but currently fails
        // arraySchema.requiredMode = REQUIRED should make 'addresses' required
        assertTrue(required != null && required.contains("addresses"), 
            "Field 'addresses' should be required when arraySchema.requiredMode=REQUIRED (currently fails - this is the bug)");
    }
    
    @Test(description = "Validates workaround using schema.requiredMode instead of arraySchema.requiredMode")
    public void testWorkaround() {
        Reader reader = new Reader(new OpenAPI());
        OpenAPI openAPI = reader.read(Issue4341Resource.class);
        
        Schema personSchema = openAPI.getComponents().getSchemas().get("Person");
        List<String> required = personSchema.getRequired();
        
        // Workaround: Using schema.requiredMode works (but is semantically wrong)
        assertTrue(required != null && required.contains("workaroundAddresses"), 
            "Workaround with schema.requiredMode should work (validates current behavior)");
    }
}
