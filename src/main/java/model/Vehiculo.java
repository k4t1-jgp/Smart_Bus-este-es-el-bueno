package model;

import jakarta.persistence.*;

/**
 * Bus de la flotilla. Su capacidad limita cuántos estudiantes se pueden asignar a una ruta.
 *
 * Esta clase es una ENTIDAD JPA: cada objeto Vehiculo corresponde a una fila de la tabla "vehiculo".
 * Spring Boot (con Hibernate) crea la tabla automáticamente a partir de esta clase.
 */
@Entity
@Table(name = "vehiculo")
public class Vehiculo {

    // Llave primaria. IDENTITY = MySQL la genera sola (AUTO_INCREMENT).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehiculo")
    private Long id;

    // Placa del vehículo. No se puede repetir.
    @Column(name = "placa", nullable = false, unique = true)
    private String placa;

    // Marca (Toyota, Hyundai...).
    @Column(name = "marca", nullable = false)
    private String marca;

    // Modelo.
    @Column(name = "modelo", nullable = false)
    private String modelo;

    // Color (opcional).
    @Column(name = "color")
    private String color;

    // Año del vehículo.
    @Column(name = "anio", nullable = false)
    private Integer anio;

    // Cantidad máxima de estudiantes que caben.
    @Column(name = "capacidad", nullable = false)
    private Integer capacidad;

    // Datos de la verificación/revisión (opcional).
    @Column(name = "verificacion")
    private String verificacion;

    // true = activo; false = inactivo (eliminación lógica, no se borra el registro).
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    // Constructor vacío: JPA lo necesita para crear objetos al leer de la base de datos.
    public Vehiculo() {
    }

    // ---------- Getters y setters ----------
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public String getVerificacion() {
        return verificacion;
    }

    public void setVerificacion(String verificacion) {
        this.verificacion = verificacion;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}
