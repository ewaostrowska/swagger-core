package io.swagger.v3.core.converting;

import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.oas.models.BaseResponse;
import io.swagger.v3.core.oas.models.ConcretionResponseA;
import io.swagger.v3.core.oas.models.ResponseContainer;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.*;

/**
 * Minimal reproduction tests for Issue #5003
 * 
 * Bug: Regression in v2.2.39 - AnnotatedType equality change drops 
 * polymorphic subtype allOf entries
 * 
 * Root Cause: AnnotatedType.equals()/hashCode() doesn't consider
 * schemaProperty/propertyName flags, causing property and subtype
 * resolutions to collapse into same cache entry.
 */
public class Issue5003Test {

    /**
     * Test 1: Verify polymorphic subtype contains complete allOf definition
     * 
     * This test reproduces the exact scenario from the bug report:
     * 1. ConcretionResponseA is first resolved as a property (in ResponseContainer)
     * 2. Then resolved as a subtype (polymorphic resolution from BaseResponse)
     * 
     * Expected: ConcretionResponseA has 2 allOf entries:
     *   - $ref to BaseResponse
     *   - object with concretionAField property
     * 
     * Actual (BUG): Only has $ref to BaseResponse, missing properties object
     */
    @Test
    public void testPolymorphicSubtypeHasCompleteAllOf() {
        // Resolve models - this triggers both property and subtype resolution
        final Map<String, Schema> models = ModelConverters.getInstance().readAll(ResponseContainer.class);
        
        // Get the ConcretionResponseA schema
        Schema concretionA = models.get("ConcretionResponseA");
        assertNotNull(concretionA, "ConcretionResponseA schema should exist");
        
        // Verify it's a ComposedSchema with allOf
        assertTrue(concretionA instanceof ComposedSchema, 
            "ConcretionResponseA should be a ComposedSchema");
        ComposedSchema composedSchema = (ComposedSchema) concretionA;
        
        assertNotNull(composedSchema.getAllOf(), 
            "ConcretionResponseA should have allOf");
        
        // BUG: This fails because allOf only contains 1 entry (the $ref)
        // Should have 2 entries: ref + properties object
        assertEquals(composedSchema.getAllOf().size(), 2, 
            "ConcretionResponseA allOf should have 2 entries: $ref to BaseResponse + properties object");
        
        // Verify first entry is the $ref to BaseResponse
        Schema firstEntry = composedSchema.getAllOf().get(0);
        assertNotNull(firstEntry.get$ref(), 
            "First allOf entry should be $ref to BaseResponse");
        assertTrue(firstEntry.get$ref().contains("BaseResponse"), 
            "First allOf entry should reference BaseResponse");
        
        // BUG: Second entry is missing entirely
        // It should contain the concretionAField property
        Schema secondEntry = composedSchema.getAllOf().get(1);
        assertNotNull(secondEntry.getProperties(), 
            "Second allOf entry should have properties");
        assertTrue(secondEntry.getProperties().containsKey("concretionAField"), 
            "Second allOf entry should contain concretionAField property");
    }

    /**
     * Test 2: Verify subtype resolution doesn't reuse property resolution cache
     * 
     * This is a simpler test that directly checks if the bug occurs
     * when the same type is resolved in different contexts.
     * 
     * Expected: BaseResponse schema includes discriminator mappings
     * Actual (BUG): May be incomplete due to cache collision
     */
    @Test
    public void testSubtypeResolutionDistinctFromPropertyResolution() {
        final Map<String, Schema> models = ModelConverters.getInstance().readAll(BaseResponse.class);
        
        // Verify BaseResponse exists
        Schema baseResponse = models.get("BaseResponse");
        assertNotNull(baseResponse, "BaseResponse schema should exist");
        
        // Verify it has discriminator
        assertNotNull(baseResponse.getDiscriminator(), 
            "BaseResponse should have discriminator");
        
        // Verify ConcretionResponseA was resolved as a subtype
        Schema concretionA = models.get("ConcretionResponseA");
        assertNotNull(concretionA, 
            "ConcretionResponseA should be resolved as a subtype");
        
        // Verify it's properly structured as a polymorphic type
        assertTrue(concretionA instanceof ComposedSchema, 
            "ConcretionResponseA should be a ComposedSchema for polymorphism");
        
        ComposedSchema composedSchema = (ComposedSchema) concretionA;
        assertNotNull(composedSchema.getAllOf(), 
            "ConcretionResponseA should have allOf for inheritance");
        
        // BUG: allOf is incomplete (missing properties entry)
        assertTrue(composedSchema.getAllOf().size() >= 2, 
            "ConcretionResponseA allOf should include both inheritance ref and own properties");
    }
}
