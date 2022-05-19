package com.oas.osmsbackend.handler;

import com.fasterxml.jackson.core.JacksonException;
import com.oas.osmsbackend.exception.ResourceNotFoundException;
import com.oas.osmsbackend.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.persistence.EntityExistsException;
import javax.security.auth.login.AccountNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.stream.Collectors;

/**
 * 自定义错误处理器。
 *
 * @author askar882
 * @date 2022/04/20
 */
@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {
    /**
     * 处理身份验证错误，返回{@link ErrorResponse}。
     *
     * @param request 请求。
     * @param ex 抛出的异常。
     * @return {@link ErrorResponse}实例。
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    public ErrorResponse authenticationError(HttpServletRequest request, AuthenticationException ex) {
        logException(request, ex);
        return new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
    }

    /**
     * 处理请求错误，要求的请求体未提供，返回{@link ErrorResponse}。
     *
     * @param request 请求。
     * @return {@link ErrorResponse}实例。
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ErrorResponse requestBodyMissing(HttpServletRequest request, Throwable ex) {
        logException(request, ex);
        String msg = "Required request body is missing or invalid.";
        if (NestedExceptionUtils.getRootCause(ex) instanceof JacksonException
                || ex instanceof MethodArgumentTypeMismatchException) {
            msg = "Invalid JSON data.";
        }
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), msg);
    }

    /**
     * 处理未分类的错误，返回{@link ErrorResponse}。
     *
     * @param request 请求。
     * @param ex 抛出的异常。
     * @return {@link ErrorResponse}实例。
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Throwable.class)
    public ErrorResponse globalException(HttpServletRequest request, Throwable ex) {
        logException(request, ex);
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    /**
     * 处理资源冲突错误，返回{@link ErrorResponse}。
     *
     * @param request 请求。
     * @param ex 抛出的异常。
     * @return {@link ErrorResponse}实例。
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EntityExistsException.class)
    public ErrorResponse entityExists(HttpServletRequest request, Throwable ex) {
        logException(request, ex);
        return new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
    }

    /**
     * 处理授权验证错误，返回{@link ErrorResponse}。
     *
     * @param request 请求。
     * @param ex 抛出的异常。
     * @return {@link ErrorResponse}实例。
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ErrorResponse accessDenied(HttpServletRequest request, Throwable ex) {
        logException(request, ex);
        return new ErrorResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage());
    }

    /**
     * 处理资源未找到错误，返回{@link ErrorResponse}。
     *
     * @param request 请求。
     * @param ex 抛出的异常。
     * @return {@link ErrorResponse}实例。
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            // 账号未找到，手动抛出
            AccountNotFoundException.class,
            // 删除不存在的资源时JPA抛出的异常
            EmptyResultDataAccessException.class,
            // 资源未找到异常
            ResourceNotFoundException.class})
    public ErrorResponse notFound(HttpServletRequest request, Throwable ex) {
        logException(request, ex);
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    /**
     * 处理SQL验证错误，包括空值验证、唯一值验证等抛出的异常。
     *
     * @param request 请求。
     * @param ex 抛出的异常。
     * @return {@link ErrorResponse}实例。
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({TransactionSystemException.class, DataIntegrityViolationException.class})
    public ErrorResponse validationException(HttpServletRequest request, Throwable ex) {
        logException(request, ex);
        Throwable rootCause = NestedExceptionUtils.getRootCause(ex);
        if (rootCause == null) {
            rootCause = ex;
        }
        String msg = rootCause.getMessage();
        if (rootCause instanceof ConstraintViolationException) {
            msg = "Validation failed for field(s): '{"
                    + ((ConstraintViolationException) rootCause).getConstraintViolations().stream()
                    .map(ConstraintViolation::getPropertyPath)
                    .map(Path::toString)
                    .collect(Collectors.joining(", "))
                    + "}.";
        }
        log.debug(msg);
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), msg);
    }

    /**
     * 打日志。
     *
     * @param request 请求。
     * @param ex 抛出的异常。
     */
    private void logException(HttpServletRequest request, Throwable ex) {
        log.debug("Exception '{}' for URI '{}'.", ex, request.getRequestURI());
        log.debug("Exception:", ex);
    }
}
