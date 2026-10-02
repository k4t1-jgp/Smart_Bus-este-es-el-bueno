package com.apibus.smartbus.model;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Conductor de los buses. Se guarda su licencia porque una de las reglas del negocio dice que la licencia debe estar vigente para conducir una ruta.
 *
 * Esta clase es una ENTIDAD JPA: cada objeto Piloto corresponde a una fila de la tabla "piloto".
 * Spring Boot (con Hibernate) crea la tabla automáticamente a partir de esta clase.
 */
@Entity
@Table(name = "piloto")
public class Piloto {

    // Llave primaria. IDENTITY = MySQL la genera sola (AUTO_INCREMENT).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_piloto")
    private Long id;

    // Nombre completo.
    @Column(name = "nombre", nullable = false)
    private String nombre;

    // Teléfono de contacto.
    @Column(name = "telefono", nullable = false)
    private String telefono;

    // Número de licencia. No se puede repetir.
    @Column(name = "numero_licencia", nullable = false, unique = true)
    private String numeroLicencia;

    // Tipo de licencia (A, B, C...).
    @Column(name = "tipo_licencia", nullable = false)
    private String tipoLicencia;

    // Fecha de nacimiento.
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    // CAMPO NUEVO: fecha en que vence la licencia. Sin este dato no se puede aplicar la regla 'la licencia debe estar vigente'.
    @Column(name = "fecha_vencimiento_licencia", nullable = false)
    private LocalDate fechaVencimientoLicencia;

    // true = activo; false = inactivo (eliminación lógica, no se borra el registro).
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    // Constructor vacío: JPA lo necesita para crear objetos al leer de la base de datos.
    public Piloto() {
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

    public String getNumeroLicencia() {
        return numeroLicencia;
    }

    public void setNumeroLicencia(String numeroLicencia) {
        this.numeroLicencia = numeroLicencia;
    }

    public String getTipoLicencia() {
        return tipoLicencia;
    }

    public void setTipoLicencia(String tipoLicencia) {
        this.tipoLicencia = tipoLicencia;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public LocalDate getFechaVencimientoLicencia() {
        return fechaVencimientoLicencia;
    }

    public void setFechaVencimientoLicencia(LocalDate fechaVencimientoLicencia) {
        this.fechaVencimientoLicencia = fechaVencimientoLicencia;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}
