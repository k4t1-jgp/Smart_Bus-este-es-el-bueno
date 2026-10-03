package controller;

import com.smartbus.smartbusapi.dto.RutaParadaDto;
import com.smartbus.smartbusapi.model.RutaParada;
import com.smartbus.smartbusapi.service.RutaParadaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador de RutaParada: /api/ruta-paradas
 * Sirve para armar el recorrido de una ruta: qué paradas tiene y en qué orden.
 */
@RestController
@RequestMapping("/api/ruta-paradas")
public class RutaParadaController {

    private final RutaParadaService rutaParadaService;

    public RutaParadaController(RutaParadaService rutaParadaService) {
        this.rutaParadaService = rutaParadaService;
    }

    // GET /api/ruta-paradas?rutaId=1 -> paradas de una ruta, ordenadas (sin rutaId lista todas)
    @GetMapping
    public List<RutaParada> listar(@RequestParam(required = false) Long rutaId) {
        return rutaParadaService.listar(rutaId);
    }

    // GET /api/ruta-paradas/{id}
    @GetMapping("/{id}")
    public RutaParada buscarPorId(@PathVariable Long id) {
        return rutaParadaService.buscarPorId(id);
    }

    // POST /api/ruta-paradas -> agrega una parada a una ruta
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RutaParada agregar(@RequestBody RutaParadaDto dto) {
        return rutaParadaService.agregar(dto);
    }

    // PUT /api/ruta-paradas/{id} -> cambia el orden (solo se usa el campo "orden")
    @PutMapping("/{id}")
    public RutaParada cambiarOrden(@PathVariable Long id, @RequestBody RutaParadaDto dto) {
        return rutaParadaService.cambiarOrden(id, dto);
    }

    // DELETE /api/ruta-paradas/{id} -> quita la parada de la ruta
    @DeleteMapping("/{id}")
    public Map<String, String> quitar(@PathVariable Long id) {
        rutaParadaService.quitar(id);
        return Map.of("mensaje", "Parada quitada de la ruta correctamente");
    }
}
