package com.oas.osmsbackend.util;

import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.token.Sha512DigestUtils;

/**
 * @author askar882
 * @date 2022/03/31
 */
@Configuration
public class Constants {
    public static final SignatureAlgorithm JWT_ALGO = SignatureAlgorithm.HS512;
    public static final String JWT_SECRET = Sha512DigestUtils.shaHex("secret");
    public static final long JWT_TOKEN_VALIDITY = 3600;
    public static final String ISSUER = "askar882";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_TOKEN = "Bearer ";
    public static final String AUTHORITIES_KEY = "roles";
}
