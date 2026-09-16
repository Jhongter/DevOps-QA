package org.example.grupo_3_atdd.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI().info(new Info()
                .title("Gamificação para Educação Continuada - API")
                .description("Grupo 3 - ATDD (BDD + TDD). Regra da US: aluno com curso finalizado "
                        + "e média >= 7,0 tem direito a mais 3 cursos.")
                .version("v1"));
    }
}
