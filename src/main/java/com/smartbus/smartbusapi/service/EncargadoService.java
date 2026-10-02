package com.smartbus.smartbusapi.service;

import com.smartbus.smartbusapi.dto.EncargadoDto;
import com.smartbus.smartbusapi.exception.RecursoNoEncontradoException;
import com.smartbus.smartbusapi.model.Encargado;
import com.smartbus.smartbusapi.repository.EncargadoRepository;
import com.smartbus.smartbusapi.util.Validaciones;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Servicio de Encargado: aquí vive la LÓGICA del módulo.
 *
 * El controlador le pasa los datos, el servicio los valida, busca lo que necesite en los
 * repositorios y finalmente guarda. Si algo está mal, lanza una excepción con un mensaje claro
 * (ver el paquete "exception").
 */
@Service
public class EncargadoService {
    private final EncargadoRepository encargadoRepository;

    // Inyección de dependencias por constructor: Spring nos entrega los repositorios ya creados.
    public EncargadoService(EncargadoRepository encargadoRepository) {
        this.encargadoRepository = encargadoRepository;
    }

    // LISTAR TODOS
    public List<Encargado> listarTodos() {
        return encargadoRepository.findAll();
    }

    // BUSCAR POR ID (si no existe, responde 404 con un mensaje claro)
    public Encargado buscarPorId(Long id) {
        return encargadoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Encargado no encontrado con id: " + id));
    }

    // CREAR
    public Encargado crear(EncargadoDto dto) {
        Encargado encargado = new Encargado();
        copiarDatos(encargado, dto);
        return encargadoRepository.save(encargado);
    }

    // ACTUALIZAR (PUT reemplaza todos los datos, por eso se vuelven a validar)
    public Encargado actualizar(Long id, EncargadoDto dto) {
        Encargado encargado = buscarPorId(id);
        copiarDatos(encargado, dto);
        return encargadoRepository.save(encargado);
    }

    // DESACTIVAR: eliminación LÓGICA. No se borra la fila, solo se marca estado = false
    // (así no se pierde el historial, tal como pide el acta de constitución).
    public void desactivar(Long id) {
        Encargado encargado = buscarPorId(id);
        encargado.setEstado(false);
        encargadoRepository.save(encargado);
    }

    // Pasa los datos del DTO a la entidad, validando lo obligatorio.
    private void copiarDatos(Encargado encargado, EncargadoDto dto) {
        Validaciones.textoObligatorio(dto.getNombre(), "nombre");
        encargado.setNombre(dto.getNombre());
        Validaciones.textoObligatorio(dto.getTelefono(), "telefono");
        encargado.setTelefono(dto.getTelefono());
        encargado.setCorreo(dto.getCorreo());
        Validaciones.textoObligatorio(dto.getDireccion(), "direccion");
        encargado.setDireccion(dto.getDireccion());
        // Si no se envía el estado, se conserva el que ya tenía (por defecto: activo).
        if (dto.getEstado() != null) {
            encargado.setEstado(dto.getEstado());
        }
    }
}
