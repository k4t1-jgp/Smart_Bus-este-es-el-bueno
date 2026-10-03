package com.smartbus.smartbusapi.service;

import com.smartbus.smartbusapi.dto.EstudianteDto;
import com.smartbus.smartbusapi.exception.RecursoNoEncontradoException;
import com.smartbus.smartbusapi.model.Estudiante;
import com.smartbus.smartbusapi.repository.EstudianteRepository;
import com.smartbus.smartbusapi.util.Validaciones;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de Estudiante: aquí vive la LÓGICA del módulo.
 *
 * El controlador le pasa los datos, el servicio los valida, busca lo que necesite en los
 * repositorios y finalmente guarda. Si algo está mal, lanza una excepción con un mensaje claro
 * (ver el paquete "exception").
 */
@Service
public class EstudianteService {
    private final EstudianteRepository estudianteRepository;

    // Inyección de dependencias por constructor: Spring nos entrega los repositorios ya creados.
    public EstudianteService(EstudianteRepository estudianteRepository) {
        this.estudianteRepository = estudianteRepository;
    }

    // LISTAR TODOS
    public List<Estudiante> listarTodos() {
        return estudianteRepository.findAll();
    }

    // BUSCAR POR ID (si no existe, responde 404 con un mensaje claro)
    public Estudiante buscarPorId(Long id) {
        return estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con id: " + id));
    }

    // CREAR
    public Estudiante crear(EstudianteDto dto) {
        Estudiante estudiante = new Estudiante();
        copiarDatos(estudiante, dto);
        return estudianteRepository.save(estudiante);
    }

    // ACTUALIZAR (PUT reemplaza todos los datos, por eso se vuelven a validar)
    public Estudiante actualizar(Long id, EstudianteDto dto) {
        Estudiante estudiante = buscarPorId(id);
        copiarDatos(estudiante, dto);
        return estudianteRepository.save(estudiante);
    }

    // DESACTIVAR: eliminación LÓGICA. No se borra la fila, solo se marca estado = false
    // (así no se pierde el historial, tal como pide el acta de constitución).
    public void desactivar(Long id) {
        Estudiante estudiante = buscarPorId(id);
        estudiante.setEstado(false);
        estudianteRepository.save(estudiante);
    }

    // Pasa los datos del DTO a la entidad, validando lo obligatorio.
    private void copiarDatos(Estudiante estudiante, EstudianteDto dto) {
        Validaciones.textoObligatorio(dto.getCarne(), "carne");
        estudiante.setCarne(dto.getCarne());
        Validaciones.textoObligatorio(dto.getNombre(), "nombre");
        estudiante.setNombre(dto.getNombre());
        Validaciones.textoObligatorio(dto.getGrado(), "grado");
        estudiante.setGrado(dto.getGrado());
        Validaciones.textoObligatorio(dto.getJornada(), "jornada");
        estudiante.setJornada(dto.getJornada());
        Validaciones.textoObligatorio(dto.getDireccion(), "direccion");
        estudiante.setDireccion(dto.getDireccion());
        // Si no se envía el estado, se conserva el que ya tenía (por defecto: activo).
        if (dto.getEstado() != null) {
            estudiante.setEstado(dto.getEstado());
        }
    }

    // BUSCAR POR CARNÉ (parte de "búsqueda y filtrado" que pide el acta).
    // Encuentra carnés que CONTENGAN el texto recibido, sin importar mayúsculas.
    public List<Estudiante> buscarPorCarne(String carne) {
        return estudianteRepository.findByCarneContainingIgnoreCase(carne);
    }
}
