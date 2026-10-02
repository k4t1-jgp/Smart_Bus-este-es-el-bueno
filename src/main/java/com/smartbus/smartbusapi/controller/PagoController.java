package com.smartbus.smartbusapi.controller;

import com.smartbus.smartbusapi.dto.PagoDto;
import com.smartbus.smartbusapi.model.Estudiante;
import com.smartbus.smartbusapi.model.Pago;
import com.smartbus.smartbusapi.service.PagoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador de Pago: /api/pagos
 */
@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    // GET /api/pagos                   -> todos
    // GET /api/pagos?estudianteId=3    -> pagos de un estudiante
    @GetMapping
    public List<Pago> listar(@RequestParam(required = false) Long estudianteId) {
        return pagoService.listar(estudianteId);
    }

    // GET /api/pagos/pendientes?mes=9&anio=2026 -> estudiantes con transporte que no han pagado
    // (sin parámetros usa el mes y año actuales)
    @GetMapping("/pendientes")
    public List<Estudiante> pendientes(@RequestParam(required = false) Integer mes,
                                       @RequestParam(required = false) Integer anio) {
        return pagoService.pendientes(mes, anio);
    }

    // GET /api/pagos/{id}
    @GetMapping("/{id}")
    public Pago buscarPorId(@PathVariable Long id) {
        return pagoService.buscarPorId(id);
    }

    // POST /api/pagos -> registra un pago
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pago crear(@RequestBody PagoDto dto) {
        return pagoService.crear(dto);
    }

    // PUT /api/pagos/{id} -> corrige un pago vigente
    @PutMapping("/{id}")
    public Pago actualizar(@PathVariable Long id, @RequestBody PagoDto dto) {
        return pagoService.actualizar(id, dto);
    }

    // DELETE /api/pagos/{id} -> anula el pago (queda como historial)
    @DeleteMapping("/{id}")
    public Map<String, String> anular(@PathVariable Long id) {
        pagoService.anular(id);
        return Map.of("mensaje", "Pago anulado correctamente");
    }
}
