package io.swagger.v3.core.issues;

import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.models.media.JsonSchema;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.*;

/**
 * Reproduces GitHub Issue #4820
 * @Schema(example="1234", type="string") interpreted as integer
 * 
 * Bug: After upgrading to OpenAPI 3.1, @Schema examples with numeric strings
 * are incorrectly interpreted as integers even when type="string" is explicitly set.
 *
 * @see https://github.com/swagger-api/swagger-core/issues/4820
 */
public class Issue4820Test {

    /**
     * Test 1: Numeric string example interpreted as integer in OAS 3.1
     * 
     * Tests that @Schema(example="1234", type="string") keeps the example as a String
     * instead of converting it to an Integer.
     */
    @Test
    public void testNumericStringExampleInterpretedAsInteger() {
        ModelConverters converter = ModelConverters.getInstance(true);
        Map<String, io.swagger.v3.oas.models.media.Schema> schemas = converter.read(TestDtoWithNumericExample.class);
        
        assertNotNull(schemas);
        assertNotNull(schemas.get("TestDtoWithNumericExample"));
        
        JsonSchema schema = (JsonSchema) schemas.get("TestDtoWithNumericExample")
                .getProperties()
                .get("name");
        
        assertNotNull(schema, "name property should exist");
        assertEquals(schema.getType(), "string", "name should be of type string");
        
        // BUG: Even with type="string", example is interpreted as Integer
        assertNotNull(schema.getExample(), "example should not be null");
        assertTrue(schema.getExample() instanceof String, 
                "example should be String, but was: " + schema.getExample().getClass().getName()); // FAILS
        assertEquals(schema.getExample(), "1234", "example should be the string '1234'");
    }

    /**
     * Test 2: Non-numeric string example works correctly
     * 
     * Validates that examples with non-numeric strings work correctly,
     * confirming the issue is specific to numeric strings.
     */
    @Test
    public void testNonNumericStringExampleWorksCorrectly() {
        ModelConverters converter = ModelConverters.getInstance(true);
        Map<String, io.swagger.v3.oas.models.media.Schema> schemas = converter.read(TestDtoWithNonNumericExample.class);
        
        assertNotNull(schemas);
        assertNotNull(schemas.get("TestDtoWithNonNumericExample"));
        
        JsonSchema schema = (JsonSchema) schemas.get("TestDtoWithNonNumericExample")
                .getProperties()
                .get("name");
        
        assertNotNull(schema);
        assertEquals(schema.getType(), "string");
        
        // This should work fine (and currently does)
        assertNotNull(schema.getExample());
        assertTrue(schema.getExample() instanceof String,
                "example should be String");
        assertEquals(schema.getExample(), "notANumber");
    }

    // Minimal test models - only what's needed to reproduce the bug
    
    /**
     * Model with numeric string example - demonstrates the bug
     */
    public static class TestDtoWithNumericExample {
        @Schema(example = "1234", type = "string")
        private String name;
        private int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    /**
     * Model with non-numeric string example - works correctly
     */
    public static class TestDtoWithNonNumericExample {
        @Schema(example = "notANumber")
        private String name;
        private int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}
