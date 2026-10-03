package dto.dto;

/**
 * Capacidad de una ruta para el panel de indicadores (dashboard):
 * cuántos asientos tiene el bus, cuántos están ocupados y cuántos quedan libres.
 */
public class CapacidadRutaDto {

    private Long rutaId;
    private String rutaNombre;
    private int capacidad;
    private long ocupados;
    private long disponibles;

    public CapacidadRutaDto(Long rutaId, String rutaNombre, int capacidad, long ocupados, long disponibles) {
        this.rutaId = rutaId;
        this.rutaNombre = rutaNombre;
        this.capacidad = capacidad;
        this.ocupados = ocupados;
        this.disponibles = disponibles;
    }

    public Long getRutaId() {
        return rutaId;
    }

    public String getRutaNombre() {
        return rutaNombre;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public long getOcupados() {
        return ocupados;
    }

    public long getDisponibles() {
        return disponibles;
    }
}
