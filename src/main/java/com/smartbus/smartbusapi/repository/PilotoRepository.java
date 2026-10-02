package com.smartbus.smartbusapi.repository;

import com.smartbus.smartbusapi.model.Piloto;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio de Piloto: es la capa que habla directamente con la base de datos.
 *
 * Al extender JpaRepository ya obtenemos GRATIS los métodos findAll, findById, save y delete.
 * Los métodos extra de abajo NO se programan: Spring los entiende por su nombre y crea la consulta
 * SQL solo (ejemplo: findByEstadoTrue = "SELECT ... WHERE estado = true").
 */
public interface PilotoRepository extends JpaRepository<Piloto, Long> {
}
