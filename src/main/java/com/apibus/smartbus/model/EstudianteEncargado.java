package com.apibus.smartbus.model;

import jakarta.persistence.*;

/**
 * Tabla intermedia que une a un Estudiante con un Encargado (relación muchos a muchos). Aquí se guarda el parentesco, el tipo de encargado y observaciones.
 *
 * Esta clase es una ENTIDAD JPA: cada objeto EstudianteEncargado corresponde a una fila de la tabla "estudiante_encargado".
 * Spring Boot (con Hibernate) crea la tabla automáticamente a partir de esta clase.
 */
@Entity
@Table(name = "estudiante_encargado",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_estudiante", "id_encargado"}))
public class EstudianteEncargado {

    // Llave primaria. IDENTITY = MySQL la genera sola (AUTO_INCREMENT).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estudiante_encargado")
    private Long id;

    // Ejemplo: Principal, Secundario, Emergencia.
    @Column(name = "tipo_encargado", nullable = false)
    private String tipoEncargado;

    // Ejemplo: Padre, Madre, Tío, Abuela.
    @Column(name = "parentesco", nullable = false)
    private String parentesco;

    // Notas adicionales (opcional).
    @Column(name = "observaciones")
    private String observaciones;

    // RELACIÓN muchos-a-uno: Estudiante al que se le asigna el encargado.
    // En la tabla se guarda solo el id en la columna "id_estudiante" (llave foránea).
    @ManyToOne
    @JoinColumn(name = "id_estudiante", nullable = false)
    private Estudiante estudiante;

    // RELACIÓN muchos-a-uno: Encargado del estudiante.
    // En la tabla se guarda solo el id en la columna "id_encargado" (llave foránea).
    @ManyToOne
    @JoinColumn(name = "id_encargado", nullable = false)
    private Encargado encargado;

    // Constructor vacío: JPA lo necesita para crear objetos al leer de la base de datos.
    public EstudianteEncargado() {
    }

    // ---------- Getters y setters ----------
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoEncargado() {
        return tipoEncargado;
    }

    public void setTipoEncargado(String tipoEncargado) {
        this.tipoEncargado = tipoEncargado;
    }

    public String getParentesco() {
        return parentesco;
    }

    public void setParentesco(String parentesco) {
        this.parentesco = parentesco;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public Encargado getEncargado() {
        return encargado;
    }

    public void setEncargado(Encargado encargado) {
        this.encargado = encargado;
    }
}
