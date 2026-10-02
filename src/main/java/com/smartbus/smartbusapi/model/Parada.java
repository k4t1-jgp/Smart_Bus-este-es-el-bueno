package com.smartbus.smartbusapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Punto donde los estudiantes abordan el bus. Una misma parada puede usarse en varias rutas.
 *
 * Esta clase es una ENTIDAD JPA: cada objeto Parada corresponde a una fila de la tabla "parada".
 * Spring Boot (con Hibernate) crea la tabla automáticamente a partir de esta clase.
 */
@Entity
@Table(name = "parada")
public class Parada {

    // Llave primaria. IDENTITY = MySQL la genera sola (AUTO_INCREMENT).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_parada")
    private Long id;

    // Nombre de la parada.
    @Column(name = "nombre_parada", nullable = false)
    private String nombre;

    // Dirección o referencia del lugar.
    @Column(name = "ubicacion", nullable = false)
    private String ubicacion;

    // true = activo; false = inactivo (eliminación lógica, no se borra el registro).
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    // Constructor vacío: JPA lo necesita para crear objetos al leer de la base de datos.
    public Parada() {
    }

    // ---------- Getters y setters ----------
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}
