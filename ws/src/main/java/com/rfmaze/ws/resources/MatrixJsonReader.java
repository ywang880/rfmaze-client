package com.rfmaze.ws.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

import com.rfmaze.ws.bean.Attenuation;

@Consumes(MediaType.APPLICATION_JSON)
public class MatrixJsonReader implements MessageBodyReader<Attenuation> {
    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public Attenuation readFrom(Class<Attenuation> type, Type genericType, Annotation[] annotations,
            MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {

        JsonReader reader = Json.createReader(new InputStreamReader(entityStream));
        JsonStructure jsonst = reader.read();

        JsonObject atten = jsonst.asJsonObject();
        return new Attenuation(atten.getJsonNumber("row").intValue(), atten.getJsonNumber("col").intValue(),
                atten.getJsonNumber("attenuation").intValue());
    }
}
