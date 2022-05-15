package com.oas.osmsbackend.security;

import com.oas.osmsbackend.config.AppConfiguration;
import com.oas.osmsbackend.domain.User;
import com.oas.osmsbackend.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * JWT验证提供器。
 *
 * @author askar882
 * @date 2022/03/31
 */
@Component
@Slf4j
public class JwtTokenProvider {
    private final UserRepository userRepository;
    private final SecretKey secretKey;
    private final AppConfiguration appConfiguration;

    public JwtTokenProvider(UserRepository userRepository, AppConfiguration appConfiguration) {
        this.userRepository = userRepository;
        this.appConfiguration = appConfiguration;
        String secret = Base64.getEncoder().encodeToString(appConfiguration.getJwtSecret().getBytes());
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成Token。
     *
     * @param authentication 生成Token的{@link Authentication}实例。
     * @return 生成的Token。
     */
    public String createToken(Authentication authentication) {
        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Claims claims = Jwts.claims().setSubject(username);
        if (!authorities.isEmpty()) {
            claims.put(appConfiguration.getAuthoritiesKey(),
                    authorities.stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.joining(","))
            );
        }
        Date validity = new Date(System.currentTimeMillis() + Duration.ofSeconds(appConfiguration.getTokenValidity()).toMillis());
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(validity)
                .setIssuer(appConfiguration.getIssuer())
                .signWith(this.secretKey, appConfiguration.getJwtAlgorithm())
                .compact();
    }

    /**
     * 通过Token返回对应的{@link Authentication}实例。
     *
     * @param token 转换的Token。
     * @return Token对应的 {@link Authentication}实例。
     * @throws BadCredentialsException Token验证失败时抛出。
     */
    public Authentication getAuthentication(String token) throws BadCredentialsException {
        if (!validateToken(token)) {
            throw new BadCredentialsException("JWT token validation failed.");
        }
        Claims claims = Jwts.parserBuilder().setSigningKey(this.secretKey).build().parseClaimsJws(token).getBody();
        Object authoritiesClaim = claims.get(appConfiguration.getAuthoritiesKey());
        Collection<? extends GrantedAuthority> authorities = authoritiesClaim == null ? AuthorityUtils.NO_AUTHORITIES
                : AuthorityUtils.commaSeparatedStringToAuthorityList(authoritiesClaim.toString());
        User principal = userRepository.findOne(Example.of(User.builder()
                        .username(claims.getSubject())
                        .roles(authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                        .creationTime(null)
                        .build()))
                .orElseThrow(() -> new BadCredentialsException("Bad JWT token."));
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    /**
     * 验证Token。
     *
     * @param token 待验证的Token。
     * @return 验证成功返回 {@code true}，否则返回{@code false}。
     */
    private boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(this.secretKey).build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        }
        return false;
    }
}
