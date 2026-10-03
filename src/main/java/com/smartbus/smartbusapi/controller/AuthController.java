package com.smartbus.smartbusapi.controller;

import com.smartbus.smartbusapi.dto.LoginDto;
import com.smartbus.smartbusapi.dto.UsuarioRespuestaDto;
import com.smartbus.smartbusapi.service.UsuarioService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Inicio de sesión: POST /api/auth/login
 *
 * Verifica usuario y contraseña y devuelve los datos del usuario con su rol.
 * OJO: por ahora solo AUTENTICA; todavía no bloquea endpoints según el rol
 * (el control de acceso por roles queda como mejora futura, ver README).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public UsuarioRespuestaDto login(@RequestBody LoginDto login) {
        return usuarioService.iniciarSesion(login);
    }
}
