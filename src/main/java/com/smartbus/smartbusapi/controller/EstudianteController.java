package com.smartbus.smartbusapi.controller;

import com.smartbus.smartbusapi.dto.EstudianteDto;
import com.smartbus.smartbusapi.model.Estudiante;
import com.smartbus.smartbusapi.service.EstudianteService;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de Estudiante: es la "puerta de entrada" HTTP del módulo.
 *
 * Recibe las peticiones (GET, POST, PUT, DELETE) en la ruta /api/estudiantes, se las pasa al
 * servicio y devuelve la respuesta en formato JSON. Aquí NO va lógica de negocio.
 */
@RestController
@RequestMapping("/api/estudiantes")
public class EstudianteController {

    private final EstudianteService estudianteService;

    // Spring inyecta el servicio automáticamente.
    public EstudianteController(EstudianteService estudianteService) {
        this.estudianteService = estudianteService;
    }

    // GET /api/estudiantes -> lista todos
    @GetMapping
    public List<Estudiante> listarTodos() {
        return estudianteService.listarTodos();
    }

    // GET /api/estudiantes/{id} -> busca uno por id
    @GetMapping("/{id}")
    public Estudiante buscarPorId(@PathVariable Long id) {
        return estudianteService.buscarPorId(id);
    }

    // POST /api/estudiantes -> crea uno nuevo (el JSON viaja en el cuerpo de la petición)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Estudiante crear(@RequestBody EstudianteDto dto) {
        return estudianteService.crear(dto);
    }

    // PUT /api/estudiantes/{id} -> actualiza uno existente
    @PutMapping("/{id}")
    public Estudiante actualizar(@PathVariable Long id, @RequestBody EstudianteDto dto) {
        return estudianteService.actualizar(id, dto);
    }

    // DELETE /api/estudiantes/{id} -> desactivar
    @DeleteMapping("/{id}")
    public Map<String, String> desactivar(@PathVariable Long id) {
        estudianteService.desactivar(id);
        return Map.of("mensaje", "Estudiante desactivado correctamente");
    }

    // GET /api/estudiantes/buscar?carne=123 -> busca estudiantes por carné.
    @GetMapping("/buscar")
    public List<Estudiante> buscarPorCarne(@RequestParam String carne) {
        return estudianteService.buscarPorCarne(carne);
    }
}
