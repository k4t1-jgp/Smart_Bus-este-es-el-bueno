package controller;

import com.smartbus.smartbusapi.dto.EncargadoDto;
import com.smartbus.smartbusapi.model.Encargado;
import com.smartbus.smartbusapi.service.EncargadoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador de Encargado: es la "puerta de entrada" HTTP del módulo.
 *
 * Recibe las peticiones (GET, POST, PUT, DELETE) en la ruta /api/encargados, se las pasa al
 * servicio y devuelve la respuesta en formato JSON. Aquí NO va lógica de negocio.
 */
@RestController
@RequestMapping("/api/encargados")
public class EncargadoController {

    private final EncargadoService encargadoService;

    // Spring inyecta el servicio automáticamente.
    public EncargadoController(EncargadoService encargadoService) {
        this.encargadoService = encargadoService;
    }

    // GET /api/encargados -> lista todos
    @GetMapping
    public List<Encargado> listarTodos() {
        return encargadoService.listarTodos();
    }

    // GET /api/encargados/{id} -> busca uno por id
    @GetMapping("/{id}")
    public Encargado buscarPorId(@PathVariable Long id) {
        return encargadoService.buscarPorId(id);
    }

    // POST /api/encargados -> crea uno nuevo (el JSON viaja en el cuerpo de la petición)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Encargado crear(@RequestBody EncargadoDto dto) {
        return encargadoService.crear(dto);
    }

    // PUT /api/encargados/{id} -> actualiza uno existente
    @PutMapping("/{id}")
    public Encargado actualizar(@PathVariable Long id, @RequestBody EncargadoDto dto) {
        return encargadoService.actualizar(id, dto);
    }

    // DELETE /api/encargados/{id} -> desactivar
    @DeleteMapping("/{id}")
    public Map<String, String> desactivar(@PathVariable Long id) {
        encargadoService.desactivar(id);
        return Map.of("mensaje", "Encargado desactivado correctamente");
    }
}
