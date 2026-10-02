package com.smartbus.smartbusapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuración de la encriptación de contraseñas.
 *
 * BCrypt convierte "1234" en un texto como "$2a$10$Xk..." que NO se puede volver a convertir en la
 * contraseña original. Para revisar un login se compara la contraseña escrita contra ese texto
 * con passwordEncoder.matches(...).
 */
@Configuration
public class SeguridadConfig {

    // @Bean = "Spring, guarda este objeto y entrégalo a quien lo pida" (por ejemplo, a UsuarioService).
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
