package com.smartbus.smartbusapi.service;

import com.smartbus.smartbusapi.dto.EstudianteEncargadoDto;
import com.smartbus.smartbusapi.exception.RecursoNoEncontradoException;
import com.smartbus.smartbusapi.model.EstudianteEncargado;
import com.smartbus.smartbusapi.repository.EncargadoRepository;
import com.smartbus.smartbusapi.repository.EstudianteEncargadoRepository;
import com.smartbus.smartbusapi.repository.EstudianteRepository;
import com.smartbus.smartbusapi.util.Validaciones;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de EstudianteEncargado: aquí vive la LÓGICA del módulo.
 *
 * El controlador le pasa los datos, el servicio los valida, busca lo que necesite en los
 * repositorios y finalmente guarda. Si algo está mal, lanza una excepción con un mensaje claro
 * (ver el paquete "exception").
 */
@Service
public class EstudianteEncargadoService {
    private final EstudianteEncargadoRepository estudianteEncargadoRepository;
    private final EstudianteRepository estudianteRepository;
    private final EncargadoRepository encargadoRepository;

    // Inyección de dependencias por constructor: Spring nos entrega los repositorios ya creados.
    public EstudianteEncargadoService(EstudianteEncargadoRepository estudianteEncargadoRepository,
                                      EstudianteRepository estudianteRepository,
                                      EncargadoRepository encargadoRepository) {
        this.estudianteEncargadoRepository = estudianteEncargadoRepository;
        this.estudianteRepository = estudianteRepository;
        this.encargadoRepository = encargadoRepository;
    }

    // LISTAR TODOS
    public List<EstudianteEncargado> listarTodos() {
        return estudianteEncargadoRepository.findAll();
    }

    // BUSCAR POR ID (si no existe, responde 404 con un mensaje claro)
    public EstudianteEncargado buscarPorId(Long id) {
        return estudianteEncargadoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("EstudianteEncargado no encontrado con id: " + id));
    }

    // CREAR
    public EstudianteEncargado crear(EstudianteEncargadoDto dto) {
        EstudianteEncargado estudianteEncargado = new EstudianteEncargado();
        copiarDatos(estudianteEncargado, dto);
        return estudianteEncargadoRepository.save(estudianteEncargado);
    }

    // ACTUALIZAR (PUT reemplaza todos los datos, por eso se vuelven a validar)
    public EstudianteEncargado actualizar(Long id, EstudianteEncargadoDto dto) {
        EstudianteEncargado estudianteEncargado = buscarPorId(id);
        copiarDatos(estudianteEncargado, dto);
        return estudianteEncargadoRepository.save(estudianteEncargado);
    }

    // ELIMINAR: esta tabla es solo una "unión" entre dos entidades, así que aquí sí se borra la fila.
    public void eliminar(Long id) {
        estudianteEncargadoRepository.delete(buscarPorId(id));
    }

    // Pasa los datos del DTO a la entidad, validando lo obligatorio.
    private void copiarDatos(EstudianteEncargado estudianteEncargado, EstudianteEncargadoDto dto) {
        Validaciones.textoObligatorio(dto.getTipoEncargado(), "tipoEncargado");
        estudianteEncargado.setTipoEncargado(dto.getTipoEncargado());
        Validaciones.textoObligatorio(dto.getParentesco(), "parentesco");
        estudianteEncargado.setParentesco(dto.getParentesco());
        estudianteEncargado.setObservaciones(dto.getObservaciones());
        estudianteEncargado.setEstudiante(estudianteRepository
                .findById(Validaciones.idObligatorio(dto.getEstudianteId(), "estudianteId"))
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con id: " + dto.getEstudianteId())));
        estudianteEncargado.setEncargado(encargadoRepository
                .findById(Validaciones.idObligatorio(dto.getEncargadoId(), "encargadoId"))
                .orElseThrow(() -> new RecursoNoEncontradoException("Encargado no encontrado con id: " + dto.getEncargadoId())));
    }
}
