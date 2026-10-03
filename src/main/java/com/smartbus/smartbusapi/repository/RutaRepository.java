package com.smartbus.smartbusapi.repository;

import com.smartbus.smartbusapi.model.Ruta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio de Ruta: es la capa que habla directamente con la base de datos.
 *
 * Al extender JpaRepository ya obtenemos GRATIS los métodos findAll, findById, save y delete.
 * Los métodos extra de abajo NO se programan: Spring los entiende por su nombre y crea la consulta
 * SQL solo (ejemplo: findByEstadoTrue = "SELECT ... WHERE estado = true").
 */
public interface RutaRepository extends JpaRepository<Ruta, Long> {

    // Consulta derivada del nombre del método.
    List<Ruta> findByEstadoTrue();

    // Consulta derivada del nombre del método.
    long countByEstadoTrue();

    // Consulta derivada del nombre del método.
    List<Ruta> findByVehiculoIdAndEstadoTrue(Long vehiculoId);

    // Consulta derivada del nombre del método.
    List<Ruta> findByPilotoIdAndEstadoTrue(Long pilotoId);
}
