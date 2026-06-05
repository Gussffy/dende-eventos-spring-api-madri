package br.com.softhouse.dende.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Dendê Eventos API")
                        .description("API REST para gerenciamento de usuários, organizadores, eventos e ingressos da plataforma Dendê Eventos")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name(null)
                                .email(null)));
    }
}
