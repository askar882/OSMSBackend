package com.oas.osmsbackend.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;

import static javax.servlet.RequestDispatcher.ERROR_STATUS_CODE;
import static org.springframework.web.servlet.HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE;

/**
 * @author askar882
 * @date 2022/05/05
 */
public enum RequestUtil {
    INSTANCE;

    public HttpStatus getErrorStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute(ERROR_STATUS_CODE);
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        try {
            return HttpStatus.valueOf(statusCode);
        } catch (Exception ex) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    public HandlerMethod getHandlerMethod(HttpServletRequest request) {
        return (HandlerMethod) request.getAttribute(BEST_MATCHING_HANDLER_ATTRIBUTE);
    }
}
