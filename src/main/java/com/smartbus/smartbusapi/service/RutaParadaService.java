package com.smartbus.smartbusapi.service;

import com.smartbus.smartbusapi.dto.RutaParadaDto;
import com.smartbus.smartbusapi.exception.RecursoNoEncontradoException;
import com.smartbus.smartbusapi.exception.ReglaNegocioException;
import com.smartbus.smartbusapi.model.Parada;
import com.smartbus.smartbusapi.model.Ruta;
import com.smartbus.smartbusapi.model.RutaParada;
import com.smartbus.smartbusapi.repository.AsignacionRepository;
import com.smartbus.smartbusapi.repository.ParadaRepository;
import com.smartbus.smartbusapi.repository.RutaParadaRepository;
import com.smartbus.smartbusapi.repository.RutaRepository;
import com.smartbus.smartbusapi.util.Validaciones;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de RutaParada: administra QUÉ paradas tiene cada ruta y en QUÉ ORDEN.
 *
 * Reglas:
 *  - Una parada no se puede repetir dentro de la misma ruta.
 *  - Dos paradas de la misma ruta no pueden tener el mismo número de orden.
 *  - No se puede quitar una parada que tenga estudiantes asignados.
 *  - Una ruta ACTIVA no puede quedarse sin paradas.
 */
@Service
public class RutaParadaService {

    private final RutaParadaRepository rutaParadaRepository;
    private final RutaRepository rutaRepository;
    private final ParadaRepository paradaRepository;
    private final AsignacionRepository asignacionRepository;

    public RutaParadaService(RutaParadaRepository rutaParadaRepository,
                             RutaRepository rutaRepository,
                             ParadaRepository paradaRepository,
                             AsignacionRepository asignacionRepository) {
        this.rutaParadaRepository = rutaParadaRepository;
        this.rutaRepository = rutaRepository;
        this.paradaRepository = paradaRepository;
        this.asignacionRepository = asignacionRepository;
    }

    // LISTAR. Si llega rutaId, devuelve solo las paradas de esa ruta, ya ordenadas.
    public List<RutaParada> listar(Long rutaId) {
        if (rutaId != null) {
            return rutaParadaRepository.findByRutaIdOrderByOrdenAsc(rutaId);
        }
        return rutaParadaRepository.findAll();
    }

    // BUSCAR POR ID
    public RutaParada buscarPorId(Long id) {
        return rutaParadaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("RutaParada no encontrada con id: " + id));
    }

    // AGREGAR una parada a una ruta (asignarParada() del UML)
    public RutaParada agregar(RutaParadaDto dto) {
        Ruta ruta = rutaRepository.findById(Validaciones.idObligatorio(dto.getRutaId(), "rutaId"))
                .orElseThrow(() -> new RecursoNoEncontradoException("Ruta no encontrada con id: " + dto.getRutaId()));
        Parada parada = paradaRepository.findById(Validaciones.idObligatorio(dto.getParadaId(), "paradaId"))
                .orElseThrow(() -> new RecursoNoEncontradoException("Parada no encontrada con id: " + dto.getParadaId()));
        validarOrden(dto.getOrden());
        if (!Boolean.TRUE.equals(parada.getEstado())) {
            throw new ReglaNegocioException("La parada '" + parada.getNombre() + "' está inactiva");
        }
        if (rutaParadaRepository.existsByRutaIdAndParadaId(ruta.getId(), parada.getId())) {
            throw new ReglaNegocioException("La parada ya pertenece a esta ruta");
        }
        if (rutaParadaRepository.existsByRutaIdAndOrden(ruta.getId(), dto.getOrden())) {
            throw new ReglaNegocioException("Ya existe otra parada con el orden " + dto.getOrden() + " en esta ruta");
        }
        RutaParada rutaParada = new RutaParada();
        rutaParada.setRuta(ruta);
        rutaParada.setParada(parada);
        rutaParada.setOrden(dto.getOrden());
        return rutaParadaRepository.save(rutaParada);
    }

    // CAMBIAR ORDEN (cambiarOrden() del UML). Solo se usa el campo "orden" del JSON.
    public RutaParada cambiarOrden(Long id, RutaParadaDto dto) {
        RutaParada rutaParada = buscarPorId(id);
        validarOrden(dto.getOrden());
        boolean cambia = !dto.getOrden().equals(rutaParada.getOrden());
        if (cambia && rutaParadaRepository.existsByRutaIdAndOrden(rutaParada.getRuta().getId(), dto.getOrden())) {
            throw new ReglaNegocioException("Ya existe otra parada con el orden " + dto.getOrden() + " en esta ruta");
        }
        rutaParada.setOrden(dto.getOrden());
        return rutaParadaRepository.save(rutaParada);
    }

    // QUITAR una parada de una ruta. Aquí sí se borra la fila: es solo la unión entre ruta y parada.
    public void quitar(Long id) {
        RutaParada rutaParada = buscarPorId(id);
        Long rutaId = rutaParada.getRuta().getId();
        Long paradaId = rutaParada.getParada().getId();
        if (asignacionRepository.countByRutaIdAndParadaIdAndEstadoTrue(rutaId, paradaId) > 0) {
            throw new ReglaNegocioException("No se puede quitar la parada: hay estudiantes asignados a ella en esta ruta");
        }
        boolean rutaActiva = Boolean.TRUE.equals(rutaParada.getRuta().getEstado());
        if (rutaActiva && rutaParadaRepository.countByRutaId(rutaId) <= 1) {
            throw new ReglaNegocioException("Una ruta activa debe conservar al menos una parada");
        }
        rutaParadaRepository.delete(rutaParada);
    }

    private void validarOrden(Integer orden) {
        Validaciones.objetoObligatorio(orden, "orden");
        if (orden <= 0) {
            throw new ReglaNegocioException("El orden debe ser un número mayor que 0");
        }
    }
}
