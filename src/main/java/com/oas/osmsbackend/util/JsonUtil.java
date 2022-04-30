package com.oas.osmsbackend.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author askar882
 * @date 2022/04/29
 */
public class JsonUtil {
    private static volatile JsonUtil instance;

    public static synchronized JsonUtil getInstance() {
        JsonUtil result = instance;
        if (result == null) {
            synchronized (JsonUtil.class) {
                result = instance;
                if (result == null) {
                    instance = result = new JsonUtil();
                }
            }
        }
        return instance;
    }

    private final ObjectMapper objectMapper;

    private JsonUtil() {
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
    }

    public String toJson(Class<?> clazz) {
        String result = clazz.getSimpleName();
        if (!clazz.isPrimitive() && !CharSequence.class.isAssignableFrom(clazz)) {
            result += "{" +
                    Arrays.stream(clazz.getDeclaredFields())
                            .map(field -> field.getType().getSimpleName() + " " + field.getName())
                            .collect(Collectors.joining(", ")) +
                    "}";
        }
        return result;
    }

    public String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

}
