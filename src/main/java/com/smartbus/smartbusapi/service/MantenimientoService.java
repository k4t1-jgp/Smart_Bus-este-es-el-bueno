package com.smartbus.smartbusapi.service;

import com.smartbus.smartbusapi.dto.MantenimientoDto;
import com.smartbus.smartbusapi.exception.RecursoNoEncontradoException;
import com.smartbus.smartbusapi.exception.ReglaNegocioException;
import com.smartbus.smartbusapi.model.Mantenimiento;
import com.smartbus.smartbusapi.model.Ruta;
import com.smartbus.smartbusapi.model.Vehiculo;
import com.smartbus.smartbusapi.repository.MantenimientoRepository;
import com.smartbus.smartbusapi.repository.RutaRepository;
import com.smartbus.smartbusapi.repository.VehiculoRepository;
import com.smartbus.smartbusapi.util.Validaciones;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de Mantenimiento: periodos en que un vehículo está en el taller.
 *
 * Reglas:
 *  - Un vehículo solo puede tener UN mantenimiento en curso a la vez.
 *  - Mientras está en mantenimiento, el vehículo no puede asignarse a rutas activas
 *    (esa revisión la hace RutaService al activar o editar una ruta).
 *  - Para registrar un mantenimiento, el vehículo no debe estar en ninguna ruta ACTIVA
 *    (primero se desactiva la ruta o se le cambia el vehículo).
 *  - Un mantenimiento no se borra: se FINALIZA y queda como historial.
 */
@Service
public class MantenimientoService {

    private final MantenimientoRepository mantenimientoRepository;
    private final VehiculoRepository vehiculoRepository;
    private final RutaRepository rutaRepository;

    public MantenimientoService(MantenimientoRepository mantenimientoRepository,
                                VehiculoRepository vehiculoRepository,
                                RutaRepository rutaRepository) {
        this.mantenimientoRepository = mantenimientoRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.rutaRepository = rutaRepository;
    }

    // LISTAR. Con vehiculoId devuelve solo el historial de ese vehículo.
    public List<Mantenimiento> listar(Long vehiculoId) {
        if (vehiculoId != null) {
            return mantenimientoRepository.findByVehiculoId(vehiculoId);
        }
        return mantenimientoRepository.findAll();
    }

    // BUSCAR POR ID
    public Mantenimiento buscarPorId(Long id) {
        return mantenimientoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mantenimiento no encontrado con id: " + id));
    }

    // REGISTRAR (registrarMantenimiento() del UML): el mantenimiento queda EN CURSO.
    public Mantenimiento registrar(MantenimientoDto dto) {
        Vehiculo vehiculo = vehiculoRepository.findById(Validaciones.idObligatorio(dto.getVehiculoId(), "vehiculoId"))
                .orElseThrow(() -> new RecursoNoEncontradoException("Vehiculo no encontrado con id: " + dto.getVehiculoId()));
        validarDatos(dto);

        if (mantenimientoRepository.existsByVehiculoIdAndEstadoTrue(vehiculo.getId())) {
            throw new ReglaNegocioException("El vehículo " + vehiculo.getPlaca() + " ya tiene un mantenimiento en curso");
        }
        List<Ruta> rutasActivas = rutaRepository.findByVehiculoIdAndEstadoTrue(vehiculo.getId());
        if (!rutasActivas.isEmpty()) {
            String nombres = rutasActivas.stream().map(Ruta::getNombre).collect(Collectors.joining(", "));
            throw new ReglaNegocioException("El vehículo " + vehiculo.getPlaca() + " está en rutas activas (" + nombres
                    + "). Desactive esas rutas o cámbieles el vehículo antes de registrar el mantenimiento");
        }

        Mantenimiento mantenimiento = new Mantenimiento();
        mantenimiento.setVehiculo(vehiculo);
        mantenimiento.setFechaInicio(dto.getFechaInicio() != null ? dto.getFechaInicio() : LocalDate.now());
        mantenimiento.setTipo(dto.getTipo());
        mantenimiento.setDetalle(dto.getDetalle());
        mantenimiento.setCosto(dto.getCosto());
        mantenimiento.setEstado(true);
        return mantenimientoRepository.save(mantenimiento);
    }

    // ACTUALIZAR (actualizarMantenimiento() del UML): solo mientras está en curso.
    // No se puede cambiar el vehículo; si se equivocaron de vehículo, se registra uno nuevo.
    public Mantenimiento actualizar(Long id, MantenimientoDto dto) {
        Mantenimiento mantenimiento = buscarPorId(id);
        if (!Boolean.TRUE.equals(mantenimiento.getEstado())) {
            throw new ReglaNegocioException("No se puede modificar un mantenimiento ya finalizado");
        }
        validarDatos(dto);
        if (dto.getFechaInicio() != null) {
            mantenimiento.setFechaInicio(dto.getFechaInicio());
        }
        mantenimiento.setTipo(dto.getTipo());
        mantenimiento.setDetalle(dto.getDetalle());
        mantenimiento.setCosto(dto.getCosto());
        return mantenimientoRepository.save(mantenimiento);
    }

    // FINALIZAR (finalizarMantenimiento() del UML): el vehículo vuelve a quedar disponible.
    public Mantenimiento finalizar(Long id) {
        Mantenimiento mantenimiento = buscarPorId(id);
        if (!Boolean.TRUE.equals(mantenimiento.getEstado())) {
            throw new ReglaNegocioException("El mantenimiento ya estaba finalizado");
        }
        mantenimiento.setFechaFin(LocalDate.now());
        mantenimiento.setEstado(false);
        return mantenimientoRepository.save(mantenimiento);
    }

    private void validarDatos(MantenimientoDto dto) {
        Validaciones.textoObligatorio(dto.getTipo(), "tipo");
        Validaciones.textoObligatorio(dto.getDetalle(), "detalle");
        Validaciones.objetoObligatorio(dto.getCosto(), "costo");
        if (dto.getCosto().compareTo(BigDecimal.ZERO) < 0) {
            throw new ReglaNegocioException("El costo no puede ser negativo");
        }
    }
}
