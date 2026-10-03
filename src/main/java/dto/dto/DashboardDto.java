package dto.dto;

import java.util.List;

/**
 * Datos del panel de indicadores (dashboard) que pide el acta:
 * rutas activas, estudiantes con transporte, pagos pendientes y capacidad disponible por ruta.
 */
public class DashboardDto {

    private int rutasActivas;
    private int estudiantesConTransporte;
    private int pagosPendientesMesActual;
    private List<CapacidadRutaDto> capacidadPorRuta;

    public int getRutasActivas() {
        return rutasActivas;
    }

    public void setRutasActivas(int rutasActivas) {
        this.rutasActivas = rutasActivas;
    }

    public int getEstudiantesConTransporte() {
        return estudiantesConTransporte;
    }

    public void setEstudiantesConTransporte(int estudiantesConTransporte) {
        this.estudiantesConTransporte = estudiantesConTransporte;
    }

    public int getPagosPendientesMesActual() {
        return pagosPendientesMesActual;
    }

    public void setPagosPendientesMesActual(int pagosPendientesMesActual) {
        this.pagosPendientesMesActual = pagosPendientesMesActual;
    }

    public List<CapacidadRutaDto> getCapacidadPorRuta() {
        return capacidadPorRuta;
    }

    public void setCapacidadPorRuta(List<CapacidadRutaDto> capacidadPorRuta) {
        this.capacidadPorRuta = capacidadPorRuta;
    }
}
