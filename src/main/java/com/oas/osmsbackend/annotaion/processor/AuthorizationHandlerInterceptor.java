package com.oas.osmsbackend.annotaion.processor;

import com.oas.osmsbackend.annotaion.CurrentUser;
import com.oas.osmsbackend.annotaion.HasRole;
import com.oas.osmsbackend.entity.User;
import com.oas.osmsbackend.enums.Role;
import com.oas.osmsbackend.util.JsonUtil;
import com.oas.osmsbackend.util.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;

/**
 * 自定义拦截器，用于实现权限验证。
 *
 * @author askar882
 * @date 2022/05/11
 */
@Slf4j
public class AuthorizationHandlerInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if (handleHasRole(handlerMethod)) {
                return true;
            }
            if (handleCurrentUser(handlerMethod, request)) {
                log.debug("Current user check successful.");
            }
        }
        return true;
    }

    /**
     * 处理被{@link HasRole}标注的类或对象，验证当前用户是否有对应角色。
     * 未被标注返回{@code false}，被标注并授权成功返回{@code true}，授权失败抛出异常。
     *
     * @param method 处理请求的方法。
     * @return 被标注并授权成功返回 {@code true}，否则返回{@code false}。
     * @throws AccessDeniedException 用户没有要求的角色，无权访问。
     */
    private boolean handleHasRole(HandlerMethod method) throws AccessDeniedException {
        HasRole hasRole = null;
        if (method.getBeanType().isAnnotationPresent(HasRole.class)) {
            hasRole = method.getBeanType().getAnnotation(HasRole.class);
        }
        if (method.hasMethodAnnotation(HasRole.class)) {
            hasRole = method.getMethodAnnotation(HasRole.class);
        }
        if (hasRole == null) {
            return false;
        }
        Role[] roles = hasRole.value();
        User currentUser = RequestUtil.INSTANCE.currentUser();
        if (Arrays.stream(roles).noneMatch(role -> currentUser.getRoles().contains(role))) {
            log.debug("User '{}' has none of roles required {}.", currentUser.getUsername(), roles);
            if (method.hasMethodAnnotation(CurrentUser.class)) {
                log.debug("Falling back to current user check.");
                return false;
            }
            throw new AccessDeniedException("未授权的角色");
        }
        log.debug("User '{}' has required role(s) '{}'.", currentUser.getUsername(), roles);
        return true;
    }

    /**
     * 处理被{@link CurrentUser}标注的类或对象，验证请求的用户ID当前用户是否是。
     * 未被标注时返回{@code false}，被标注并验证成功返回{@code true}，验证失败抛出异常。
     *
     * @param method 处理请求的方法。
     * @param request 请求。
     * @return 被标注并验证成功返回 {@code true}，否则返回{@code false}。
     * @throws AccessDeniedException 非当前用户，无权访问。
     */
    private boolean handleCurrentUser(HandlerMethod method, HttpServletRequest request) throws AccessDeniedException {
        if (method.hasMethodAnnotation(CurrentUser.class)) {
            Optional<Long> userId = getUserId(request);
            if (userId.isPresent()) {
                log.debug("User ID extracted from request: '{}'.", userId);
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (!userId.get().equals(((User) authentication.getPrincipal()).getId())) {
                    throw new AccessDeniedException("未授权的用户");
                }
                log.debug("Granted access.");
                return true;
            }
            log.debug("Failed to extract user ID.");
            throw new AccessDeniedException("无法验证用户身份");
        }
        return false;
    }

    /**
     * 从请求体或参数获取用户ID。
     *
     * @param request 请求。
     * @return 用户ID。
     */
    private Optional<Long> getUserId(HttpServletRequest request) {
        String path = request.getServletPath();
        try {
            return Optional.of(Long.valueOf(path.substring(path.lastIndexOf('/') + 1)));
        } catch (NumberFormatException ex) {
            log.debug("No userId extracted from path '{}'.", path);
        }
        Optional<String> content = RequestUtil.INSTANCE.readContent(request);
        if (!content.isPresent() || !StringUtils.hasLength(content.get())) {
            log.debug("Empty request body.");
            return Optional.empty();
        }
        return JsonUtil.INSTANCE.fromJson(content.get(), User.class)
                .map(User::getId);
    }
}
