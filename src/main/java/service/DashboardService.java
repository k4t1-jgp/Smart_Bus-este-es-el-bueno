package service;

import com.smartbus.smartbusapi.dto.CapacidadRutaDto;
import com.smartbus.smartbusapi.dto.DashboardDto;
import com.smartbus.smartbusapi.model.Ruta;
import com.smartbus.smartbusapi.repository.AsignacionRepository;
import com.smartbus.smartbusapi.repository.RutaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio del panel de indicadores (dashboard). Solo CONSULTA: junta datos de otros módulos
 * y los entrega en un solo objeto para que el frontend los muestre.
 */
@Service
public class DashboardService {

    private final RutaRepository rutaRepository;
    private final AsignacionRepository asignacionRepository;
    private final AsignacionService asignacionService;
    private final PagoService pagoService;

    public DashboardService(RutaRepository rutaRepository,
                            AsignacionRepository asignacionRepository,
                            AsignacionService asignacionService,
                            PagoService pagoService) {
        this.rutaRepository = rutaRepository;
        this.asignacionRepository = asignacionRepository;
        this.asignacionService = asignacionService;
        this.pagoService = pagoService;
    }

    public DashboardDto obtener() {
        List<Ruta> rutasActivas = rutaRepository.findByEstadoTrue();

        // Por cada ruta activa: capacidad del bus, asientos ocupados y asientos libres.
        List<CapacidadRutaDto> capacidades = new ArrayList<>();
        for (Ruta ruta : rutasActivas) {
            int capacidad = ruta.getVehiculo().getCapacidad();
            long ocupados = asignacionRepository.countByRutaIdAndEstadoTrue(ruta.getId());
            capacidades.add(new CapacidadRutaDto(ruta.getId(), ruta.getNombre(), capacidad, ocupados, capacidad - ocupados));
        }

        DashboardDto dashboard = new DashboardDto();
        dashboard.setRutasActivas(rutasActivas.size());
        dashboard.setEstudiantesConTransporte(asignacionService.estudiantesConTransporte().size());
        dashboard.setPagosPendientesMesActual(pagoService.pendientes(null, null).size());
        dashboard.setCapacidadPorRuta(capacidades);
        return dashboard;
    }
}
