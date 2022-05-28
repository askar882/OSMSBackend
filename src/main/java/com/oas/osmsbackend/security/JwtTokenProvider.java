package com.oas.osmsbackend.security;

import com.oas.osmsbackend.config.AppConfiguration;
import com.oas.osmsbackend.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
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
    private final SecretKey secretKey;
    private final AppConfiguration appConfiguration;
    private final RedisStore redisStore;

    public JwtTokenProvider(AppConfiguration appConfiguration, RedisStore redisStore) {
        this.appConfiguration = appConfiguration;
        this.redisStore = redisStore;
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
        User user = (User) authentication.getDetails();
        Claims claims = Jwts.claims().setSubject(username);
        if (!authorities.isEmpty()) {
            claims.put(appConfiguration.getAuthoritiesKey(),
                    authorities.stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.joining(","))
            );
        }
        claims.put("id", user.getId());
        Date validity = new Date(System.currentTimeMillis() + appConfiguration.getTokenValidity().toMillis());
        String token = Jwts.builder()
                .setClaims(claims)
                .setExpiration(validity)
                .setIssuer(appConfiguration.getIssuer())
                .signWith(this.secretKey, appConfiguration.getJwtAlgorithm())
                .compact();
        redisStore.saveToken(user, token);
        return token;
    }

    /**
     * 通过Token返回对应的{@link Authentication}实例。
     *
     * @param token 转换的Token。
     * @return Token对应的 {@link Authentication}实例。
     * @throws JwtException Token不在Redis存储时抛出。
     */
    public Authentication getAuthentication(String token) throws JwtException {
        User principal = redisStore.getUser(token)
                .orElseThrow(() -> new JwtException("Invalid token"));
        return new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
    }
}
