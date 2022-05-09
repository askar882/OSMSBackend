package com.oas.osmsbackend.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author askar882
 * @date 2022/04/29
 */
@Slf4j
public enum JsonUtil {
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
            String result = objectWriter.writeValueAsString(object);
            log.debug("Serialized object {}, result: {}", object, result);
            return result;
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize {}. Error: {}", object, e.getMessage());
            return "Failed to serialize " + object;
        }
    }

}
