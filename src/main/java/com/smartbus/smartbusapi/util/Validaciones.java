package com.smartbus.smartbusapi.util;

import com.smartbus.smartbusapi.exception.ReglaNegocioException;

/**
 * Pequeñas validaciones reutilizables para no repetir los mismos "if" en todos los servicios.
 * Si el dato no cumple, lanzan ReglaNegocioException con un mensaje claro.
 */
public final class Validaciones {

    // Clase de utilidades: no se crean objetos de ella.
    private Validaciones() {
    }

    // El texto no puede ser null ni estar vacío o en blanco.
    public static void textoObligatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new ReglaNegocioException("El campo '" + campo + "' es obligatorio");
        }
    }

    // Cualquier dato (número, fecha, hora...) no puede ser null.
    public static void objetoObligatorio(Object valor, String campo) {
        if (valor == null) {
            throw new ReglaNegocioException("El campo '" + campo + "' es obligatorio");
        }
    }

    // Igual que el anterior pero devuelve el id, para usarlo directo en findById(...).
    public static Long idObligatorio(Long id, String campo) {
        if (id == null) {
            throw new ReglaNegocioException("El campo '" + campo + "' es obligatorio");
        }
        return id;
    }
}
