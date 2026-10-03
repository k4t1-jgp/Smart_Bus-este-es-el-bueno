package com.smartbus.smartbusapi.service;

import com.smartbus.smartbusapi.dto.PilotoDto;
import com.smartbus.smartbusapi.exception.RecursoNoEncontradoException;
import com.smartbus.smartbusapi.model.Piloto;
import com.smartbus.smartbusapi.repository.PilotoRepository;
import com.smartbus.smartbusapi.util.Validaciones;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de Piloto: aquí vive la LÓGICA del módulo.
 *
 * El controlador le pasa los datos, el servicio los valida, busca lo que necesite en los
 * repositorios y finalmente guarda. Si algo está mal, lanza una excepción con un mensaje claro
 * (ver el paquete "exception").
 */
@Service
public class PilotoService {
    private final PilotoRepository pilotoRepository;

    // Inyección de dependencias por constructor: Spring nos entrega los repositorios ya creados.
    public PilotoService(PilotoRepository pilotoRepository) {
        this.pilotoRepository = pilotoRepository;
    }

    // LISTAR TODOS
    public List<Piloto> listarTodos() {
        return pilotoRepository.findAll();
    }

    // BUSCAR POR ID (si no existe, responde 404 con un mensaje claro)
    public Piloto buscarPorId(Long id) {
        return pilotoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Piloto no encontrado con id: " + id));
    }

    // CREAR
    public Piloto crear(PilotoDto dto) {
        Piloto piloto = new Piloto();
        copiarDatos(piloto, dto);
        return pilotoRepository.save(piloto);
    }

    // ACTUALIZAR (PUT reemplaza todos los datos, por eso se vuelven a validar)
    public Piloto actualizar(Long id, PilotoDto dto) {
        Piloto piloto = buscarPorId(id);
        copiarDatos(piloto, dto);
        return pilotoRepository.save(piloto);
    }

    // DESACTIVAR: eliminación LÓGICA. No se borra la fila, solo se marca estado = false
    // (así no se pierde el historial, tal como pide el acta de constitución).
    public void desactivar(Long id) {
        Piloto piloto = buscarPorId(id);
        piloto.setEstado(false);
        pilotoRepository.save(piloto);
    }

    // Pasa los datos del DTO a la entidad, validando lo obligatorio.
    private void copiarDatos(Piloto piloto, PilotoDto dto) {
        Validaciones.textoObligatorio(dto.getNombre(), "nombre");
        piloto.setNombre(dto.getNombre());
        Validaciones.textoObligatorio(dto.getTelefono(), "telefono");
        piloto.setTelefono(dto.getTelefono());
        Validaciones.textoObligatorio(dto.getNumeroLicencia(), "numeroLicencia");
        piloto.setNumeroLicencia(dto.getNumeroLicencia());
        Validaciones.textoObligatorio(dto.getTipoLicencia(), "tipoLicencia");
        piloto.setTipoLicencia(dto.getTipoLicencia());
        Validaciones.objetoObligatorio(dto.getFechaNacimiento(), "fechaNacimiento");
        piloto.setFechaNacimiento(dto.getFechaNacimiento());
        Validaciones.objetoObligatorio(dto.getFechaVencimientoLicencia(), "fechaVencimientoLicencia");
        piloto.setFechaVencimientoLicencia(dto.getFechaVencimientoLicencia());
        // Si no se envía el estado, se conserva el que ya tenía (por defecto: activo).
        if (dto.getEstado() != null) {
            piloto.setEstado(dto.getEstado());
        }
    }
}
