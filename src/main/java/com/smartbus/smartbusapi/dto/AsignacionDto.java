package com.smartbus.smartbusapi.dto;

import java.time.LocalDate;

/**
 * DTO ("Data Transfer Object") de Asignacion.
 *
 * Es la "forma" del JSON que llega en un POST o PUT. No es la entidad: aquí las relaciones se
 * reciben solo como el ID del objeto relacionado (por ejemplo "rolId": 1), y el servicio se
 * encarga de buscar el objeto completo en la base de datos.
 */
public class AsignacionDto {

    private LocalDate fecha;

    private Long estudianteId;

    private Long rutaId;

    private Long paradaId;

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Long getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(Long estudianteId) {
        this.estudianteId = estudianteId;
    }

    public Long getRutaId() {
        return rutaId;
    }

    public void setRutaId(Long rutaId) {
        this.rutaId = rutaId;
    }

    public Long getParadaId() {
        return paradaId;
    }

    public void setParadaId(Long paradaId) {
        this.paradaId = paradaId;
    }
}
