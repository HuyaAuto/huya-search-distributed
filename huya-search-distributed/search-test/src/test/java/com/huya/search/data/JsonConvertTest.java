package com.huya.search.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * Created by zhangyiqun1@yy.com on 2017/10/10.
 */
public class JsonConvertTest {

    public static final String JSON =
            "{\n" +
            "    \"int\" : 1,\n" +
            "    \"long\" : 1000,\n" +
            "    \"float\" : 0.00,\n" +
            "    \"double\" : 0.000000001,\n" +
            "    \"string\" : \"string\"\n" +
            "}";

    @Test
    public void JacksonTest() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Object> map = mapper.readValue(JSON, Map.class);
            map.forEach((key, value) -> {System.out.println(value.getClass().getName());});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
