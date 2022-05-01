package com.oas.osmsbackend.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author askar882
 * @date 2022/04/29
 */
public enum JsonUtil {
    /**
     *
     */
    INSTANCE;

    private final ObjectMapper objectMapper = new ObjectMapper();

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
        return toJson(object, true);
    }

    public String toJson(Object object, boolean wrapRoot) {
        ObjectWriter objectWriter = objectMapper.writer();
        if (wrapRoot) {
            objectWriter = objectWriter.with(SerializationFeature.WRAP_ROOT_VALUE);
        }
        try {
            return objectWriter.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

}
