package com.smartbus.smartbusapi.controller;

import com.smartbus.smartbusapi.dto.VehiculoDto;
import com.smartbus.smartbusapi.model.Vehiculo;
import com.smartbus.smartbusapi.service.VehiculoService;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de Vehiculo: es la "puerta de entrada" HTTP del módulo.
 *
 * Recibe las peticiones (GET, POST, PUT, DELETE) en la ruta /api/vehiculos, se las pasa al
 * servicio y devuelve la respuesta en formato JSON. Aquí NO va lógica de negocio.
 */
@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    // Spring inyecta el servicio automáticamente.
    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    // GET /api/vehiculos -> lista todos
    @GetMapping
    public List<Vehiculo> listarTodos() {
        return vehiculoService.listarTodos();
    }

    // GET /api/vehiculos/{id} -> busca uno por id
    @GetMapping("/{id}")
    public Vehiculo buscarPorId(@PathVariable Long id) {
        return vehiculoService.buscarPorId(id);
    }

    // POST /api/vehiculos -> crea uno nuevo (el JSON viaja en el cuerpo de la petición)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Vehiculo crear(@RequestBody VehiculoDto dto) {
        return vehiculoService.crear(dto);
    }

    // PUT /api/vehiculos/{id} -> actualiza uno existente
    @PutMapping("/{id}")
    public Vehiculo actualizar(@PathVariable Long id, @RequestBody VehiculoDto dto) {
        return vehiculoService.actualizar(id, dto);
    }

    // DELETE /api/vehiculos/{id} -> desactivar
    @DeleteMapping("/{id}")
    public Map<String, String> desactivar(@PathVariable Long id) {
        vehiculoService.desactivar(id);
        return Map.of("mensaje", "Vehiculo desactivado correctamente");
    }

    // GET /api/vehiculos/disponibles -> vehículos activos y sin mantenimiento en curso.
    @GetMapping("/disponibles")
    public List<Vehiculo> listarDisponibles() {
        return vehiculoService.listarDisponibles();
    }
}
