package io.swagger.v3.core.issues;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverterContextImpl;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.core.resolving.SwaggerTestBase;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

/**
 * Test suite for GitHub Issue #4993
 * Regression: class specified in @ArraySchema.schema.implementation not rendered in schemas
 * 
 * Bug: In OpenAPI 3.1, when using @ArraySchema(schema = @Schema(implementation = SomeClass.class)),
 * the implementation class is not rendered in the components/schemas section, causing resolver errors.
 * 
 * Root Cause: PR #4445 changed ModelResolver31.java to detach Schema annotations, breaking
 * @ArraySchema.schema.implementation processing.
 * 
 * @see https://github.com/swagger-api/swagger-core/issues/4993
 * @see https://github.com/swagger-api/swagger-core/pull/4445
 */
public class Issue4993Test extends SwaggerTestBase {
    
    /**
     * Test model classes to reproduce the bug
     */
    static class ThingAssignment {
        @Schema(description = "Assignment identifier")
        public String id;
        
        @Schema(description = "Assignment name")
        public String name;
        
        @Schema(description = "Assignment status")
        public String status;
    }
    
    static class User {
        @Schema(description = "User identifier")
        public String userId;
        
        @Schema(description = "User name")
        public String userName;
        
        /**
         * This is the critical annotation pattern that fails in OpenAPI 3.1
         * The ThingAssignment class should be rendered in components/schemas
         */
        @ArraySchema(schema = @Schema(implementation = ThingAssignment.class, description = "Thing assignment"))
        public List<ThingAssignment> assignments;
    }
    
    static class UserWithNestedSchema {
        @Schema(description = "User identifier")
        public String userId;
        
        /**
         * Alternative pattern with arraySchema property
         */
        @ArraySchema(
            arraySchema = @Schema(description = "List of assignments"),
            schema = @Schema(implementation = ThingAssignment.class, description = "Individual assignment")
        )
        public List<ThingAssignment> assignments;
    }
    
    /**
     * Test 1: Reproduce the bug with OpenAPI 3.1
     * Expected: ThingAssignment should appear in components/schemas
     * Actual (bug): ThingAssignment is missing, causing reference resolution errors
     */
    @Test(description = "Reproduces @ArraySchema.schema.implementation regression in OpenAPI 3.1")
    public void testArraySchemaImplementationNotRenderedInOAS31() {
        final ModelResolver modelResolver = new ModelResolver(mapper()).openapi31(true);
        final ModelConverterContextImpl context = new ModelConverterContextImpl(modelResolver);
        
        // Resolve the User model which contains @ArraySchema with implementation
        io.swagger.v3.oas.models.media.Schema userModel = context.resolve(new AnnotatedType(User.class));
        
        assertNotNull(userModel, "User model should be resolved");
        assertNotNull(userModel.getProperties(), "User model should have properties");
        
        // Check that assignments property exists
        io.swagger.v3.oas.models.media.Schema assignmentsProperty = 
            (io.swagger.v3.oas.models.media.Schema) userModel.getProperties().get("assignments");
        assertNotNull(assignmentsProperty, "assignments property should exist");
        
        // Check if it has type array (may be null due to bug)
        String type = assignmentsProperty.getType();
        if (type != null) {
            assertEquals(type, "array", "assignments should be an array");
        }
        
        // BUG: ThingAssignment should be in the defined models but is missing in OAS 3.1
        Map<String, io.swagger.v3.oas.models.media.Schema> definedModels = context.getDefinedModels();
        
        // This assertion will FAIL until the bug is fixed
        assertTrue(definedModels.containsKey("ThingAssignment"), 
            "BUG: ThingAssignment class should be rendered in components/schemas for OpenAPI 3.1");
        
        // Verify the schema has proper structure
        if (definedModels.containsKey("ThingAssignment")) {
            io.swagger.v3.oas.models.media.Schema thingAssignmentSchema = definedModels.get("ThingAssignment");
            assertNotNull(thingAssignmentSchema.getProperties(), "ThingAssignment should have properties");
            assertTrue(thingAssignmentSchema.getProperties().containsKey("id"), 
                "ThingAssignment should have 'id' property");
            assertTrue(thingAssignmentSchema.getProperties().containsKey("name"), 
                "ThingAssignment should have 'name' property");
            assertTrue(thingAssignmentSchema.getProperties().containsKey("status"), 
                "ThingAssignment should have 'status' property");
        }
    }
    
    /**
     * Test 2: Verify OpenAPI 3.0 works correctly (baseline/workaround)
     * This should pass, showing the regression is specific to OAS 3.1
     */
    @Test(description = "Validates that OpenAPI 3.0 renders @ArraySchema.schema.implementation correctly")
    public void testArraySchemaImplementationRenderedInOAS30() {
        final ModelResolver modelResolver = new ModelResolver(mapper()).openapi31(false);
        final ModelConverterContextImpl context = new ModelConverterContextImpl(modelResolver);
        
        // Resolve the User model
        io.swagger.v3.oas.models.media.Schema userModel = context.resolve(new AnnotatedType(User.class));
        
        assertNotNull(userModel, "User model should be resolved");
        assertNotNull(userModel.getProperties(), "User model should have properties");
        
        // Check assignments property
        io.swagger.v3.oas.models.media.Schema assignmentsProperty = 
            (io.swagger.v3.oas.models.media.Schema) userModel.getProperties().get("assignments");
        assertNotNull(assignmentsProperty, "assignments property should exist");
        assertEquals(assignmentsProperty.getType(), "array", "assignments should be an array");
        
        // In OpenAPI 3.0, ThingAssignment SHOULD be in the defined models (this works)
        Map<String, io.swagger.v3.oas.models.media.Schema> definedModels = context.getDefinedModels();
        assertTrue(definedModels.containsKey("ThingAssignment"), 
            "ThingAssignment class should be rendered in components/schemas for OpenAPI 3.0");
        
        // Verify the schema structure
        io.swagger.v3.oas.models.media.Schema thingAssignmentSchema = definedModels.get("ThingAssignment");
        assertNotNull(thingAssignmentSchema.getProperties(), "ThingAssignment should have properties");
        assertEquals(3, thingAssignmentSchema.getProperties().size(), 
            "ThingAssignment should have 3 properties (id, name, status)");
    }
    
