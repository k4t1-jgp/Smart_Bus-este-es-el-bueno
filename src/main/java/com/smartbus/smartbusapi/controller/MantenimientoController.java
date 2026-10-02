package com.smartbus.smartbusapi.controller;

import com.smartbus.smartbusapi.dto.MantenimientoDto;
import com.smartbus.smartbusapi.model.Mantenimiento;
import com.smartbus.smartbusapi.service.MantenimientoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador de Mantenimiento: /api/mantenimientos
 * No tiene DELETE a propósito: un mantenimiento se FINALIZA, no se borra.
 */
@RestController
@RequestMapping("/api/mantenimientos")
public class MantenimientoController {

    private final MantenimientoService mantenimientoService;

    public MantenimientoController(MantenimientoService mantenimientoService) {
        this.mantenimientoService = mantenimientoService;
    }

    // GET /api/mantenimientos                  -> todos
    // GET /api/mantenimientos?vehiculoId=2     -> historial de un vehículo
    @GetMapping
    public List<Mantenimiento> listar(@RequestParam(required = false) Long vehiculoId) {
        return mantenimientoService.listar(vehiculoId);
    }

    // GET /api/mantenimientos/{id}
    @GetMapping("/{id}")
    public Mantenimiento buscarPorId(@PathVariable Long id) {
        return mantenimientoService.buscarPorId(id);
    }

    // POST /api/mantenimientos -> registra un mantenimiento (queda en curso)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mantenimiento registrar(@RequestBody MantenimientoDto dto) {
        return mantenimientoService.registrar(dto);
    }

    // PUT /api/mantenimientos/{id} -> corrige tipo, detalle, costo o fecha de inicio
    @PutMapping("/{id}")
    public Mantenimiento actualizar(@PathVariable Long id, @RequestBody MantenimientoDto dto) {
        return mantenimientoService.actualizar(id, dto);
    }

    // PUT /api/mantenimientos/{id}/finalizar -> termina el mantenimiento (fecha fin = hoy)
    @PutMapping("/{id}/finalizar")
    public Mantenimiento finalizar(@PathVariable Long id) {
        return mantenimientoService.finalizar(id);
    }
}
