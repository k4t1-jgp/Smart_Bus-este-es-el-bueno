package com.smartbus.smartbusapi.controller;

import com.smartbus.smartbusapi.dto.JornadaDto;
import com.smartbus.smartbusapi.model.Jornada;
import com.smartbus.smartbusapi.service.JornadaService;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de Jornada: es la "puerta de entrada" HTTP del módulo.
 *
 * Recibe las peticiones (GET, POST, PUT, DELETE) en la ruta /api/jornadas, se las pasa al
 * servicio y devuelve la respuesta en formato JSON. Aquí NO va lógica de negocio.
 */
@RestController
@RequestMapping("/api/jornadas")
public class JornadaController {

    private final JornadaService jornadaService;

    // Spring inyecta el servicio automáticamente.
    public JornadaController(JornadaService jornadaService) {
        this.jornadaService = jornadaService;
    }

    // GET /api/jornadas -> lista todos
    @GetMapping
    public List<Jornada> listarTodos() {
        return jornadaService.listarTodos();
    }

    // GET /api/jornadas/{id} -> busca uno por id
    @GetMapping("/{id}")
    public Jornada buscarPorId(@PathVariable Long id) {
        return jornadaService.buscarPorId(id);
    }

    // POST /api/jornadas -> crea uno nuevo (el JSON viaja en el cuerpo de la petición)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Jornada crear(@RequestBody JornadaDto dto) {
        return jornadaService.crear(dto);
    }

    // PUT /api/jornadas/{id} -> actualiza uno existente
    @PutMapping("/{id}")
    public Jornada actualizar(@PathVariable Long id, @RequestBody JornadaDto dto) {
        return jornadaService.actualizar(id, dto);
    }

    // DELETE /api/jornadas/{id} -> desactivar
    @DeleteMapping("/{id}")
    public Map<String, String> desactivar(@PathVariable Long id) {
        jornadaService.desactivar(id);
        return Map.of("mensaje", "Jornada desactivado correctamente");
    }
}
