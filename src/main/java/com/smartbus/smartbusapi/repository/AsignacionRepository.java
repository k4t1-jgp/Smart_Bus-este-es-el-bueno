package com.smartbus.smartbusapi.repository;

import com.smartbus.smartbusapi.model.Asignacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio de Asignacion: es la capa que habla directamente con la base de datos.
 *
 * Al extender JpaRepository ya obtenemos GRATIS los métodos findAll, findById, save y delete.
 * Los métodos extra de abajo NO se programan: Spring los entiende por su nombre y crea la consulta
 * SQL solo (ejemplo: findByEstadoTrue = "SELECT ... WHERE estado = true").
 */
public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {

    // Consulta derivada del nombre del método.
    List<Asignacion> findByEstadoTrue();

    // Consulta derivada del nombre del método.
    List<Asignacion> findByRutaIdAndEstadoTrue(Long rutaId);

    // Consulta derivada del nombre del método.
    List<Asignacion> findByEstudianteId(Long estudianteId);

    // Consulta derivada del nombre del método.
    List<Asignacion> findByEstudianteIdAndEstadoTrue(Long estudianteId);

    // Consulta derivada del nombre del método.
    long countByRutaIdAndEstadoTrue(Long rutaId);

    // Consulta derivada del nombre del método.
    long countByRutaIdAndParadaIdAndEstadoTrue(Long rutaId, Long paradaId);
}
