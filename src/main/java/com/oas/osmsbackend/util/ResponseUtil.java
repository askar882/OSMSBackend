package com.oas.osmsbackend.util;

import com.oas.osmsbackend.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author askar882
 * @date 2022/05/06
 */
@Slf4j
public enum ResponseUtil {
    INSTANCE;

    public void writeError(HttpServletResponse response, int status, String message) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(status)
                .message(message)
                .build();
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        try {
            response.getWriter().write(JsonUtil.INSTANCE.toJson(errorResponse));
            response.getWriter().flush();
        } catch (IOException e) {
            log.error("Error writing response body: {}", errorResponse);
        }
    }
}
