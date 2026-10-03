package com.smartbus.smartbusapi.controller;

import com.smartbus.smartbusapi.dto.UsuarioDto;
import com.smartbus.smartbusapi.dto.UsuarioRespuestaDto;
import com.smartbus.smartbusapi.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador de Usuario: /api/usuarios
 * Devuelve UsuarioRespuestaDto para no exponer nunca la contraseña.
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // GET /api/usuarios -> lista todos
    @GetMapping
    public List<UsuarioRespuestaDto> listarTodos() {
        return usuarioService.listarTodos();
    }

    // GET /api/usuarios/{id} -> busca uno por id
    @GetMapping("/{id}")
    public UsuarioRespuestaDto buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorId(id);
    }

    // POST /api/usuarios -> crea un usuario (la contraseña se encripta)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioRespuestaDto crear(@RequestBody UsuarioDto dto) {
        return usuarioService.crear(dto);
    }

    // PUT /api/usuarios/{id} -> actualiza (sin contraseña en el JSON = se conserva la actual)
    @PutMapping("/{id}")
    public UsuarioRespuestaDto actualizar(@PathVariable Long id, @RequestBody UsuarioDto dto) {
        return usuarioService.actualizar(id, dto);
    }

    // DELETE /api/usuarios/{id} -> desactiva el usuario
    @DeleteMapping("/{id}")
    public Map<String, String> desactivar(@PathVariable Long id) {
        usuarioService.desactivar(id);
        return Map.of("mensaje", "Usuario desactivado correctamente");
    }
}
