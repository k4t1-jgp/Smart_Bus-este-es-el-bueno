package com.smartbus.smartbusapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Permiso CORS para el front-end.
 *
 * Si las pantallas se abren desde el mismo Spring (http://localhost:8080) esto no hace falta.
 * Pero si alguien abre el front-end con otro servidor (por ejemplo Live Server de VS Code en
 * http://127.0.0.1:5500), el navegador bloquea las peticiones a otro puerto a menos que la API
 * diga explícitamente que las acepta. Esta clase es la que lo dice.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                // Solo para desarrollo: acepta cualquier origen local.
                .allowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
