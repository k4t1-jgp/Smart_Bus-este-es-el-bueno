package repository;

import com.smartbus.smartbusapi.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio de Pago: es la capa que habla directamente con la base de datos.
 *
 * Al extender JpaRepository ya obtenemos GRATIS los métodos findAll, findById, save y delete.
 * Los métodos extra de abajo NO se programan: Spring los entiende por su nombre y crea la consulta
 * SQL solo (ejemplo: findByEstadoTrue = "SELECT ... WHERE estado = true").
 */
public interface PagoRepository extends JpaRepository<Pago, Long> {

    // Consulta derivada del nombre del método.
    List<Pago> findByEstudianteId(Long estudianteId);

    // Consulta derivada del nombre del método.
    List<Pago> findByEstudianteIdAndMesAndAnioAndEstadoTrue(Long estudianteId, Integer mes, Integer anio);

    // Consulta derivada del nombre del método.
    List<Pago> findByMesAndAnioAndEstadoTrue(Integer mes, Integer anio);
}
