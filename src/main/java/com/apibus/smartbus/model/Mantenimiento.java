package com.apibus.smartbus.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Periodo en que un vehículo está en el taller. Mientras dure, el vehículo NO se puede usar en rutas activas.
 *
 * Esta clase es una ENTIDAD JPA: cada objeto Mantenimiento corresponde a una fila de la tabla "mantenimiento".
 * Spring Boot (con Hibernate) crea la tabla automáticamente a partir de esta clase.
 */
@Entity
@Table(name = "mantenimiento")
public class Mantenimiento {

    // Llave primaria. IDENTITY = MySQL la genera sola (AUTO_INCREMENT).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mantenimiento")
    private Long id;

    // Día en que inicia el mantenimiento.
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    // Día en que termina (se llena al finalizar).
    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    // Tipo: preventivo, correctivo...
    @Column(name = "tipo", nullable = false)
    private String tipo;

    // Qué se le hizo o se le hará al vehículo.
    @Column(name = "detalle", nullable = false)
    private String detalle;

    // Costo del mantenimiento.
    @Column(name = "costo", nullable = false, precision = 10, scale = 2)
    private BigDecimal costo;

    // true = mantenimiento EN CURSO; false = ya finalizado.
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    // RELACIÓN muchos-a-uno: Vehículo que está en mantenimiento.
    // En la tabla se guarda solo el id en la columna "id_vehiculo" (llave foránea).
    @ManyToOne
    @JoinColumn(name = "id_vehiculo", nullable = false)
    private Vehiculo vehiculo;

    // Constructor vacío: JPA lo necesita para crear objetos al leer de la base de datos.
    public Mantenimiento() {
    }

    // ---------- Getters y setters ----------
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }
}
