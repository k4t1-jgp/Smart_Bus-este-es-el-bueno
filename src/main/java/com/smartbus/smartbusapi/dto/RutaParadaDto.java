package com.smartbus.smartbusapi.dto;

/**
 * DTO ("Data Transfer Object") de RutaParada.
 *
 * Es la "forma" del JSON que llega en un POST o PUT. No es la entidad: aquí las relaciones se
 * reciben solo como el ID del objeto relacionado (por ejemplo "rolId": 1), y el servicio se
 * encarga de buscar el objeto completo en la base de datos.
 */
public class RutaParadaDto {

    private Integer orden;

    private Long rutaId;

    private Long paradaId;

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
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
