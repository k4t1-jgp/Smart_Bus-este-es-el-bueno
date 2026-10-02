package com.smartbus.smartbusapi.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Atrapa las excepciones lanzadas en cualquier parte del sistema y las convierte en una respuesta
 * JSON ordenada, por ejemplo:
 *
 *   { "estado": 400, "error": "Bad Request", "mensaje": "El bus ya superó su capacidad" }
 *
 * Sin esta clase, el usuario vería un error 500 genérico y sin explicación.
 */
@RestControllerAdvice
public class ManejadorGlobalExcepciones {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> manejarNoEncontrado(RecursoNoEncontradoException ex) {
        return construir(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<Map<String, Object>> manejarReglaNegocio(ReglaNegocioException ex) {
        return construir(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(NoAutorizadoException.class)
    public ResponseEntity<Map<String, Object>> manejarNoAutorizado(NoAutorizadoException ex) {
        return construir(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    // La base de datos rechazó el guardado: valor repetido en un campo único (carné, placa...)
    // o intento de borrar algo que está relacionado con otros datos.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> manejarIntegridad(DataIntegrityViolationException ex) {
        return construir(HttpStatus.CONFLICT,
                "No se pudo guardar: hay un valor repetido (carné, placa, nombre...) o el registro "
                        + "está relacionado con otros datos.");
    }

    private ResponseEntity<Map<String, Object>> construir(HttpStatus estado, String mensaje) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("estado", estado.value());
        cuerpo.put("error", estado.getReasonPhrase());
        cuerpo.put("mensaje", mensaje);
        return ResponseEntity.status(estado).body(cuerpo);
    }
}
