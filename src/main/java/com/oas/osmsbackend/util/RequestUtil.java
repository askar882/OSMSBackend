package com.oas.osmsbackend.util;

import com.oas.osmsbackend.domain.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;

import static javax.servlet.RequestDispatcher.ERROR_STATUS_CODE;
import static org.springframework.web.servlet.HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE;

/**
 * {@link HttpServletRequest}工具类。
 * @author askar882
 * @date 2022/05/05
 */
public enum RequestUtil {
    /**
     * 单例模式实例。
     */
    INSTANCE;

    /**
     * 从{@link HttpServletRequest}对象获取HTTP状态码，返回对应的{@link HttpStatus}对象。
     * @param request 获取状态的{@link HttpServletRequest}对象。
     * @return 获取的HTTP状态码对应的 {@link HttpStatus}对象。
     */
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

    /**
     * 从{@link HttpServletRequest}对象获取处理该请求的方法{@link HandlerMethod}对象。
     *
     * @param request 获取方法的{@link HttpServletRequest}对象。
     * @return 处理请求的 {@link HandlerMethod}对象。
     */
    public HandlerMethod getHandlerMethod(HttpServletRequest request) {
        return (HandlerMethod) request.getAttribute(BEST_MATCHING_HANDLER_ATTRIBUTE);
    }

    /**
     * TODO: 替代
     * @param id
     * @param principal
     * @return
     */
    public void checkSelf(Long id, Object principal) {
        if (!(principal instanceof User)) {
            return;
        }
        User user = (User) principal;
        if (!id.equals(user.getId())) {
            throw new AccessDeniedException("Access denied.");
        }
    }
}
