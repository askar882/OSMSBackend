package com.oas.osmsbackend.util;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * JSON工具类。
 * @author askar882
 * @date 2022/04/29
 */
@Slf4j
public enum JsonUtil {
    /**
     * 单例模式实例。
     */
    INSTANCE;

    /**
     * {@link ObjectMapper}实例，用于解析JSON数据。
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 序列化{@link Class}对象，提取类名称，属性类型和名称。
     * @param clazz 序列化的{@link Class}对象。
     * @return 序列化的JSON字符串。
     */
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

    /**
     * 序列化{@link Object}对象，包裹{@code ROOT_VALUE}。
     * @param object 序列化的对象。
     * @return 序列化的JSON字符串。
     */
    public String toJson(Object object) {
        return toJson(object, true);
    }

    /**
     * 序列化{@link Object}对象。
     * 若传入的对象的类已经被{@link JsonTypeInfo}标注，且{@link JsonTypeInfo#use()}的值为{@link JsonTypeInfo.As#WRAPPER_OBJECT}，
     * 则不启用{@link ObjectWriter}的{@link SerializationFeature#WRAP_ROOT_VALUE}特性。
     *
     * @param object 序列化的对象。
     * @param wrapRoot 是否包裹{@code ROOT_VALUE}。
     * @return 序列化的JSON字符串。
     */
    public String toJson(Object object, boolean wrapRoot) {
        if (object.getClass().isAnnotationPresent(JsonTypeInfo.class)) {
            JsonTypeInfo jsonTypeInfo = object.getClass().getAnnotation(JsonTypeInfo.class);
            if (JsonTypeInfo.As.WRAPPER_OBJECT.equals(jsonTypeInfo.include())) {
                log.debug(
                        "toJson: JsonTypeInfo annotation with WRAPPER_OBJECT inclusion found on class '{}', disabling wrapRoot.",
                        object.getClass().getName()
                );
                wrapRoot = false;
            }
        }
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

    /**
     * 反序列化JSON字符串。
     * @param jsonString JSON字符串。
     * @param valueType 反序列化后的结果类型。
     * @return 反序列化结果，失败返回{@code null}。
     */
    public <T> T fromJson(String jsonString, Class<T> valueType) {
        try {
            return objectMapper.reader().readValue(jsonString, valueType);
        } catch (IOException e) {
            log.debug("Failed to deserialize JSON string: '{}'.", jsonString);
        }
        return null;
    }

}
