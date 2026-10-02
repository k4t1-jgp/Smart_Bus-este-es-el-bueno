package com.apibus.smartbus.model;

import jakarta.persistence.*;

/**
 * Alumno que usa (o puede usar) el servicio de bus escolar.
 *
 * Esta clase es una ENTIDAD JPA: cada objeto Estudiante corresponde a una fila de la tabla "estudiante".
 * Spring Boot (con Hibernate) crea la tabla automáticamente a partir de esta clase.
 */
@Entity
@Table(name = "estudiante")
public class Estudiante {

    // Llave primaria. IDENTITY = MySQL la genera sola (AUTO_INCREMENT).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estudiante")
    private Long id;

    // Código o carné del estudiante. No se puede repetir.
    @Column(name = "carne", nullable = false, unique = true)
    private String carne;

    // Nombre completo.
    @Column(name = "nombre", nullable = false)
    private String nombre;

    // Grado que cursa.
    @Column(name = "grado", nullable = false)
    private String grado;

    // Jornada en la que estudia (texto, como en el UML).
    @Column(name = "jornada", nullable = false)
    private String jornada;

    // Dirección de residencia.
    @Column(name = "direccion", nullable = false)
    private String direccion;

    // true = activo; false = inactivo (eliminación lógica, no se borra el registro).
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    // Constructor vacío: JPA lo necesita para crear objetos al leer de la base de datos.
    public Estudiante() {
    }

    // ---------- Getters y setters ----------
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCarne() {
        return carne;
    }

    public void setCarne(String carne) {
        this.carne = carne;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getGrado() {
        return grado;
    }

    public void setGrado(String grado) {
        this.grado = grado;
    }

    public String getJornada() {
        return jornada;
    }

    public void setJornada(String jornada) {
        this.jornada = jornada;
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
