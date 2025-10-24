package io.swagger.v3.jaxrs2.resources;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * Resource for Issue #4963 - minimal reproduction
 * 
 * Tests primitive boxed types (Boolean, Integer) in @ApiResponse
 * to demonstrate incorrect type casting to "string" in OpenAPI 3.1
 */
@Path("/issue4963")
public class Issue4963Resource {

    @POST
    @Path("/boolean")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = Boolean.class))
        )
    })
    public void booleanResponse() {
    }

    @POST
    @Path("/integer")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = Integer.class))
        )
    })
    public void integerResponse() {
    }
}
