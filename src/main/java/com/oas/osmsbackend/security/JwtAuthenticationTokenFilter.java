package com.oas.osmsbackend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oas.osmsbackend.response.ErrorResponse;
import com.oas.osmsbackend.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author askar882
 * @date 3/31/2022
 */
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationTokenFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain filterChain)
            throws AuthenticationException, IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        log.debug("Filtering URL: '{}', servletPath: '{}'",
                httpServletRequest.getRequestURI(),
                httpServletRequest.getServletPath());
        if ("/auth/login".equals(httpServletRequest.getServletPath())) {
            filterChain.doFilter(request, response);
            return;
        }
        String header = httpServletRequest.getHeader(Constants.AUTHORIZATION_HEADER);
        if (!StringUtils.hasText(header) || !header.startsWith(Constants.BEARER_TOKEN)) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .status(401)
                    .message("JWT authentication header check failed.")
                    .build();
            httpServletResponse.setContentType("application/json");
            httpServletResponse.setStatus(401);
            httpServletResponse.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
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
        filterChain.doFilter(request, response);
    }
}
