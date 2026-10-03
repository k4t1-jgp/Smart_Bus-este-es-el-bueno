package controller;

import com.smartbus.smartbusapi.dto.EstudianteEncargadoDto;
import com.smartbus.smartbusapi.model.EstudianteEncargado;
import com.smartbus.smartbusapi.service.EstudianteEncargadoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador de EstudianteEncargado: es la "puerta de entrada" HTTP del módulo.
 *
 * Recibe las peticiones (GET, POST, PUT, DELETE) en la ruta /api/estudiante-encargados, se las pasa al
 * servicio y devuelve la respuesta en formato JSON. Aquí NO va lógica de negocio.
 */
@RestController
@RequestMapping("/api/estudiante-encargados")
public class EstudianteEncargadoController {

    private final EstudianteEncargadoService estudianteEncargadoService;

    // Spring inyecta el servicio automáticamente.
    public EstudianteEncargadoController(EstudianteEncargadoService estudianteEncargadoService) {
        this.estudianteEncargadoService = estudianteEncargadoService;
    }

    // GET /api/estudiante-encargados -> lista todos
    @GetMapping
    public List<EstudianteEncargado> listarTodos() {
        return estudianteEncargadoService.listarTodos();
    }

    // GET /api/estudiante-encargados/{id} -> busca uno por id
    @GetMapping("/{id}")
    public EstudianteEncargado buscarPorId(@PathVariable Long id) {
        return estudianteEncargadoService.buscarPorId(id);
    }

    // POST /api/estudiante-encargados -> crea uno nuevo (el JSON viaja en el cuerpo de la petición)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EstudianteEncargado crear(@RequestBody EstudianteEncargadoDto dto) {
        return estudianteEncargadoService.crear(dto);
    }

    // PUT /api/estudiante-encargados/{id} -> actualiza uno existente
    @PutMapping("/{id}")
    public EstudianteEncargado actualizar(@PathVariable Long id, @RequestBody EstudianteEncargadoDto dto) {
        return estudianteEncargadoService.actualizar(id, dto);
    }

    // DELETE /api/estudiante-encargados/{id} -> eliminar
    @DeleteMapping("/{id}")
    public Map<String, String> eliminar(@PathVariable Long id) {
        estudianteEncargadoService.eliminar(id);
        return Map.of("mensaje", "EstudianteEncargado eliminado correctamente");
    }
}
