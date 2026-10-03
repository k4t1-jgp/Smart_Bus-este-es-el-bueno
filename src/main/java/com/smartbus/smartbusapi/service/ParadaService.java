package com.smartbus.smartbusapi.service;

import com.smartbus.smartbusapi.dto.ParadaDto;
import com.smartbus.smartbusapi.exception.RecursoNoEncontradoException;
import com.smartbus.smartbusapi.model.Parada;
import com.smartbus.smartbusapi.repository.ParadaRepository;
import com.smartbus.smartbusapi.util.Validaciones;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de Parada: aquí vive la LÓGICA del módulo.
 *
 * El controlador le pasa los datos, el servicio los valida, busca lo que necesite en los
 * repositorios y finalmente guarda. Si algo está mal, lanza una excepción con un mensaje claro
 * (ver el paquete "exception").
 */
@Service
public class ParadaService {
    private final ParadaRepository paradaRepository;

    // Inyección de dependencias por constructor: Spring nos entrega los repositorios ya creados.
    public ParadaService(ParadaRepository paradaRepository) {
        this.paradaRepository = paradaRepository;
    }

    // LISTAR TODOS
    public List<Parada> listarTodos() {
        return paradaRepository.findAll();
    }

    // BUSCAR POR ID (si no existe, responde 404 con un mensaje claro)
    public Parada buscarPorId(Long id) {
        return paradaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Parada no encontrado con id: " + id));
    }

    // CREAR
    public Parada crear(ParadaDto dto) {
        Parada parada = new Parada();
        copiarDatos(parada, dto);
        return paradaRepository.save(parada);
    }

    // ACTUALIZAR (PUT reemplaza todos los datos, por eso se vuelven a validar)
    public Parada actualizar(Long id, ParadaDto dto) {
        Parada parada = buscarPorId(id);
        copiarDatos(parada, dto);
        return paradaRepository.save(parada);
    }

    // DESACTIVAR: eliminación LÓGICA. No se borra la fila, solo se marca estado = false
    // (así no se pierde el historial, tal como pide el acta de constitución).
    public void desactivar(Long id) {
        Parada parada = buscarPorId(id);
        parada.setEstado(false);
        paradaRepository.save(parada);
    }

    // Pasa los datos del DTO a la entidad, validando lo obligatorio.
    private void copiarDatos(Parada parada, ParadaDto dto) {
        Validaciones.textoObligatorio(dto.getNombre(), "nombre");
        parada.setNombre(dto.getNombre());
        Validaciones.textoObligatorio(dto.getUbicacion(), "ubicacion");
        parada.setUbicacion(dto.getUbicacion());
        // Si no se envía el estado, se conserva el que ya tenía (por defecto: activo).
        if (dto.getEstado() != null) {
            parada.setEstado(dto.getEstado());
        }
    }
}
