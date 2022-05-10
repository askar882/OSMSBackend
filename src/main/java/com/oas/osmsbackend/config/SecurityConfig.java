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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutFilter;

/**
 * @author askar882
 * @date 2022/04/01
 */
@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    private final AppConfiguration appConfiguration;

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
            String url = matcher.getUrl();
            HttpMethod method = matcher.getMethod();
            log.debug("Ignored {} request on '{}'.", method == null ? "all" : method.name(), url);
            if (method != null) {
                registry.antMatchers(method, url).permitAll();
            } else {
                registry.antMatchers(url).permitAll();
            }
        }
        registry.anyRequest().authenticated();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username '" + username + "' not found."));
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder encoder) {
        return authentication -> {
            String username = authentication.getPrincipal().toString();
            String password = authentication.getCredentials().toString();
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (!encoder.matches(password, userDetails.getPassword())) {
                throw new BadCredentialsException("Bad credentials.");
            }

            if (!userDetails.isEnabled()) {
                throw new DisabledException("User is disabled.");
            }
            return new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities());
        };
    }
}
