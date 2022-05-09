package com.oas.osmsbackend.security;

import com.oas.osmsbackend.util.Constants;
import com.oas.osmsbackend.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

/**
 * @author askar882
 * @date 3/31/2022
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        log.debug("Filtering request URI: '{}'", request.getRequestURI());
        if (!requiresAuthentication(request, response, chain)) {
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

    private boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        try {
            Field additionalFiltersField = chain.getClass().getDeclaredField("additionalFilters");
            additionalFiltersField.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<Filter> additionalFilters = (List<Filter>) additionalFiltersField.get(chain);
            FilterSecurityInterceptor interceptor = (FilterSecurityInterceptor) additionalFilters.get(additionalFilters.size() - 1);
            Collection<ConfigAttribute> attributes = interceptor.obtainSecurityMetadataSource()
                    .getAttributes(new FilterInvocation(request, response, chain));
            log.debug("attributes: {}", attributes.toString());
            return attributes.stream().noneMatch(attribute -> "permitAll".equals(attribute.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
