package service;

import com.smartbus.smartbusapi.dto.RutaDto;
import com.smartbus.smartbusapi.exception.RecursoNoEncontradoException;
import com.smartbus.smartbusapi.exception.ReglaNegocioException;
import com.smartbus.smartbusapi.model.Jornada;
import com.smartbus.smartbusapi.model.Piloto;
import com.smartbus.smartbusapi.model.Ruta;
import com.smartbus.smartbusapi.model.Vehiculo;
import com.smartbus.smartbusapi.repository.*;
import com.smartbus.smartbusapi.util.Validaciones;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio de Ruta: es el módulo con más reglas del negocio.
 *
 * Una ruta se CREA siempre inactiva. Para ACTIVARLA se verifica que:
 *  1. Tenga al menos una parada.
 *  2. La jornada, el vehículo y el piloto estén activos.
 *  3. El vehículo NO esté en mantenimiento.
 *  4. La licencia del piloto esté vigente.
 *  5. El vehículo no esté en otra ruta activa con horario que se cruce.
 *  6. El piloto no conduzca otra ruta activa con horario que se cruce.
 *  7. Los estudiantes ya asignados quepan en el vehículo (capacidad).
 * Si la ruta ya está activa y se edita, las reglas 2 a 7 se vuelven a revisar.
 */
@Service
public class RutaService {

    private final RutaRepository rutaRepository;
    private final JornadaRepository jornadaRepository;
    private final VehiculoRepository vehiculoRepository;
    private final PilotoRepository pilotoRepository;
    private final RutaParadaRepository rutaParadaRepository;
    private final AsignacionRepository asignacionRepository;
    private final MantenimientoRepository mantenimientoRepository;

    public RutaService(RutaRepository rutaRepository,
                       JornadaRepository jornadaRepository,
                       VehiculoRepository vehiculoRepository,
                       PilotoRepository pilotoRepository,
                       RutaParadaRepository rutaParadaRepository,
                       AsignacionRepository asignacionRepository,
                       MantenimientoRepository mantenimientoRepository) {
        this.rutaRepository = rutaRepository;
        this.jornadaRepository = jornadaRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.pilotoRepository = pilotoRepository;
        this.rutaParadaRepository = rutaParadaRepository;
        this.asignacionRepository = asignacionRepository;
        this.mantenimientoRepository = mantenimientoRepository;
    }

    // LISTAR TODAS
    public List<Ruta> listarTodos() {
        return rutaRepository.findAll();
    }

    // BUSCAR POR ID
    public Ruta buscarPorId(Long id) {
        return rutaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ruta no encontrada con id: " + id));
    }

    // CREAR: siempre queda inactiva hasta que se le agreguen paradas y se active.
    public Ruta crear(RutaDto dto) {
        Ruta ruta = new Ruta();
        copiarDatos(ruta, dto);
        ruta.setEstado(false);
        return rutaRepository.save(ruta);
    }

    // ACTUALIZAR: si la ruta está activa, se revalidan los recursos (vehículo, piloto, horarios).
    public Ruta actualizar(Long id, RutaDto dto) {
        Ruta ruta = buscarPorId(id);
        copiarDatos(ruta, dto);
        if (Boolean.TRUE.equals(ruta.getEstado())) {
            validarRecursos(ruta);
        }
        return rutaRepository.save(ruta);
    }

    // ACTIVAR (activarRuta() del UML)
    public Ruta activar(Long id) {
        Ruta ruta = buscarPorId(id);
        if (rutaParadaRepository.countByRutaId(id) == 0) {
            throw new ReglaNegocioException("La ruta debe tener al menos una parada antes de activarse");
        }
        validarRecursos(ruta);
        ruta.setEstado(true);
        return rutaRepository.save(ruta);
    }

    // DESACTIVAR (desactivarRuta() del UML). Nunca se borra una ruta: así no se pierde el historial
    // de los estudiantes que viajaron en ella.
    public void desactivar(Long id) {
        Ruta ruta = buscarPorId(id);
        ruta.setEstado(false);
        rutaRepository.save(ruta);
    }

    // ---------- Métodos auxiliares privados ----------

