package io.swagger.v3.jaxrs2.resources;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;

/**
 * Test resource for GitHub Issue #4341
 * Reproduces bug where ArraySchema.arraySchema.requiredMode doesn't work
 * 
 * @see https://github.com/swagger-api/swagger-core/issues/4341
 */
public class Issue4341Resource {

    @GET
    @Path("/person")
    public Person getPerson() {
        return null;
    }

    static class Person {
        // Test 1: Bug reproduction - arraySchema.requiredMode should mark field as required
        @ArraySchema(
            arraySchema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        )
        public List<String> addresses;

        // Test 2: Current workaround - schema.requiredMode works (but shouldn't be used for array requirement)
        @ArraySchema(
            arraySchema = @Schema(description = "The person"),
            schema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        )
        public List<String> workaroundAddresses;
    }
}
