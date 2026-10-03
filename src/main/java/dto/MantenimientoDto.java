package dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO ("Data Transfer Object") de Mantenimiento.
 *
 * Es la "forma" del JSON que llega en un POST o PUT. No es la entidad: aquí las relaciones se
 * reciben solo como el ID del objeto relacionado (por ejemplo "rolId": 1), y el servicio se
 * encarga de buscar el objeto completo en la base de datos.
 */
public class MantenimientoDto {

    private LocalDate fechaInicio;

    private String tipo;

    private String detalle;

    private BigDecimal costo;

    private Long vehiculoId;

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }

    public Long getVehiculoId() {
        return vehiculoId;
    }

    public void setVehiculoId(Long vehiculoId) {
        this.vehiculoId = vehiculoId;
    }
}
