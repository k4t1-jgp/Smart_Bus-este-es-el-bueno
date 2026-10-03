package dto.dto;

import java.time.LocalTime;

/**
 * DTO ("Data Transfer Object") de Jornada.
 *
 * Es la "forma" del JSON que llega en un POST o PUT. No es la entidad: aquí las relaciones se
 * reciben solo como el ID del objeto relacionado (por ejemplo "rolId": 1), y el servicio se
 * encarga de buscar el objeto completo en la base de datos.
 */
public class JornadaDto {

    private String nombre;

    private LocalTime horarioInicio;

    private LocalTime horarioFin;

    private Boolean estado;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalTime getHorarioInicio() {
        return horarioInicio;
    }

    public void setHorarioInicio(LocalTime horarioInicio) {
        this.horarioInicio = horarioInicio;
    }

    public LocalTime getHorarioFin() {
        return horarioFin;
    }

    public void setHorarioFin(LocalTime horarioFin) {
        this.horarioFin = horarioFin;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}
