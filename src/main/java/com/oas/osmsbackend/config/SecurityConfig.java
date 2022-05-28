package com.oas.osmsbackend.config;

import com.oas.osmsbackend.repository.UserRepository;
import com.oas.osmsbackend.security.JwtAuthenticationTokenFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutFilter;

/**
 * 安全配置。
 *
 * @author askar882
 * @date 2022/04/01
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    private final AppConfiguration appConfiguration;

    /**
     * 配置安全设置。
     *
     * @param http 待配置的{@link HttpSecurity}实例。
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().exceptionHandling()
                    .authenticationEntryPoint(authenticationEntryPoint)
                .and().addFilterBefore(jwtAuthenticationTokenFilter, LogoutFilter.class);
        var registry = http.authorizeRequests();
        for (var matcher : appConfiguration.getIgnoredUrls()) {
            String pattern = matcher.getPattern();
            HttpMethod method = matcher.getMethod();
            log.debug("Ignored {} request on '{}'.", method == null ? "all" : method.name(), pattern);
            if (method != null) {
                registry.antMatchers(method, pattern).permitAll();
            } else {
                registry.antMatchers(pattern).permitAll();
            }
        }
        registry.anyRequest().authenticated();
    }

    /**
     * 使用{@link PasswordEncoderFactories#createDelegatingPasswordEncoder()}方法获取密钥加密器，并设置为默认密钥加密器。
     *
     * @return {@link PasswordEncoder}实例。
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * 加载用户数据的服务，通过{@link UserRepository}实现获取用户数据。
     * {@link AuthenticationManager}依赖于此类实现获取用户数据。
     *
     * @param userRepository {@link UserRepository}实例，用于获取用户数据。
     * @return {@link UserDetailsService}实例。
     */
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户 '" + username + "' 不存在"));
    }
}
