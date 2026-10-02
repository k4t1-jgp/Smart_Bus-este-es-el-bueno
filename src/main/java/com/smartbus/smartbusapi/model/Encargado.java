package com.smartbus.smartbusapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Padre, madre o persona responsable de uno o más estudiantes. El parentesco NO va aquí sino en EstudianteEncargado, porque depende de cada estudiante.
 *
 * Esta clase es una ENTIDAD JPA: cada objeto Encargado corresponde a una fila de la tabla "encargado".
 * Spring Boot (con Hibernate) crea la tabla automáticamente a partir de esta clase.
 */
@Entity
@Table(name = "encargado")
public class Encargado {

    // Llave primaria. IDENTITY = MySQL la genera sola (AUTO_INCREMENT).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_encargado")
    private Long id;

    // Nombre completo.
    @Column(name = "nombre", nullable = false)
    private String nombre;

    // Teléfono de contacto.
    @Column(name = "telefono", nullable = false)
    private String telefono;

    // Correo electrónico (opcional).
    @Column(name = "correo")
    private String correo;

    // Dirección de residencia.
    @Column(name = "direccion", nullable = false)
    private String direccion;

    // true = activo; false = inactivo (eliminación lógica, no se borra el registro).
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    // Constructor vacío: JPA lo necesita para crear objetos al leer de la base de datos.
    public Encargado() {
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}
