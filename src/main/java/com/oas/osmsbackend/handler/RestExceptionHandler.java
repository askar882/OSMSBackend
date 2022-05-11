package com.oas.osmsbackend.handler;

import com.oas.osmsbackend.response.ErrorResponse;
import com.oas.osmsbackend.util.JsonUtil;
import com.oas.osmsbackend.util.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

import javax.persistence.EntityExistsException;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author askar882
 * @date 2022/04/20
 */
@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    public ErrorResponse handle401(AuthenticationException ex) {
        log.debug("Authentication failed: {}", ex.getMessage());
        return new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorResponse requestBodyMissing(HttpServletRequest request) {
        HandlerMethod method = RequestUtil.INSTANCE.getHandlerMethod(request);
        String requestBody = Arrays.stream(method.getMethodParameters())
                .map(m -> JsonUtil.INSTANCE.toJson(m.getParameterType()) + " " + m.getParameterName())
                .collect(Collectors.joining(","));
        String msg = "Required request body is missing: " + requestBody;
        log.debug(msg);
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), msg);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Throwable.class)
    public ErrorResponse globalException(HttpServletRequest request, Throwable ex) {
        log.debug("Exception '{}' for URI '{}'.", ex.getMessage(), request.getRequestURI());
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EntityExistsException.class)
    public ErrorResponse entityExists(HttpServletRequest request, Throwable ex) {
        log.debug("Exception '{}' for URI '{}'.", ex.getMessage(), request.getRequestURI());
        return new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ErrorResponse accessDenied(HttpServletRequest request, Throwable ex) {
        log.debug("Access denied('{}') for URI '{}'.", ex.getMessage(), request.getRequestURI());
        return new ErrorResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage());
    }
}