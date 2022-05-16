package com.oas.osmsbackend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author askar882
 * @date 2022/05/13
 */
@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {
    @Bean
    public OpenAPI openAPI(AppConfiguration appConfiguration) {
        return new OpenAPI()
                .info(new Info()
                        .title("OSMS API")
                        .version(appConfiguration.getVersion())
                        .description(appConfiguration.getDescription() + "API文档"));
    }
}
