package com.oas.osmsbackend.security;

import com.oas.osmsbackend.config.AppConfiguration;
import com.oas.osmsbackend.util.ResponseUtil;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT身份认证过滤器。
 * 继承{@link OncePerRequestFilter}类来表示对一个请求只执行一次，防止{@link Component}被扫描自动添加的最低优先级实例被重新调用。
 *
 * @author askar882
 * @date 3/31/2022
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    /**
     * JWT验证提供器。
     */
    private final JwtTokenProvider jwtTokenProvider;
    /**
     * 应用配置，用于获取忽略的URL列表。
     */
    private final AppConfiguration appConfiguration;

    /**
     * 过滤逻辑。
     *
     * @param request 过滤的{@link HttpServletRequest}对象。
     * @param response 过滤的{@link HttpServletResponse}对象。
     * @param chain 过滤链。
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        log.debug("Filtering request URI: '{}'", request.getRequestURI());
        if (!requiresAuthentication(request)) {
            log.debug("Authentication ignored.");
            chain.doFilter(request, response);
            return;
        }
        String header = request.getHeader(appConfiguration.getAuthHeader());
        if (!StringUtils.hasText(header) || !header.startsWith(appConfiguration.getBearerToken())) {
            ResponseUtil.INSTANCE.writeError(
                    response,
                    HttpStatus.UNAUTHORIZED.value(),
                    "JWT authentication header check failed.");
            return;
        }
        String token = header.substring(7);
        log.info("Extracted token: '{}'", token);
        try {
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            log.debug("Authenticated.");
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (BadCredentialsException | JwtException ex) {
            ResponseUtil.INSTANCE.writeError(
                    response,
                    HttpStatus.UNAUTHORIZED.value(),
                    ex.getMessage()
            );
            log.debug("JWT token authentication failed.", ex);
            return;
        }
        chain.doFilter(request, response);
    }

    /**
     * 检查请求是否需要鉴权。
     *
     * @param request 检查的请求。
     * @return 如需鉴权，返回{@code true}，否则返回{@code false}。
     */
    private boolean requiresAuthentication(HttpServletRequest request) {
        return appConfiguration.getIgnoredUrls().stream().map(matcher -> {
            String pattern = matcher.getPattern();
            HttpMethod method = matcher.getMethod();
            if (method != null) {
                return new AntPathRequestMatcher(pattern, method.name());
            }
            return new AntPathRequestMatcher(pattern);
        }).noneMatch(matcher -> matcher.matches(request));
    }
}
