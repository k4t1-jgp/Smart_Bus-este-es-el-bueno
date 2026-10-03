package com.smartbus.smartbusapi.exception;

/**
 * Se lanza cuando una petición rompe una regla del negocio o falta un dato obligatorio
 * (ejemplo: "El bus ya superó su capacidad"). Se convierte en una respuesta HTTP 400.
 */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
