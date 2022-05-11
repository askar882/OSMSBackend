package com.oas.osmsbackend.security;

import com.oas.osmsbackend.annotaion.IsSelf;
import com.oas.osmsbackend.domain.User;
import com.oas.osmsbackend.util.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * FIXME: 其他方式获取{@link HandlerMethod}。
 * @author askar882
 * @date 2022/05/11
 */
//@Component
@Slf4j
public class AuthorizationFilter extends GenericFilterBean {
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HandlerMethod method = RequestUtil.INSTANCE.getHandlerMethod(request);
        if (method != null) {
            if (method.hasMethodAnnotation(IsSelf.class)) {
                String path = request.getServletPath();
                log.debug("path: {}", path);
                Long id = Long.valueOf(path.substring(path.lastIndexOf('/')));
                User user = (User) request.getUserPrincipal();
                if (!id.equals(user.getId())) {
                    SecurityContextHolder.getContext().setAuthentication(null);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }
}
