package com.oas.osmsbackend.annotaion.processor;

import com.oas.osmsbackend.annotaion.IsSelf;
import com.oas.osmsbackend.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author askar882
 * @date 2022/05/11
 */
@Slf4j
public class AuthorizationHandlerInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if (handlerMethod.hasMethodAnnotation(IsSelf.class)) {
                log.debug("Intercepted IsSelf for handler {}.", handlerMethod);
                Long userId = getId(request);
                if (userId != null) {
                    log.debug("User ID extracted from request: '{}'.", userId);
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    if (!userId.equals(((User) authentication.getPrincipal()).getId())) {
                        throw new AccessDeniedException("Permission denied.");
                    }
                    log.debug("Granted access.");
                }
            }
        }
        return true;
    }

    private Long getId(HttpServletRequest request) {
        String path = request.getServletPath();
        try {
            return Long.valueOf(path.substring(path.lastIndexOf('/') + 1));
        } catch (NumberFormatException ex) {
            log.debug("No userId extracted from path '{}'.", path);
            return null;
        }
    }
}
