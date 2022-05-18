package com.oas.osmsbackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.oas.osmsbackend.annotaion.processor.AuthorizationHandlerInterceptor;
import com.oas.osmsbackend.handler.ErrorReport;
import com.oas.osmsbackend.response.DataResponse;
import com.oas.osmsbackend.response.ErrorResponse;
import lombok.var;
import org.apache.catalina.Container;
import org.apache.catalina.core.StandardHost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring Web配置。
 *
 * @author askar882
 * @date 2022/04/07
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    /**
     * 设置默认响应内容类型为JSON。
     *
     * @param configurer {@link ContentNegotiationConfigurer}实例。
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.ignoreAcceptHeader(true);
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }

    /**
     * 添加自定义Handler拦截器，实现权限验证。
     *
     * @param registry 拦截器注册表。
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthorizationHandlerInterceptor());
    }

    /**
     * 自定义Tomcat默认错误页。
     *
     * @return Tomcat错误页自定义Bean。
     */
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> errorValveCustomizer() {
        return (factory) -> factory.addContextCustomizers((context -> {
            Container parent = context.getParent();
            if (parent instanceof StandardHost) {
                ((StandardHost) parent).setErrorReportValveClass(ErrorReport.class.getName());
            }
        }));
    }

    /**
     * 自定义{@link MappingJackson2HttpMessageConverter}，注册自定义的{@link ObjectMapper}。
     *
     * @param defaultMapper 自动生成的默认{@link ObjectMapper}。
     * @return 新的 {@link MappingJackson2HttpMessageConverter}实例。
     */
    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(@Autowired ObjectMapper defaultMapper) {
        var converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(defaultMapper);
        var objectMapper = new ObjectMapper().enable(SerializationFeature.WRAP_ROOT_VALUE);
        converter.registerObjectMappersForType(ErrorResponse.class, m -> m.put(MediaType.APPLICATION_JSON, objectMapper));
        converter.registerObjectMappersForType(DataResponse.class, m -> m.put(MediaType.APPLICATION_JSON, objectMapper));
        return converter;
    }
}
