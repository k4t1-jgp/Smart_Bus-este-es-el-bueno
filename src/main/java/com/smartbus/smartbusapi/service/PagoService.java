package com.smartbus.smartbusapi.service;

import com.smartbus.smartbusapi.dto.PagoDto;
import com.smartbus.smartbusapi.exception.RecursoNoEncontradoException;
import com.smartbus.smartbusapi.exception.ReglaNegocioException;
import com.smartbus.smartbusapi.model.Estudiante;
import com.smartbus.smartbusapi.model.Pago;
import com.smartbus.smartbusapi.repository.EstudianteRepository;
import com.smartbus.smartbusapi.repository.PagoRepository;
import com.smartbus.smartbusapi.util.Validaciones;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio de Pago: cuotas mensuales de transporte.
 *
 * Reglas:
 *  - Los pagos se registran por mes (1 a 12) y año.
 *  - El monto debe ser mayor que 0.
 *  - No se permiten pagos duplicados del mismo estudiante en el mismo mes y año.
 *  - Un pago no se borra: se ANULA (estado = false) y queda como historial.
 */
@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final EstudianteRepository estudianteRepository;
    private final AsignacionService asignacionService;

    public PagoService(PagoRepository pagoRepository,
                       EstudianteRepository estudianteRepository,
                       AsignacionService asignacionService) {
        this.pagoRepository = pagoRepository;
        this.estudianteRepository = estudianteRepository;
        this.asignacionService = asignacionService;
    }

    // LISTAR. Con estudianteId devuelve solo los pagos de ese estudiante.
    public List<Pago> listar(Long estudianteId) {
        if (estudianteId != null) {
            return pagoRepository.findByEstudianteId(estudianteId);
        }
        return pagoRepository.findAll();
    }

    // BUSCAR POR ID
    public Pago buscarPorId(Long id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pago no encontrado con id: " + id));
    }

    // REGISTRAR (registrarPago() del UML)
    public Pago crear(PagoDto dto) {
        Pago pago = new Pago();
        aplicar(pago, dto);
        return pagoRepository.save(pago);
    }

    // ACTUALIZAR (actualizarPago() del UML): sirve para corregir un pago vigente.
    public Pago actualizar(Long id, PagoDto dto) {
        Pago pago = buscarPorId(id);
        if (!Boolean.TRUE.equals(pago.getEstado())) {
            throw new ReglaNegocioException("No se puede modificar un pago anulado");
        }
        aplicar(pago, dto);
        return pagoRepository.save(pago);
    }

    // ANULAR: eliminación lógica.
    public void anular(Long id) {
        Pago pago = buscarPorId(id);
        pago.setEstado(false);
        pagoRepository.save(pago);
    }

    // PAGOS PENDIENTES: estudiantes con transporte que NO tienen pago vigente en ese mes y año.
    // Si no se envían mes y año, se usan los actuales.
    public List<Estudiante> pendientes(Integer mes, Integer anio) {
        LocalDate hoy = LocalDate.now();
        int mesConsulta = (mes != null) ? mes : hoy.getMonthValue();
        int anioConsulta = (anio != null) ? anio : hoy.getYear();

        // Ids de los estudiantes que YA pagaron ese periodo.
        Set<Long> yaPagaron = pagoRepository.findByMesAndAnioAndEstadoTrue(mesConsulta, anioConsulta).stream()
                .map(pago -> pago.getEstudiante().getId())
                .collect(Collectors.toSet());

        List<Estudiante> pendientes = new ArrayList<>();
        for (Estudiante estudiante : asignacionService.estudiantesConTransporte()) {
            if (!yaPagaron.contains(estudiante.getId())) {
                pendientes.add(estudiante);
            }
        }
        return pendientes;
    }

    // ---------- Método auxiliar privado: validaciones y copia de datos ----------
    private void aplicar(Pago pago, PagoDto dto) {
        Estudiante estudiante = estudianteRepository.findById(Validaciones.idObligatorio(dto.getEstudianteId(), "estudianteId"))
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con id: " + dto.getEstudianteId()));
        Validaciones.objetoObligatorio(dto.getMonto(), "monto");
        Validaciones.objetoObligatorio(dto.getMes(), "mes");
        Validaciones.objetoObligatorio(dto.getAnio(), "anio");

        if (dto.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ReglaNegocioException("El monto debe ser mayor que 0");
        }
        if (dto.getMes() < 1 || dto.getMes() > 12) {
            throw new ReglaNegocioException("El mes debe estar entre 1 y 12");
        }
        if (dto.getAnio() < 2000) {
            throw new ReglaNegocioException("El año no es válido");
        }
        // Regla: no se permiten pagos duplicados del mismo periodo.
        List<Pago> mismoPeriodo = pagoRepository.findByEstudianteIdAndMesAndAnioAndEstadoTrue(
                estudiante.getId(), dto.getMes(), dto.getAnio());
        for (Pago existente : mismoPeriodo) {
            boolean esElMismo = pago.getId() != null && existente.getId().equals(pago.getId());
            if (!esElMismo) {
                throw new ReglaNegocioException("El estudiante ya tiene un pago vigente para " + dto.getMes() + "/" + dto.getAnio());
            }
        }

        pago.setEstudiante(estudiante);
        pago.setMonto(dto.getMonto());
        pago.setMes(dto.getMes());
        pago.setAnio(dto.getAnio());
        pago.setFechaPago(dto.getFechaPago() != null ? dto.getFechaPago() : LocalDate.now());
        pago.setEstado(true);
    }
}