    /**
     * Test 3: Verify array items reference is created
     * Even if the schema is missing, check that the reference is attempted
     */
    @Test(description = "Validates that array items reference to implementation class")
    public void testArrayItemsReferencesImplementationClass() {
        final ModelResolver modelResolver = new ModelResolver(mapper()).openapi31(true);
        final ModelConverterContextImpl context = new ModelConverterContextImpl(modelResolver);
        
        io.swagger.v3.oas.models.media.Schema userModel = context.resolve(new AnnotatedType(User.class));
        
        io.swagger.v3.oas.models.media.Schema assignmentsProperty = 
            (io.swagger.v3.oas.models.media.Schema) userModel.getProperties().get("assignments");
        assertNotNull(assignmentsProperty, "assignments property should exist");
        
        // Check the items schema
        io.swagger.v3.oas.models.media.Schema items = assignmentsProperty.getItems();
        assertNotNull(items, "Array items should be defined");
        
        // The items should reference ThingAssignment (either directly or via $ref)
        // This may show a $ref to a non-existent schema if the bug exists
        boolean hasReference = items.get$ref() != null && items.get$ref().contains("ThingAssignment");
        boolean hasType = "object".equals(items.getType());
        
        assertTrue(hasReference || hasType, 
            "Array items should reference or define ThingAssignment type");
    }
    
    /**
     * Test 4: Test with arraySchema property as well
     * Ensures the bug affects different @ArraySchema patterns
     */
    @Test(description = "Validates @ArraySchema with both schema and arraySchema properties")
    public void testArraySchemaWithArraySchemaProperty() {
        final ModelResolver modelResolver = new ModelResolver(mapper()).openapi31(true);
        final ModelConverterContextImpl context = new ModelConverterContextImpl(modelResolver);
        
        io.swagger.v3.oas.models.media.Schema userModel = context.resolve(new AnnotatedType(UserWithNestedSchema.class));
        
        assertNotNull(userModel, "UserWithNestedSchema model should be resolved");
        
        // Verify ThingAssignment is rendered
        Map<String, io.swagger.v3.oas.models.media.Schema> definedModels = context.getDefinedModels();
        assertTrue(definedModels.containsKey("ThingAssignment"), 
            "ThingAssignment should be rendered when using arraySchema property");
    }
    
    /**
     * Test 5: Verify the schema annotation description is preserved
     * Related to PR #4445 changes that detach annotations
     */
    @Test(description = "Validates that @Schema descriptions within @ArraySchema are preserved")
    public void testSchemaDescriptionPreservedInArraySchema() {
        final ModelResolver modelResolver = new ModelResolver(mapper()).openapi31(true);
        final ModelConverterContextImpl context = new ModelConverterContextImpl(modelResolver);
        
        io.swagger.v3.oas.models.media.Schema userModel = context.resolve(new AnnotatedType(User.class));
        
        io.swagger.v3.oas.models.media.Schema assignmentsProperty = 
            (io.swagger.v3.oas.models.media.Schema) userModel.getProperties().get("assignments");
        assertNotNull(assignmentsProperty);
        
        // Check if items schema has the description from @Schema annotation
        io.swagger.v3.oas.models.media.Schema items = assignmentsProperty.getItems();
        if (items != null && items.getDescription() != null) {
            assertEquals("Thing assignment", items.getDescription(), 
                "Schema description should be preserved from @ArraySchema.schema annotation");
        }
    }
    
    /**
     * Test 6: Compare OAS 3.0 vs 3.1 behavior side by side
     * Demonstrates the regression clearly
     */
    @Test(description = "Compares OpenAPI 3.0 vs 3.1 behavior for same model")
    public void testCompareOAS30vsOAS31() {
        // Test with OAS 3.0
        final ModelResolver resolver30 = new ModelResolver(mapper()).openapi31(false);
        final ModelConverterContextImpl context30 = new ModelConverterContextImpl(resolver30);
        context30.resolve(new AnnotatedType(User.class));
        Map<String, io.swagger.v3.oas.models.media.Schema> models30 = context30.getDefinedModels();
        
        // Test with OAS 3.1
        final ModelResolver resolver31 = new ModelResolver(mapper()).openapi31(true);
        final ModelConverterContextImpl context31 = new ModelConverterContextImpl(resolver31);
        context31.resolve(new AnnotatedType(User.class));
        Map<String, io.swagger.v3.oas.models.media.Schema> models31 = context31.getDefinedModels();
        
        // Both should contain ThingAssignment
        boolean has30 = models30.containsKey("ThingAssignment");
        boolean has31 = models31.containsKey("ThingAssignment");
        
        assertTrue(has30, "OpenAPI 3.0 should contain ThingAssignment (baseline)");
        
        // This will fail until bug is fixed - demonstrates the regression
        assertEquals(has30, has31, 
            "REGRESSION: OpenAPI 3.1 should behave the same as 3.0 for @ArraySchema.schema.implementation");
    }
}
