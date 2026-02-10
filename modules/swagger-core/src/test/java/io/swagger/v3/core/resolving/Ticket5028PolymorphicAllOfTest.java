package io.swagger.v3.core.resolving;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverterContextImpl;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.core.matchers.SerializationMatchers;
import io.swagger.v3.core.resolving.resources.AttributeType;
import io.swagger.v3.core.resolving.resources.DateAttributeTypeImpl;
import io.swagger.v3.oas.models.media.Schema;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

/**
 * Regression test for issue #5028 - Polymorphic Types Missing allOf
 */
public class Ticket5028PolymorphicAllOfTest extends SwaggerTestBase {
    
    @Test
    public void testSubtypePreservesAllOf() throws Exception {
        final ModelResolver modelResolver = new ModelResolver(mapper());
        final ModelConverterContextImpl context = new ModelConverterContextImpl(modelResolver);

        // Resolve the parent interface
        final Schema parentModel = context.resolve(new AnnotatedType(AttributeType.class));
        assertNotNull(parentModel);

        // Resolve the subtype
        final Schema subtypeModel = context.resolve(new AnnotatedType(DateAttributeTypeImpl.class));
        assertNotNull(subtypeModel);

        // Verify that DateAttributeTypeImpl has allOf reference to AttributeType
        SerializationMatchers.assertEqualsToYaml(context.getDefinedModels(), 
            "AttributeType:\n" +
            "  required:\n" +
            "  - type\n" +
            "  type: object\n" +
            "  properties:\n" +
            "    name:\n" +
            "      type: string\n" +
            "    type:\n" +
            "      type: string\n" +
            "  discriminator:\n" +
            "    propertyName: type\n" +
            "DateAttributeTypeImpl:\n" +
            "  type: object\n" +
            "  allOf:\n" +
            "  - $ref: \"#/components/schemas/AttributeType\"\n" +
            "  - type: object\n" +
            "    properties:\n" +
            "      format:\n" +
            "        type: string\n");
    }
}
