package com.huya.search.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


public final class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);

    private static final ObjectMapper prettyObjectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static ObjectMapper getPrettyObjectMapper() {
        return prettyObjectMapper;
    }

    public static ObjectNode createObject() {
        return objectMapper.createObjectNode();
    }

    public static ArrayNode createArray() {
        return objectMapper.createArrayNode();
    }

}
