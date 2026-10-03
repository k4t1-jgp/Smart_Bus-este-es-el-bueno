package service;

import com.smartbus.smartbusapi.dto.VehiculoDto;
import com.smartbus.smartbusapi.exception.RecursoNoEncontradoException;
import com.smartbus.smartbusapi.exception.ReglaNegocioException;
import com.smartbus.smartbusapi.model.Vehiculo;
import com.smartbus.smartbusapi.repository.MantenimientoRepository;
import com.smartbus.smartbusapi.repository.VehiculoRepository;
import com.smartbus.smartbusapi.util.Validaciones;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de Vehiculo: aquí vive la LÓGICA del módulo.
 *
 * El controlador le pasa los datos, el servicio los valida, busca lo que necesite en los
 * repositorios y finalmente guarda. Si algo está mal, lanza una excepción con un mensaje claro
 * (ver el paquete "exception").
 */
@Service
public class VehiculoService {
    private final VehiculoRepository vehiculoRepository;
    private final MantenimientoRepository mantenimientoRepository;

    // Inyección de dependencias por constructor: Spring nos entrega los repositorios ya creados.
    public VehiculoService(VehiculoRepository vehiculoRepository,
                           MantenimientoRepository mantenimientoRepository) {
        this.vehiculoRepository = vehiculoRepository;
        this.mantenimientoRepository = mantenimientoRepository;
    }

    // LISTAR TODOS
    public List<Vehiculo> listarTodos() {
        return vehiculoRepository.findAll();
    }

    // BUSCAR POR ID (si no existe, responde 404 con un mensaje claro)
    public Vehiculo buscarPorId(Long id) {
        return vehiculoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Vehiculo no encontrado con id: " + id));
    }

    // CREAR
    public Vehiculo crear(VehiculoDto dto) {
        Vehiculo vehiculo = new Vehiculo();
        copiarDatos(vehiculo, dto);
        return vehiculoRepository.save(vehiculo);
    }

    // ACTUALIZAR (PUT reemplaza todos los datos, por eso se vuelven a validar)
    public Vehiculo actualizar(Long id, VehiculoDto dto) {
        Vehiculo vehiculo = buscarPorId(id);
        copiarDatos(vehiculo, dto);
        return vehiculoRepository.save(vehiculo);
    }

    // DESACTIVAR: eliminación LÓGICA. No se borra la fila, solo se marca estado = false
    // (así no se pierde el historial, tal como pide el acta de constitución).
    public void desactivar(Long id) {
        Vehiculo vehiculo = buscarPorId(id);
        vehiculo.setEstado(false);
        vehiculoRepository.save(vehiculo);
    }

    // Pasa los datos del DTO a la entidad, validando lo obligatorio.
    private void copiarDatos(Vehiculo vehiculo, VehiculoDto dto) {
        Validaciones.textoObligatorio(dto.getPlaca(), "placa");
        vehiculo.setPlaca(dto.getPlaca());
        Validaciones.textoObligatorio(dto.getMarca(), "marca");
        vehiculo.setMarca(dto.getMarca());
        Validaciones.textoObligatorio(dto.getModelo(), "modelo");
        vehiculo.setModelo(dto.getModelo());
        vehiculo.setColor(dto.getColor());
        Validaciones.objetoObligatorio(dto.getAnio(), "anio");
        vehiculo.setAnio(dto.getAnio());
        Validaciones.objetoObligatorio(dto.getCapacidad(), "capacidad");
        vehiculo.setCapacidad(dto.getCapacidad());
        vehiculo.setVerificacion(dto.getVerificacion());
        // Regla de negocio.
        if (dto.getCapacidad() <= 0) {
            throw new ReglaNegocioException("La capacidad del vehículo debe ser mayor que 0");
        }
        // Si no se envía el estado, se conserva el que ya tenía (por defecto: activo).
        if (dto.getEstado() != null) {
            vehiculo.setEstado(dto.getEstado());
        }
    }

    // VEHÍCULOS DISPONIBLES (parte de "búsqueda y filtrado" del acta).
    // Definición usada: vehículo ACTIVO y que NO tiene un mantenimiento en curso.
    public List<Vehiculo> listarDisponibles() {
        return vehiculoRepository.findByEstadoTrue().stream()
                .filter(v -> !mantenimientoRepository.existsByVehiculoIdAndEstadoTrue(v.getId()))
                .toList();
    }
}
