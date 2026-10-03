package com.smartbus.smartbusapi.model;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Dice que un Estudiante viaja en una Ruta y aborda en una Parada concreta.
 *
 * Esta clase es una ENTIDAD JPA: cada objeto Asignacion corresponde a una fila de la tabla "asignacion".
 * Spring Boot (con Hibernate) crea la tabla automáticamente a partir de esta clase.
 */
@Entity
@Table(name = "asignacion")
public class Asignacion {

    // Llave primaria. IDENTITY = MySQL la genera sola (AUTO_INCREMENT).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asignacion")
    private Long id;

    // Fecha en que se hizo la asignación (por defecto, hoy).
    @Column(name = "fecha")
    private LocalDate fecha;

    // true = asignación vigente; false = cancelada (se conserva como historial).
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    // RELACIÓN muchos-a-uno: Estudiante asignado.
    // En la tabla se guarda solo el id en la columna "id_estudiante" (llave foránea).
    @ManyToOne
    @JoinColumn(name = "id_estudiante", nullable = false)
    private Estudiante estudiante;

    // RELACIÓN muchos-a-uno: Ruta en la que viaja.
    // En la tabla se guarda solo el id en la columna "id_ruta" (llave foránea).
    @ManyToOne
    @JoinColumn(name = "id_ruta", nullable = false)
    private Ruta ruta;

    // RELACIÓN muchos-a-uno: Parada donde aborda.
    // En la tabla se guarda solo el id en la columna "id_parada" (llave foránea).
    @ManyToOne
    @JoinColumn(name = "id_parada", nullable = false)
    private Parada parada;

    // Constructor vacío: JPA lo necesita para crear objetos al leer de la base de datos.
    public Asignacion() {
    }

    // ---------- Getters y setters ----------
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public Ruta getRuta() {
        return ruta;
    }

    public void setRuta(Ruta ruta) {
        this.ruta = ruta;
    }

    public Parada getParada() {
        return parada;
    }

    public void setParada(Parada parada) {
        this.parada = parada;
    }
}
