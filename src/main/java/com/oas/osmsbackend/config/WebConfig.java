package com.oas.osmsbackend.config;

import org.apache.catalina.Container;
import org.apache.catalina.core.StandardHost;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author askar882
 * @date 2022/04/07
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.ignoreAcceptHeader(true);
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> errorValveCustomizer() {
        return (factory) -> {
            factory.addContextCustomizers((context -> {
                Container parent = context.getParent();
                if (parent instanceof StandardHost) {
                    ((StandardHost) parent).setErrorReportValveClass("com.oas.osmsbackend.handler.ErrorReport");
                }
            }));
        };
    }
}
