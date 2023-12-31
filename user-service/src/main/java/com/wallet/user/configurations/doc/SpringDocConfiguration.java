package com.wallet.user.configurations.doc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SpringDocConfiguration {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .components(getApiSecurityComponents())
            .info(getApiInfo());
    }

    private Components getApiSecurityComponents() {
        return new Components()
            .addSecuritySchemes("bearer-key", 
                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").scheme("JWT"));
    }

    private Info getApiInfo() {
        return new Info()
            .title("Wallet API - User Service")
            .description("Backend para aplicativo de carteira digital - User Service")
            .version("0.0.1-SNAPSHOT")
            .contact(new Contact()
                .email("vitor.augsilva98@gmail.com")
                .name("Vitor Augusto Silva")
                .url("https://github.com/vitorsilva98/"));
    }
}
