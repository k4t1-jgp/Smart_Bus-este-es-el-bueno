package com.smartbus.smartbusapi.controller;

import com.smartbus.smartbusapi.dto.AsignacionDto;
import com.smartbus.smartbusapi.model.Asignacion;
import com.smartbus.smartbusapi.service.AsignacionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador de Asignacion: /api/asignaciones
 * Vincula estudiantes con una ruta y una parada.
 */
@RestController
@RequestMapping("/api/asignaciones")
public class AsignacionController {

    private final AsignacionService asignacionService;

    public AsignacionController(AsignacionService asignacionService) {
        this.asignacionService = asignacionService;
    }

    // GET /api/asignaciones                    -> todas
    // GET /api/asignaciones?rutaId=1           -> estudiantes activos de una ruta
    // GET /api/asignaciones?estudianteId=3     -> historial de un estudiante
    @GetMapping
    public List<Asignacion> listar(@RequestParam(required = false) Long rutaId,
                                   @RequestParam(required = false) Long estudianteId) {
        return asignacionService.listar(rutaId, estudianteId);
    }

    // GET /api/asignaciones/{id}
    @GetMapping("/{id}")
    public Asignacion buscarPorId(@PathVariable Long id) {
        return asignacionService.buscarPorId(id);
    }

    // POST /api/asignaciones -> asigna un estudiante a una ruta y parada
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Asignacion crear(@RequestBody AsignacionDto dto) {
        return asignacionService.crear(dto);
    }

    // PUT /api/asignaciones/{id} -> cambia la ruta o la parada
    @PutMapping("/{id}")
    public Asignacion actualizar(@PathVariable Long id, @RequestBody AsignacionDto dto) {
        return asignacionService.actualizar(id, dto);
    }

    // DELETE /api/asignaciones/{id} -> cancela la asignación (queda como historial)
    @DeleteMapping("/{id}")
    public Map<String, String> cancelar(@PathVariable Long id) {
        asignacionService.cancelar(id);
        return Map.of("mensaje", "Asignación cancelada correctamente");
    }
}
