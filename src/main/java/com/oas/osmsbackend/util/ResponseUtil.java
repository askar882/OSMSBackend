package com.oas.osmsbackend.util;

import com.oas.osmsbackend.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * {@link HttpServletResponse}工具类。
 * @author askar882
 * @date 2022/05/06
 */
@Slf4j
public enum ResponseUtil {
    /**
     * 单例模式实例。
     */
    INSTANCE;

    /**
     * 向{@link HttpServletResponse}对象写入错误消息。
     * @param response 写入消息的{@link HttpServletResponse}对象。
     * @param status HTTP状态码。
     * @param message 写入的消息。
     */
    public void writeError(HttpServletResponse response, int status, String message) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(status)
                .message(message)
                .build();
        writeMessage(response, status, JsonUtil.INSTANCE.toJson(errorResponse));
    }

    /**
     * 向{@link HttpServletResponse}对象写入消息。
     * @param response 写入消息的{@link HttpServletResponse}对象。
     * @param status HTTP状态码。
     * @param message 写入的消息。
     */
    public void writeMessage(HttpServletResponse response, int status, String message) {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        try {
            response.getWriter().write(message);
            response.getWriter().flush();
        } catch (IOException e) {
            log.error("Error writing response body: {}", message);
        }
    }
}
