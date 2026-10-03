package com.smartbus.smartbusapi.controller;

import com.smartbus.smartbusapi.dto.PilotoDto;
import com.smartbus.smartbusapi.model.Piloto;
import com.smartbus.smartbusapi.service.PilotoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador de Piloto: es la "puerta de entrada" HTTP del módulo.
 *
 * Recibe las peticiones (GET, POST, PUT, DELETE) en la ruta /api/pilotos, se las pasa al
 * servicio y devuelve la respuesta en formato JSON. Aquí NO va lógica de negocio.
 */
@RestController
@RequestMapping("/api/pilotos")
public class PilotoController {

    private final PilotoService pilotoService;

    // Spring inyecta el servicio automáticamente.
    public PilotoController(PilotoService pilotoService) {
        this.pilotoService = pilotoService;
    }

    // GET /api/pilotos -> lista todos
    @GetMapping
    public List<Piloto> listarTodos() {
        return pilotoService.listarTodos();
    }

    // GET /api/pilotos/{id} -> busca uno por id
    @GetMapping("/{id}")
    public Piloto buscarPorId(@PathVariable Long id) {
        return pilotoService.buscarPorId(id);
    }

    // POST /api/pilotos -> crea uno nuevo (el JSON viaja en el cuerpo de la petición)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Piloto crear(@RequestBody PilotoDto dto) {
        return pilotoService.crear(dto);
    }

    // PUT /api/pilotos/{id} -> actualiza uno existente
    @PutMapping("/{id}")
    public Piloto actualizar(@PathVariable Long id, @RequestBody PilotoDto dto) {
        return pilotoService.actualizar(id, dto);
    }

    // DELETE /api/pilotos/{id} -> desactivar
    @DeleteMapping("/{id}")
    public Map<String, String> desactivar(@PathVariable Long id) {
        pilotoService.desactivar(id);
        return Map.of("mensaje", "Piloto desactivado correctamente");
    }
}
