package com.creditcardcomparison.httpserver.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;

public class Json {

    private static ObjectMapper myObjectMapper = defaultObjectMapper();

    private static ObjectMapper defaultObjectMapper() {
        ObjectMapper om = new ObjectMapper();

        // keeps program from crashing if a property is missing
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return om;
    }

    public static JsonNode parse(String jsonSource) throws JsonProcessingException {
        return myObjectMapper.readTree(jsonSource);
    }

    public static <A> A fromJson(JsonNode node, Class<A> classType) throws JsonProcessingException {
        return myObjectMapper.treeToValue(node, classType);
    }


    public static JsonNode toJson(Object obj) {
        return myObjectMapper.valueToTree(obj);
    }

    public static String serialize(JsonNode node) throws JsonProcessingException {
        return generateJson(node, false);
    }

    public static String serializeFormatted(JsonNode node) throws JsonProcessingException {
        return generateJson(node, true);
    }

    private static String generateJson(Object obj, boolean format) throws JsonProcessingException {
        ObjectWriter objectWriter = myObjectMapper.writer();

        // Adds indents that make it easier to read when format is true
        if (format) {
            objectWriter = objectWriter.with(SerializationFeature.INDENT_OUTPUT);
        }

        return objectWriter.writeValueAsString(obj);
    }
}
