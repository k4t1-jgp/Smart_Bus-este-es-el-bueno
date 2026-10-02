package com.smartbus.smartbusapi.controller;

import com.smartbus.smartbusapi.dto.RutaDto;
import com.smartbus.smartbusapi.model.Ruta;
import com.smartbus.smartbusapi.service.RutaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador de Ruta: /api/rutas
 * Además del CRUD tiene el endpoint para ACTIVAR una ruta.
 */
@RestController
@RequestMapping("/api/rutas")
public class RutaController {

    private final RutaService rutaService;

    public RutaController(RutaService rutaService) {
        this.rutaService = rutaService;
    }

    // GET /api/rutas -> lista todas
    @GetMapping
    public List<Ruta> listarTodos() {
        return rutaService.listarTodos();
    }

    // GET /api/rutas/{id} -> busca una por id
    @GetMapping("/{id}")
    public Ruta buscarPorId(@PathVariable Long id) {
        return rutaService.buscarPorId(id);
    }

    // POST /api/rutas -> crea una ruta (queda INACTIVA)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Ruta crear(@RequestBody RutaDto dto) {
        return rutaService.crear(dto);
    }

    // PUT /api/rutas/{id} -> actualiza los datos de la ruta
    @PutMapping("/{id}")
    public Ruta actualizar(@PathVariable Long id, @RequestBody RutaDto dto) {
        return rutaService.actualizar(id, dto);
    }

    // PUT /api/rutas/{id}/activar -> activa la ruta si cumple todas las reglas
    @PutMapping("/{id}/activar")
    public Ruta activar(@PathVariable Long id) {
        return rutaService.activar(id);
    }

    // DELETE /api/rutas/{id} -> desactiva la ruta (nunca se borra)
    @DeleteMapping("/{id}")
    public Map<String, String> desactivar(@PathVariable Long id) {
        rutaService.desactivar(id);
        return Map.of("mensaje", "Ruta desactivada correctamente");
    }
}
