package com.smartbus.smartbusapi.service;

import com.smartbus.smartbusapi.dto.RolDto;
import com.smartbus.smartbusapi.exception.RecursoNoEncontradoException;
import com.smartbus.smartbusapi.model.Rol;
import com.smartbus.smartbusapi.repository.RolRepository;
import com.smartbus.smartbusapi.util.Validaciones;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Servicio de Rol: aquí vive la LÓGICA del módulo.
 *
 * El controlador le pasa los datos, el servicio los valida, busca lo que necesite en los
 * repositorios y finalmente guarda. Si algo está mal, lanza una excepción con un mensaje claro
 * (ver el paquete "exception").
 */
@Service
public class RolService {
    private final RolRepository rolRepository;

    // Inyección de dependencias por constructor: Spring nos entrega los repositorios ya creados.
    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    // LISTAR TODOS
    public List<Rol> listarTodos() {
        return rolRepository.findAll();
    }

    // BUSCAR POR ID (si no existe, responde 404 con un mensaje claro)
    public Rol buscarPorId(Long id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol no encontrado con id: " + id));
    }

    // CREAR
    public Rol crear(RolDto dto) {
        Rol rol = new Rol();
        copiarDatos(rol, dto);
        return rolRepository.save(rol);
    }

    // ACTUALIZAR (PUT reemplaza todos los datos, por eso se vuelven a validar)
    public Rol actualizar(Long id, RolDto dto) {
        Rol rol = buscarPorId(id);
        copiarDatos(rol, dto);
        return rolRepository.save(rol);
    }

    // DESACTIVAR: eliminación LÓGICA. No se borra la fila, solo se marca estado = false
    // (así no se pierde el historial, tal como pide el acta de constitución).
    public void desactivar(Long id) {
        Rol rol = buscarPorId(id);
        rol.setEstado(false);
        rolRepository.save(rol);
    }

    // Pasa los datos del DTO a la entidad, validando lo obligatorio.
    private void copiarDatos(Rol rol, RolDto dto) {
        Validaciones.textoObligatorio(dto.getNombre(), "nombre");
        rol.setNombre(dto.getNombre());
        rol.setDescripcion(dto.getDescripcion());
        // Si no se envía el estado, se conserva el que ya tenía (por defecto: activo).
        if (dto.getEstado() != null) {
            rol.setEstado(dto.getEstado());
        }
    }
}
