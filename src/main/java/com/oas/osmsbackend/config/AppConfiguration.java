package com.oas.osmsbackend.config;

import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * 应用个性化配置。
 *
 * @author askar882
 * @date 2022/05/10
 */
@Component
@ConfigurationProperties(prefix = "app")
@Validated
@Getter
@Setter
public class AppConfiguration {
    /**
     * 项目版本。
     */
    private String version;

    /**
     * 项目说明。
     */
    private String description;

    /**
     * 无需验证身份的资源列表。
     */
    @Valid
    private List<MatcherConfig> ignoredUrls = new ArrayList<>();

    /**
     * URL资源匹配配置类。
     * TODO: 使用{@link AntPathRequestMatcher}代替。
     */
    @Data
    public static class MatcherConfig {
        /**
         * 匹配的URL，不可为空。
         */
        @NotNull
        private String pattern;
        /**
         * 匹配的HTTP方法。
         */
        private HttpMethod method;
    }

    /**
     * JWT加密算法，默认使用{@link SignatureAlgorithm#HS512}。
     */
    private SignatureAlgorithm jwtAlgorithm = SignatureAlgorithm.HS512;

    /**
     * JWT Token签发人。
     */
    private String issuer = "askar882";

    /**
     * 身份认证Header名称。
     */
    private String authHeader = "Authorization";

    /**
     * Token开头。
     */
    private String bearerToken = "Bearer ";

    /**
     * 用户权限存储键名。
     */
    private String authoritiesKey = "roles";

    /**
     * JWT Token有效时间，单位秒。
     */
    @NotNull
    private Long tokenValidity;

    /**
     * JWT加密密钥。
     */
    @NotNull
    private String jwtSecret;

    /**
     * 设置JWT密钥时使用{@link Sha512DigestUtils}进行加密。
     *
     * @param jwtSecret JWT明文密钥。
     */
    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = Sha512DigestUtils.shaHex(jwtSecret);
    }
}
