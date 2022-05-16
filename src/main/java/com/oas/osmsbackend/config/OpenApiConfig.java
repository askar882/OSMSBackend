package com.oas.osmsbackend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author askar882
 * @date 2022/05/13
 */
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openAPI(
            @Value("${app.description:OSMS project.}") String description,
            @Value("${app.version:1.0.0}") String version
            ) {
        return new OpenAPI()
                .info(new Info()
                        .title("OSMS API")
                        .version(version)
                        .description(description + "API文档"));
    }
}
