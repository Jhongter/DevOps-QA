package org.example.grupo_3_atdd.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Libera o front-end (Vite, rodando em localhost:5173) a chamar a API em
 * localhost:8081 (ou outra porta configurada) sem ser bloqueado pelo CORS
 * do navegador. Sem isso, toda chamada do frontend falha silenciosamente
 * como "sem resposta do servidor", mesmo com o backend no ar.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}