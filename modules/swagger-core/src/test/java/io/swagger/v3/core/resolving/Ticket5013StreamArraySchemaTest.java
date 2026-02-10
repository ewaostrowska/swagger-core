package io.swagger.v3.core.resolving;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverterContextImpl;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.core.resolving.resources.StreamModel;
import io.swagger.v3.oas.models.media.Schema;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Regression test for issue #5013 - Stream ArraySchema Wrong Type in OAS 3.1
 */
public class Ticket5013StreamArraySchemaTest extends SwaggerTestBase {
    
    @Test
    public void testStreamPropertyGeneratesArraySchemaInOAS31() throws Exception {
        final ModelResolver modelResolver = new ModelResolver(mapper()).openapi31(true);
        final ModelConverterContextImpl context = new ModelConverterContextImpl(modelResolver);

        final Schema model = context.resolve(new AnnotatedType(StreamModel.class));
        assertNotNull(model);

        Schema greetingsProperty = (Schema) model.getProperties().get("greetings");
        assertNotNull(greetingsProperty, "greetings property should exist");
        
        // In OAS 3.1, use types array instead of type
        assertNotNull(greetingsProperty.getTypes(), "types should not be null");
        assertEquals(greetingsProperty.getTypes().size(), 1, "Should have exactly one type");
        assertTrue(greetingsProperty.getTypes().contains("array"), 
            "Stream<Greeting> should generate types containing 'array' in OAS 3.1");
        
        assertNotNull(greetingsProperty.getItems(), 
            "Array schema should have items defined");
    }
    
    @Test
    public void testStreamPropertyGeneratesArraySchemaInOAS30() throws Exception {
        final ModelResolver modelResolver = new ModelResolver(mapper());
        final ModelConverterContextImpl context = new ModelConverterContextImpl(modelResolver);

        final Schema model = context.resolve(new AnnotatedType(StreamModel.class));
        assertNotNull(model);

        Schema greetingsProperty = (Schema) model.getProperties().get("greetings");
        assertNotNull(greetingsProperty, "greetings property should exist");
        
        // In OAS 3.0, should be array type
        assertEquals(greetingsProperty.getType(), "array", 
            "Stream<Greeting> should generate type: array in OAS 3.0");
        
        assertNotNull(greetingsProperty.getItems(), 
            "Array schema should have items defined");
    }
}
