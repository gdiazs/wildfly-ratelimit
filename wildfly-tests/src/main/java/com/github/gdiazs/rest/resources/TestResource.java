package com.github.gdiazs.rest.resources;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.github.gdiazs.rest.services.BottleNeckService;

@Path("/test")
public class TestResource {

    @Inject
    private BottleNeckService bottleNeckService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() {

        for (int i = 0; i < 125; i ++){
            this.bottleNeckService.operationAsync();
        }
        
        JsonObject json = Json.createObjectBuilder().add("status", "ok").build();
        return Response.ok(json).build();
    }
}
