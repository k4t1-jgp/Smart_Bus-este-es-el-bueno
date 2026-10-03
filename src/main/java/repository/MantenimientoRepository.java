package repository;

import com.smartbus.smartbusapi.model.Mantenimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio de Mantenimiento: es la capa que habla directamente con la base de datos.
 *
 * Al extender JpaRepository ya obtenemos GRATIS los métodos findAll, findById, save y delete.
 * Los métodos extra de abajo NO se programan: Spring los entiende por su nombre y crea la consulta
 * SQL solo (ejemplo: findByEstadoTrue = "SELECT ... WHERE estado = true").
 */
public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Long> {

    // Consulta derivada del nombre del método.
    List<Mantenimiento> findByVehiculoId(Long vehiculoId);

    // Consulta derivada del nombre del método.
    boolean existsByVehiculoIdAndEstadoTrue(Long vehiculoId);
}
