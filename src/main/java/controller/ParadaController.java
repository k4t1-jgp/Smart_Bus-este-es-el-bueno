package controller;

import com.smartbus.smartbusapi.dto.ParadaDto;
import com.smartbus.smartbusapi.model.Parada;
import com.smartbus.smartbusapi.service.ParadaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador de Parada: es la "puerta de entrada" HTTP del módulo.
 *
 * Recibe las peticiones (GET, POST, PUT, DELETE) en la ruta /api/paradas, se las pasa al
 * servicio y devuelve la respuesta en formato JSON. Aquí NO va lógica de negocio.
 */
@RestController
@RequestMapping("/api/paradas")
public class ParadaController {

    private final ParadaService paradaService;

    // Spring inyecta el servicio automáticamente.
    public ParadaController(ParadaService paradaService) {
        this.paradaService = paradaService;
    }

    // GET /api/paradas -> lista todos
    @GetMapping
    public List<Parada> listarTodos() {
        return paradaService.listarTodos();
    }

    // GET /api/paradas/{id} -> busca uno por id
    @GetMapping("/{id}")
    public Parada buscarPorId(@PathVariable Long id) {
        return paradaService.buscarPorId(id);
    }

    // POST /api/paradas -> crea uno nuevo (el JSON viaja en el cuerpo de la petición)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Parada crear(@RequestBody ParadaDto dto) {
        return paradaService.crear(dto);
    }

    // PUT /api/paradas/{id} -> actualiza uno existente
    @PutMapping("/{id}")
    public Parada actualizar(@PathVariable Long id, @RequestBody ParadaDto dto) {
        return paradaService.actualizar(id, dto);
    }

    // DELETE /api/paradas/{id} -> desactivar
    @DeleteMapping("/{id}")
    public Map<String, String> desactivar(@PathVariable Long id) {
        paradaService.desactivar(id);
        return Map.of("mensaje", "Parada desactivado correctamente");
    }
}
