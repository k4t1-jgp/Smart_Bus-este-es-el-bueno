package com.smartbus.smartbusapi.service;

import com.smartbus.smartbusapi.dto.JornadaDto;
import com.smartbus.smartbusapi.exception.RecursoNoEncontradoException;
import com.smartbus.smartbusapi.exception.ReglaNegocioException;
import com.smartbus.smartbusapi.model.Jornada;
import com.smartbus.smartbusapi.repository.JornadaRepository;
import com.smartbus.smartbusapi.util.Validaciones;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de Jornada: aquí vive la LÓGICA del módulo.
 *
 * El controlador le pasa los datos, el servicio los valida, busca lo que necesite en los
 * repositorios y finalmente guarda. Si algo está mal, lanza una excepción con un mensaje claro
 * (ver el paquete "exception").
 */
@Service
public class JornadaService {
    private final JornadaRepository jornadaRepository;

    // Inyección de dependencias por constructor: Spring nos entrega los repositorios ya creados.
    public JornadaService(JornadaRepository jornadaRepository) {
        this.jornadaRepository = jornadaRepository;
    }

    // LISTAR TODOS
    public List<Jornada> listarTodos() {
        return jornadaRepository.findAll();
    }

    // BUSCAR POR ID (si no existe, responde 404 con un mensaje claro)
    public Jornada buscarPorId(Long id) {
        return jornadaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Jornada no encontrado con id: " + id));
    }

    // CREAR
    public Jornada crear(JornadaDto dto) {
        Jornada jornada = new Jornada();
        copiarDatos(jornada, dto);
        return jornadaRepository.save(jornada);
    }

    // ACTUALIZAR (PUT reemplaza todos los datos, por eso se vuelven a validar)
    public Jornada actualizar(Long id, JornadaDto dto) {
        Jornada jornada = buscarPorId(id);
        copiarDatos(jornada, dto);
        return jornadaRepository.save(jornada);
    }

    // DESACTIVAR: eliminación LÓGICA. No se borra la fila, solo se marca estado = false
    // (así no se pierde el historial, tal como pide el acta de constitución).
    public void desactivar(Long id) {
        Jornada jornada = buscarPorId(id);
        jornada.setEstado(false);
        jornadaRepository.save(jornada);
    }

    // Pasa los datos del DTO a la entidad, validando lo obligatorio.
    private void copiarDatos(Jornada jornada, JornadaDto dto) {
        Validaciones.textoObligatorio(dto.getNombre(), "nombre");
        jornada.setNombre(dto.getNombre());
        Validaciones.objetoObligatorio(dto.getHorarioInicio(), "horarioInicio");
        jornada.setHorarioInicio(dto.getHorarioInicio());
        Validaciones.objetoObligatorio(dto.getHorarioFin(), "horarioFin");
        jornada.setHorarioFin(dto.getHorarioFin());
        // Regla de negocio.
        if (!dto.getHorarioInicio().isBefore(dto.getHorarioFin())) {
            throw new ReglaNegocioException("La hora de inicio debe ser anterior a la hora de fin");
        }
        // Si no se envía el estado, se conserva el que ya tenía (por defecto: activo).
        if (dto.getEstado() != null) {
            jornada.setEstado(dto.getEstado());
        }
    }
}
