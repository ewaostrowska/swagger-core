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
 * Reproduces GitHub Issue #4993
 * Regression: class specified in @ArraySchema.schema.implementation not rendered in schemas
 * 
 * @see https://github.com/swagger-api/swagger-core/issues/4993
 */
public class Issue4993Test extends SwaggerTestBase {
    
    static class ThingAssignment {
        public String id;
        public String name;
        public String status;
    }
    
    static class User {
        @ArraySchema(schema = @Schema(implementation = ThingAssignment.class))
        public List<ThingAssignment> assignments;
    }
    
    @Test(description = "Reproduces issue #4993: @ArraySchema.schema.implementation not rendered in OAS 3.1")
    public void testIssue4993() {
        final ModelResolver modelResolver = new ModelResolver(mapper()).openapi31(true);
        final ModelConverterContextImpl context = new ModelConverterContextImpl(modelResolver);
        
        context.resolve(new AnnotatedType(User.class));
        
        Map<String, io.swagger.v3.oas.models.media.Schema> definedModels = context.getDefinedModels();
        
        assertTrue(definedModels.containsKey("ThingAssignment"), 
            "ThingAssignment should be rendered in components/schemas for OpenAPI 3.1");
    }
    
    @Test(description = "Baseline: OpenAPI 3.0 correctly renders @ArraySchema.schema.implementation")
    public void testOpenApi30Baseline() {
        final ModelResolver modelResolver = new ModelResolver(mapper()).openapi31(false);
        final ModelConverterContextImpl context = new ModelConverterContextImpl(modelResolver);
        
        context.resolve(new AnnotatedType(User.class));
        
        Map<String, io.swagger.v3.oas.models.media.Schema> definedModels = context.getDefinedModels();
        
        assertTrue(definedModels.containsKey("ThingAssignment"), 
            "ThingAssignment should be rendered in components/schemas for OpenAPI 3.0");
    }
}
