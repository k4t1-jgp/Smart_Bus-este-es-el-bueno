package repository;

import com.smartbus.smartbusapi.model.Encargado;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio de Encargado: es la capa que habla directamente con la base de datos.
 *
 * Al extender JpaRepository ya obtenemos GRATIS los métodos findAll, findById, save y delete.
 * Los métodos extra de abajo NO se programan: Spring los entiende por su nombre y crea la consulta
 * SQL solo (ejemplo: findByEstadoTrue = "SELECT ... WHERE estado = true").
 */
public interface EncargadoRepository extends JpaRepository<Encargado, Long> {
}
