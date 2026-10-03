package com.smartbus.smartbusapi.model;

import jakarta.persistence.*;

/**
 * Un rol define qué tipo de usuario es alguien (Administrador, Coordinador de transportes, Encargado de pagos, etc.). Cada Usuario pertenece a un Rol.
 *
 * Esta clase es una ENTIDAD JPA: cada objeto Rol corresponde a una fila de la tabla "rol".
 * Spring Boot (con Hibernate) crea la tabla automáticamente a partir de esta clase.
 */
@Entity
@Table(name = "rol")
public class Rol {

    // Llave primaria. IDENTITY = MySQL la genera sola (AUTO_INCREMENT).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Long id;

    // Nombre del rol. No se puede repetir.
    @Column(name = "nombre_rol", nullable = false, unique = true)
    private String nombre;

    // Explicación corta de para qué sirve el rol.
    @Column(name = "descripcion")
    private String descripcion;

    // true = activo; false = inactivo (eliminación lógica, no se borra el registro).
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    // Constructor vacío: JPA lo necesita para crear objetos al leer de la base de datos.
    public Rol() {
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}
