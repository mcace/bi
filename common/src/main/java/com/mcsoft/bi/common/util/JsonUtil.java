package com.mcsoft.bi.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JSON工具类
 * Created by MC on 2020/8/12.
 *
 * @author MC
 */
public class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER;

    static {

        OBJECT_MAPPER = new ObjectMapper();
    }

    private JsonUtil() {
    }

    public static <T> T readValue(String json, Class<T> clazz) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(json, clazz);
    }

    public static <T> String writeToJson(T obj) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(obj);
    }

}
