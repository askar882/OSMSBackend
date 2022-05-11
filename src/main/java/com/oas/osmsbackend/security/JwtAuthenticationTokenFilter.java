package com.oas.osmsbackend.security;

import com.oas.osmsbackend.config.AppConfiguration;
import com.oas.osmsbackend.util.Constants;
import com.oas.osmsbackend.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
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
        String header = request.getHeader(Constants.AUTHORIZATION_HEADER);
        if (!StringUtils.hasText(header) || !header.startsWith(Constants.BEARER_TOKEN)) {
            ResponseUtil.INSTANCE.writeError(
                    response,
                    401,
                    "JWT authentication header check failed.");
            return;
        }
        String token = header.substring(7);
        log.info("Extracted token: '{}'", token);
        if (jwtTokenProvider.validateToken(token)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }

    /**
     * 检查请求是否需要鉴权。
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
        }).anyMatch(matcher -> matcher.matches(request));
    }
}
