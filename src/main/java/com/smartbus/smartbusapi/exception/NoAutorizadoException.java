package com.smartbus.smartbusapi.exception;

/**
 * Se lanza cuando el inicio de sesión falla (usuario o contraseña incorrectos, usuario inactivo).
 * Se convierte en una respuesta HTTP 401.
 */
public class NoAutorizadoException extends RuntimeException {

    public NoAutorizadoException(String mensaje) {
        super(mensaje);
    }
}
