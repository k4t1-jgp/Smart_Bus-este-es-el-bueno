package dto.dto;

import java.time.LocalTime;

/**
 * DTO ("Data Transfer Object") de Ruta.
 *
 * Es la "forma" del JSON que llega en un POST o PUT. No es la entidad: aquí las relaciones se
 * reciben solo como el ID del objeto relacionado (por ejemplo "rolId": 1), y el servicio se
 * encarga de buscar el objeto completo en la base de datos.
 */
public class RutaDto {

    private String nombre;

    private String recorrido;

    private LocalTime horarioSalida;

    private LocalTime horarioLlegada;

    private Long jornadaId;

    private Long vehiculoId;

    private Long pilotoId;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRecorrido() {
        return recorrido;
    }

    public void setRecorrido(String recorrido) {
        this.recorrido = recorrido;
    }

    public LocalTime getHorarioSalida() {
        return horarioSalida;
    }

    public void setHorarioSalida(LocalTime horarioSalida) {
        this.horarioSalida = horarioSalida;
    }

    public LocalTime getHorarioLlegada() {
        return horarioLlegada;
    }

    public void setHorarioLlegada(LocalTime horarioLlegada) {
        this.horarioLlegada = horarioLlegada;
    }

    public Long getJornadaId() {
        return jornadaId;
    }

    public void setJornadaId(Long jornadaId) {
        this.jornadaId = jornadaId;
    }

    public Long getVehiculoId() {
        return vehiculoId;
    }

    public void setVehiculoId(Long vehiculoId) {
        this.vehiculoId = vehiculoId;
    }

    public Long getPilotoId() {
        return pilotoId;
    }

    public void setPilotoId(Long pilotoId) {
        this.pilotoId = pilotoId;
    }
}
