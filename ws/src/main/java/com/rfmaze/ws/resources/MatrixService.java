package com.rfmaze.ws.resources;

import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.rfmaze.ws.bean.Attenuation;

@Path("/matrix")
public class MatrixService {

    @GET
    @Path("/{r}/{c}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAttenuation(@PathParam("r") int r, @PathParam("c") int c) {

        Map<String, Object> config = new HashMap<String, Object>();
        config.put("javax.json.stream.JsonGenerator.prettyPrinting", Boolean.valueOf(true));

        JsonBuilderFactory factory = Json.createBuilderFactory(config);
        JsonObject value = factory.createObjectBuilder().add("row", r).add("col", c).add("attenuation", 25).build();

        return Response.status(200).entity(value.toString()).build();

    }


    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setAttenuation(Attenuation atten) {

        System.out.println(atten);
        
        return Response.status(200).build();

    }

}