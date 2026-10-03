package com.smartbus.smartbusapi.model;

import jakarta.persistence.*;

/**
 * Persona que puede entrar al sistema. Guarda el nombre de usuario, la contraseña (siempre encriptada con BCrypt) y el Rol que define qué puede hacer.
 *
 * Esta clase es una ENTIDAD JPA: cada objeto Usuario corresponde a una fila de la tabla "usuario".
 * Spring Boot (con Hibernate) crea la tabla automáticamente a partir de esta clase.
 */
@Entity
@Table(name = "usuario")
public class Usuario {

    // Llave primaria. IDENTITY = MySQL la genera sola (AUTO_INCREMENT).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    // Nombre con el que inicia sesión. No se puede repetir.
    @Column(name = "nombre_usuario", nullable = false, unique = true)
    private String nombreUsuario;

    // Contraseña ENCRIPTADA (BCrypt). Nunca se guarda en texto plano.
    @Column(name = "contrasena", nullable = false)
    private String contrasena;

    // true = activo; false = inactivo (eliminación lógica, no se borra el registro).
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    // RELACIÓN muchos-a-uno: Rol del usuario (define sus permisos).
    // En la tabla se guarda solo el id en la columna "id_rol" (llave foránea).
    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    // Constructor vacío: JPA lo necesita para crear objetos al leer de la base de datos.
    public Usuario() {
    }

    // ---------- Getters y setters ----------
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}
