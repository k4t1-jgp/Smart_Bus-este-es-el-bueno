package com.smartbus.smartbusapi.controller;

import com.smartbus.smartbusapi.dto.RolDto;
import com.smartbus.smartbusapi.model.Rol;
import com.smartbus.smartbusapi.service.RolService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador de Rol: es la "puerta de entrada" HTTP del módulo.
 *
 * Recibe las peticiones (GET, POST, PUT, DELETE) en la ruta /api/roles, se las pasa al
 * servicio y devuelve la respuesta en formato JSON. Aquí NO va lógica de negocio.
 */
@RestController
@RequestMapping("/api/roles")
public class RolController {

    private final RolService rolService;

    // Spring inyecta el servicio automáticamente.
    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    // GET /api/roles -> lista todos
    @GetMapping
    public List<Rol> listarTodos() {
        return rolService.listarTodos();
    }

    // GET /api/roles/{id} -> busca uno por id
    @GetMapping("/{id}")
    public Rol buscarPorId(@PathVariable Long id) {
        return rolService.buscarPorId(id);
    }

    // POST /api/roles -> crea uno nuevo (el JSON viaja en el cuerpo de la petición)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Rol crear(@RequestBody RolDto dto) {
        return rolService.crear(dto);
    }

    // PUT /api/roles/{id} -> actualiza uno existente
    @PutMapping("/{id}")
    public Rol actualizar(@PathVariable Long id, @RequestBody RolDto dto) {
        return rolService.actualizar(id, dto);
    }

    // DELETE /api/roles/{id} -> desactivar
    @DeleteMapping("/{id}")
    public Map<String, String> desactivar(@PathVariable Long id) {
        rolService.desactivar(id);
        return Map.of("mensaje", "Rol desactivado correctamente");
    }
}
