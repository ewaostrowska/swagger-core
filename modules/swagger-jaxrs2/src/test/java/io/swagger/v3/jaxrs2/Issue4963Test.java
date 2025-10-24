package io.swagger.v3.jaxrs2;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.jaxrs2.resources.Issue4963Resource;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Schema;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Minimal reproduction tests for Issue #4963
 * 
 * Bug: Primitive boxed type schemas used in @ApiResponse content schema 
 * implementations are incorrectly cast to string in OpenAPI 3.1
 */
public class Issue4963Test {

    /**
     * Test 1: Verify Boolean.class primitive type has correct type field
     * 
     * Expected: type = "boolean" (matching types field)
     * Actual: type = "string" (BUG)
     */
    @Test
    public void testBooleanPrimitiveTypeCasting31() {
        SwaggerConfiguration config = new SwaggerConfiguration().openAPI31(true);
        Reader reader = new Reader(config);
        OpenAPI openAPI = reader.read(Issue4963Resource.class);

        Operation postOperation = openAPI.getPaths().get("/issue4963/boolean").getPost();
        assertNotNull(postOperation, "POST operation should exist");
        
        Schema responseSchema = postOperation.getResponses().get("200")
            .getContent().get("*/*").getSchema();
        
        assertNotNull(responseSchema, "Response schema should exist");
        assertNotNull(responseSchema.getTypes(), "Schema types should be set");
        
        String expectedType = (String) responseSchema.getTypes().iterator().next();
        
        // BUG: This fails because type is "string" instead of "boolean"
        assertEquals(responseSchema.getType(), expectedType, 
            "Schema type should match types field value");
    }

    /**
     * Test 2: Verify Integer.class primitive type has correct type field
     * 
     * Expected: type = "integer" (matching types field)
     * Actual: type = "string" (BUG)
     */
    @Test
    public void testIntegerPrimitiveTypeCasting31() {
        SwaggerConfiguration config = new SwaggerConfiguration().openAPI31(true);
        Reader reader = new Reader(config);
        OpenAPI openAPI = reader.read(Issue4963Resource.class);

        Operation postOperation = openAPI.getPaths().get("/issue4963/integer").getPost();
        assertNotNull(postOperation, "POST operation should exist");
        
        Schema responseSchema = postOperation.getResponses().get("200")
            .getContent().get("*/*").getSchema();
        
        assertNotNull(responseSchema, "Response schema should exist");
        assertNotNull(responseSchema.getTypes(), "Schema types should be set");
        
        String expectedType = (String) responseSchema.getTypes().iterator().next();
        
        // BUG: This fails because type is "string" instead of "integer"
        assertEquals(responseSchema.getType(), expectedType, 
            "Schema type should match types field value");
    }
}
