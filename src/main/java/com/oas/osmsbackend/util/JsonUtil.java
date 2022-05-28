package com.oas.osmsbackend.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * JSON工具类。
 *
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
     *
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
     *
     * @param object 序列化的对象。
     * @return 序列化的JSON字符串。
     */
    public String toJson(Object object) {
        return toJson(object, true);
    }

    /**
     * 序列化{@link Object}对象。
     * {@code wrapRoot}参数为{@code true}时启用{@link ObjectWriter}的{@link SerializationFeature#WRAP_ROOT_VALUE}特性。
     *
     * @param object 序列化的对象。
     * @param wrapRoot 是否包裹{@code ROOT_VALUE}。
     * @return 序列化的JSON字符串。
     */
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

    /**
     * 反序列化JSON字符串。
     * {@code unwrapRoot}参数为{@code true}时启用{@link ObjectReader}的{@link DeserializationFeature#UNWRAP_ROOT_VALUE}特性。
     *
     * @param jsonString JSON字符串。
     * @param valueType 反序列化结果类型。
     * @param unwrapRoot 是否去除外层包裹。
     * @return 反序列化的 {@code Optional}结果，失败返回{@link Optional#empty()}。
     * @param <T> 反序列化结果类型。
     */
    public <T> Optional<T> fromJson(String jsonString, Class<T> valueType, boolean unwrapRoot) {
        if (!StringUtils.hasText(jsonString)) {
            return Optional.empty();
        }
        ObjectReader objectReader = objectMapper.reader().without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        if (unwrapRoot) {
            objectReader = objectReader.with(DeserializationFeature.UNWRAP_ROOT_VALUE);
        }
        try {
            T object = objectReader.readValue(jsonString, valueType);
            log.debug("Deserialized JSON '{}', result object: '{}'.", jsonString, object);
            return Optional.of(object);
        } catch (IOException e) {
            log.debug("Failed to deserialize JSON string.", e);
        }
        return Optional.empty();
    }

    /**
     * 反序列化JSON字符串，默认不去除外层包裹。
     *
     * @param jsonString JSON字符串。
     * @param valueType 反序列化结果类型。
     * @return 反序列化的 {@code Optional}结果，失败返回{@link Optional#empty()}。
     * @param <T> 反序列化结果类型。
     */
    public <T> Optional<T> fromJson(String jsonString, Class<T> valueType) {
        return fromJson(jsonString, valueType, false);
    }
}
