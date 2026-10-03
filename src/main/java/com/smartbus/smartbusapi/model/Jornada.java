package com.smartbus.smartbusapi.model;

import jakarta.persistence.*;

import java.time.LocalTime;

/**
 * Horario general de clases (por ejemplo Matutina o Vespertina). Cada Ruta pertenece a una Jornada.
 *
 * Esta clase es una ENTIDAD JPA: cada objeto Jornada corresponde a una fila de la tabla "jornada".
 * Spring Boot (con Hibernate) crea la tabla automáticamente a partir de esta clase.
 */
@Entity
@Table(name = "jornada")
public class Jornada {

    // Llave primaria. IDENTITY = MySQL la genera sola (AUTO_INCREMENT).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_jornada")
    private Long id;

    // Nombre de la jornada (Matutina, Vespertina...).
    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;

    // Hora en que inicia la jornada.
    @Column(name = "horario_inicio", nullable = false)
    private LocalTime horarioInicio;

    // Hora en que termina la jornada.
    @Column(name = "horario_fin", nullable = false)
    private LocalTime horarioFin;

    // true = activo; false = inactivo (eliminación lógica, no se borra el registro).
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    // Constructor vacío: JPA lo necesita para crear objetos al leer de la base de datos.
    public Jornada() {
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

    public LocalTime getHorarioInicio() {
        return horarioInicio;
    }

    public void setHorarioInicio(LocalTime horarioInicio) {
        this.horarioInicio = horarioInicio;
    }

    public LocalTime getHorarioFin() {
        return horarioFin;
    }

    public void setHorarioFin(LocalTime horarioFin) {
        this.horarioFin = horarioFin;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}
