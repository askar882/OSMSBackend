package com.oas.osmsbackend.security;

import com.oas.osmsbackend.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 身份验证管理器，用于验证用户名密码。
 *
 * @author askar882
 * @date 2022/05/18
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationManager implements AuthenticationManager {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 验证用户名密码。
     *
     * @param authentication 包含用户名密码的{@link Authentication}实例。
     * @return 鉴权成功，包含用户名、用户ID和角色列表的{@link Authentication}实例。
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getPrincipal().toString();
        String password = authentication.getCredentials().toString();
        User user = (User) userDetailsService.loadUserByUsername(username);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.debug("Provided password doesn't match for user '{}'.", username);
            throw new BadCredentialsException("Bad credentials.");
        }
        if (!user.isEnabled()) {
            throw new DisabledException("User is disabled.");
        }
        var authenticationToken = new UsernamePasswordAuthenticationToken(username, null, user.getAuthorities());
        authenticationToken.setDetails(user.getId());
        return authenticationToken;
    }
}