    // Pasa los datos del DTO a la entidad y valida lo básico (datos obligatorios y horas).
    private void copiarDatos(Ruta ruta, RutaDto dto) {
        Validaciones.textoObligatorio(dto.getNombre(), "nombre");
        Validaciones.objetoObligatorio(dto.getHorarioSalida(), "horarioSalida");
        Validaciones.objetoObligatorio(dto.getHorarioLlegada(), "horarioLlegada");
        if (!dto.getHorarioSalida().isBefore(dto.getHorarioLlegada())) {
            throw new ReglaNegocioException("La hora de salida debe ser anterior a la hora de llegada");
        }
        Jornada jornada = jornadaRepository.findById(Validaciones.idObligatorio(dto.getJornadaId(), "jornadaId"))
                .orElseThrow(() -> new RecursoNoEncontradoException("Jornada no encontrada con id: " + dto.getJornadaId()));
        Vehiculo vehiculo = vehiculoRepository.findById(Validaciones.idObligatorio(dto.getVehiculoId(), "vehiculoId"))
                .orElseThrow(() -> new RecursoNoEncontradoException("Vehiculo no encontrado con id: " + dto.getVehiculoId()));
        Piloto piloto = pilotoRepository.findById(Validaciones.idObligatorio(dto.getPilotoId(), "pilotoId"))
                .orElseThrow(() -> new RecursoNoEncontradoException("Piloto no encontrado con id: " + dto.getPilotoId()));

        ruta.setNombre(dto.getNombre());
        ruta.setRecorrido(dto.getRecorrido());
        ruta.setHorarioSalida(dto.getHorarioSalida());
        ruta.setHorarioLlegada(dto.getHorarioLlegada());
        ruta.setJornada(jornada);
        ruta.setVehiculo(vehiculo);
        ruta.setPiloto(piloto);
    }

    // Reglas 2 a 7 (ver descripción de la clase). Lanza ReglaNegocioException si alguna falla.
    private void validarRecursos(Ruta ruta) {
        Vehiculo vehiculo = ruta.getVehiculo();
        Piloto piloto = ruta.getPiloto();

        if (!Boolean.TRUE.equals(ruta.getJornada().getEstado())) {
            throw new ReglaNegocioException("La jornada de la ruta está inactiva");
        }
        if (!Boolean.TRUE.equals(vehiculo.getEstado())) {
            throw new ReglaNegocioException("El vehículo " + vehiculo.getPlaca() + " está inactivo");
        }
        // Regla: un vehículo en mantenimiento no puede asignarse a una ruta activa.
        if (mantenimientoRepository.existsByVehiculoIdAndEstadoTrue(vehiculo.getId())) {
            throw new ReglaNegocioException("El vehículo " + vehiculo.getPlaca() + " está en mantenimiento");
        }
        if (!Boolean.TRUE.equals(piloto.getEstado())) {
            throw new ReglaNegocioException("El piloto " + piloto.getNombre() + " está inactivo");
        }
        // Regla: la licencia del piloto debe estar vigente.
        LocalDate vence = piloto.getFechaVencimientoLicencia();
        if (vence == null || vence.isBefore(LocalDate.now())) {
            throw new ReglaNegocioException("La licencia del piloto " + piloto.getNombre() + " no está vigente");
        }
        // Regla: un vehículo no puede estar en dos rutas activas al mismo horario.
        for (Ruta otra : rutaRepository.findByVehiculoIdAndEstadoTrue(vehiculo.getId())) {
            if (!otra.getId().equals(ruta.getId()) && hayCruce(ruta, otra)) {
                throw new ReglaNegocioException("El vehículo " + vehiculo.getPlaca()
                        + " ya está en la ruta activa '" + otra.getNombre() + "' en un horario que se cruza");
            }
        }
        // Regla: un piloto no puede conducir dos rutas simultáneamente.
        for (Ruta otra : rutaRepository.findByPilotoIdAndEstadoTrue(piloto.getId())) {
            if (!otra.getId().equals(ruta.getId()) && hayCruce(ruta, otra)) {
                throw new ReglaNegocioException("El piloto " + piloto.getNombre()
                        + " ya conduce la ruta activa '" + otra.getNombre() + "' en un horario que se cruza");
            }
        }
        // Regla: los estudiantes asignados no pueden superar la capacidad del vehículo.
        long ocupados = asignacionRepository.countByRutaIdAndEstadoTrue(ruta.getId());
        if (ocupados > vehiculo.getCapacidad()) {
            throw new ReglaNegocioException("La ruta ya tiene " + ocupados + " estudiantes asignados y el vehículo "
                    + vehiculo.getPlaca() + " solo tiene capacidad para " + vehiculo.getCapacidad());
        }
    }

    // Dos rutas se "cruzan" si una empieza antes de que la otra termine, y viceversa.
    // Ejemplo: 06:00-07:30 y 07:00-08:00 se cruzan; 06:00-07:00 y 07:00-08:00 NO se cruzan.
    private boolean hayCruce(Ruta a, Ruta b) {
        return a.getHorarioSalida().isBefore(b.getHorarioLlegada())
                && b.getHorarioSalida().isBefore(a.getHorarioLlegada());
    }
}
