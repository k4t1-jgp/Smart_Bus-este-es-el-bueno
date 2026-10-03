package repository;

import com.smartbus.smartbusapi.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio de Usuario: es la capa que habla directamente con la base de datos.
 *
 * Al extender JpaRepository ya obtenemos GRATIS los métodos findAll, findById, save y delete.
 * Los métodos extra de abajo NO se programan: Spring los entiende por su nombre y crea la consulta
 * SQL solo (ejemplo: findByEstadoTrue = "SELECT ... WHERE estado = true").
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Consulta derivada del nombre del método.
    boolean existsByNombreUsuario(String nombreUsuario);

    // Consulta derivada del nombre del método.
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
}
