package com.oas.osmsbackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * 应用个性化配置。
 * @author askar882
 * @date 2022/05/10
 */
@Component
@ConfigurationProperties(prefix = "app")
@Validated
@Data
public class AppConfiguration {
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
}
