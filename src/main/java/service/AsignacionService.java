package service;

import com.smartbus.smartbusapi.dto.AsignacionDto;
import com.smartbus.smartbusapi.exception.RecursoNoEncontradoException;
import com.smartbus.smartbusapi.exception.ReglaNegocioException;
import com.smartbus.smartbusapi.model.Asignacion;
import com.smartbus.smartbusapi.model.Estudiante;
import com.smartbus.smartbusapi.model.Parada;
import com.smartbus.smartbusapi.model.Ruta;
import com.smartbus.smartbusapi.repository.*;
import com.smartbus.smartbusapi.util.Validaciones;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio de Asignacion: sube a un estudiante a una ruta y le asigna su parada.
 *
 * Reglas:
 *  - El estudiante y la ruta deben estar activos.
 *  - La parada debe pertenecer a esa ruta.
 *  - Un estudiante solo puede tener UNA ruta activa por jornada.
 *  - No se puede superar la capacidad del vehículo de la ruta.
 */
@Service
public class AsignacionService {

    private final AsignacionRepository asignacionRepository;
    private final EstudianteRepository estudianteRepository;
    private final RutaRepository rutaRepository;
    private final ParadaRepository paradaRepository;
    private final RutaParadaRepository rutaParadaRepository;

    public AsignacionService(AsignacionRepository asignacionRepository,
                             EstudianteRepository estudianteRepository,
                             RutaRepository rutaRepository,
                             ParadaRepository paradaRepository,
                             RutaParadaRepository rutaParadaRepository) {
        this.asignacionRepository = asignacionRepository;
        this.estudianteRepository = estudianteRepository;
        this.rutaRepository = rutaRepository;
        this.paradaRepository = paradaRepository;
        this.rutaParadaRepository = rutaParadaRepository;
    }

    // LISTAR. Con rutaId: estudiantes activos de esa ruta. Con estudianteId: historial del estudiante.
    public List<Asignacion> listar(Long rutaId, Long estudianteId) {
        if (rutaId != null) {
            return asignacionRepository.findByRutaIdAndEstadoTrue(rutaId);
        }
        if (estudianteId != null) {
            return asignacionRepository.findByEstudianteId(estudianteId);
        }
        return asignacionRepository.findAll();
    }

    // BUSCAR POR ID
    public Asignacion buscarPorId(Long id) {
        return asignacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Asignación no encontrada con id: " + id));
    }

    // CREAR (asignarEstudiante() del UML)
    public Asignacion crear(AsignacionDto dto) {
        Asignacion asignacion = new Asignacion();
        aplicar(asignacion, dto);
        return asignacionRepository.save(asignacion);
    }

    // ACTUALIZAR: cambiar al estudiante de ruta o de parada (asignarRuta() / asignarParada() del UML)
    public Asignacion actualizar(Long id, AsignacionDto dto) {
        Asignacion asignacion = buscarPorId(id);
        if (!Boolean.TRUE.equals(asignacion.getEstado())) {
            throw new ReglaNegocioException("No se puede modificar una asignación cancelada");
        }
        aplicar(asignacion, dto);
        return asignacionRepository.save(asignacion);
    }

    // CANCELAR (cancelarAsignacion() del UML): eliminación lógica, se conserva el historial.
    public void cancelar(Long id) {
        Asignacion asignacion = buscarPorId(id);
        asignacion.setEstado(false);
        asignacionRepository.save(asignacion);
    }

    // Estudiantes que HOY tienen transporte: estudiante activo con una asignación vigente
    // en una ruta activa. Lo usan el dashboard y los pagos pendientes.
    public List<Estudiante> estudiantesConTransporte() {
        Map<Long, Estudiante> resultado = new LinkedHashMap<>();
        for (Asignacion asignacion : asignacionRepository.findByEstadoTrue()) {
            Estudiante estudiante = asignacion.getEstudiante();
            boolean estudianteActivo = Boolean.TRUE.equals(estudiante.getEstado());
            boolean rutaActiva = Boolean.TRUE.equals(asignacion.getRuta().getEstado());
            if (estudianteActivo && rutaActiva) {
                resultado.put(estudiante.getId(), estudiante); // el Map evita repetir estudiantes
            }
        }
        return new ArrayList<>(resultado.values());
    }

    // ---------- Método auxiliar privado: aquí están todas las reglas ----------
    private void aplicar(Asignacion asignacion, AsignacionDto dto) {
        Estudiante estudiante = estudianteRepository.findById(Validaciones.idObligatorio(dto.getEstudianteId(), "estudianteId"))
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con id: " + dto.getEstudianteId()));
        Ruta ruta = rutaRepository.findById(Validaciones.idObligatorio(dto.getRutaId(), "rutaId"))
                .orElseThrow(() -> new RecursoNoEncontradoException("Ruta no encontrada con id: " + dto.getRutaId()));
        Parada parada = paradaRepository.findById(Validaciones.idObligatorio(dto.getParadaId(), "paradaId"))
                .orElseThrow(() -> new RecursoNoEncontradoException("Parada no encontrada con id: " + dto.getParadaId()));

        if (!Boolean.TRUE.equals(estudiante.getEstado())) {
            throw new ReglaNegocioException("El estudiante está inactivo");
        }
        if (!Boolean.TRUE.equals(ruta.getEstado())) {
            throw new ReglaNegocioException("La ruta '" + ruta.getNombre() + "' no está activa");
        }
        // La parada debe formar parte del recorrido de la ruta.
        if (!rutaParadaRepository.existsByRutaIdAndParadaId(ruta.getId(), parada.getId())) {
            throw new ReglaNegocioException("La parada '" + parada.getNombre() + "' no pertenece a la ruta '" + ruta.getNombre() + "'");
        }
        // Regla: un estudiante solo puede tener una ruta activa por jornada.
        for (Asignacion otra : asignacionRepository.findByEstudianteIdAndEstadoTrue(estudiante.getId())) {
            boolean esLaMisma = asignacion.getId() != null && otra.getId().equals(asignacion.getId());
            boolean rutaActiva = Boolean.TRUE.equals(otra.getRuta().getEstado());
            boolean mismaJornada = otra.getRuta().getJornada().getId().equals(ruta.getJornada().getId());
            if (!esLaMisma && rutaActiva && mismaJornada) {
                throw new ReglaNegocioException("El estudiante ya tiene una ruta activa en la jornada '"
                        + ruta.getJornada().getNombre() + "': " + otra.getRuta().getNombre());
            }
        }
        // Regla: no se puede superar la capacidad del vehículo.
        long ocupados = asignacionRepository.countByRutaIdAndEstadoTrue(ruta.getId());
        // Si es una modificación dentro de la misma ruta, este estudiante ya estaba contado.
        boolean yaContada = asignacion.getId() != null && asignacion.getRuta() != null
                && asignacion.getRuta().getId().equals(ruta.getId())
                && Boolean.TRUE.equals(asignacion.getEstado());
        if (yaContada) {
            ocupados--;
        }
        int capacidad = ruta.getVehiculo().getCapacidad();
        if (ocupados >= capacidad) {
            throw new ReglaNegocioException("La ruta '" + ruta.getNombre() + "' ya alcanzó la capacidad del vehículo ("
                    + capacidad + " estudiantes)");
        }

        asignacion.setEstudiante(estudiante);
        asignacion.setRuta(ruta);
        asignacion.setParada(parada);
        if (dto.getFecha() != null) {
            asignacion.setFecha(dto.getFecha());
        } else if (asignacion.getFecha() == null) {
            asignacion.setFecha(LocalDate.now());
        }
        asignacion.setEstado(true);
    }
}
