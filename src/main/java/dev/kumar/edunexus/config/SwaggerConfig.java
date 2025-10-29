package dev.kumar.edunexus.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Development"),
                        new Server().url("http://68.233.96.179:8000").description("Oracle Cloud Production")
                ))
                .info(new Info()
                        .title("EduNexus API")
                        .description("Educational platform API for duolingo alternative. Get Firebase JWT token from [token.kumarwhocodes.com](https://token.kumarwhocodes.com)")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Kumar Sambhav")
                                .email("sambhav26k@gmail.com")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter Firebase JWT token. Get token from: https://token.kumarwhocodes.me")));
    }
}