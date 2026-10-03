package model;

import jakarta.persistence.*;

import java.time.LocalTime;

/**
 * Recorrido del bus: tiene un vehículo, un piloto, una jornada, un horario y varias paradas (ver RutaParada). Una ruta nace INACTIVA y se activa cuando cumple las reglas del negocio.
 *
 * Esta clase es una ENTIDAD JPA: cada objeto Ruta corresponde a una fila de la tabla "ruta".
 * Spring Boot (con Hibernate) crea la tabla automáticamente a partir de esta clase.
 */
@Entity
@Table(name = "ruta")
public class Ruta {

    // Llave primaria. IDENTITY = MySQL la genera sola (AUTO_INCREMENT).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ruta")
    private Long id;

    // Nombre de la ruta.
    @Column(name = "nombre_ruta", nullable = false)
    private String nombre;

    // Descripción general del recorrido (opcional).
    @Column(name = "recorrido")
    private String recorrido;

    // Hora en que sale el bus.
    @Column(name = "horario_salida", nullable = false)
    private LocalTime horarioSalida;

    // Hora en que llega al colegio.
    @Column(name = "horario_llegada", nullable = false)
    private LocalTime horarioLlegada;

    // true = ruta activa (operando); false = inactiva o en preparación.
    @Column(name = "estado", nullable = false)
    private Boolean estado = false;

    // RELACIÓN muchos-a-uno: Jornada a la que pertenece la ruta.
    // En la tabla se guarda solo el id en la columna "id_jornada" (llave foránea).
    @ManyToOne
    @JoinColumn(name = "id_jornada", nullable = false)
    private Jornada jornada;

    // RELACIÓN muchos-a-uno: Bus que hace la ruta.
    // En la tabla se guarda solo el id en la columna "id_vehiculo" (llave foránea).
    @ManyToOne
    @JoinColumn(name = "id_vehiculo", nullable = false)
    private Vehiculo vehiculo;

    // RELACIÓN muchos-a-uno: Piloto que conduce la ruta.
    // En la tabla se guarda solo el id en la columna "id_piloto" (llave foránea).
    @ManyToOne
    @JoinColumn(name = "id_piloto", nullable = false)
    private Piloto piloto;

    // Constructor vacío: JPA lo necesita para crear objetos al leer de la base de datos.
    public Ruta() {
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

    public String getRecorrido() {
        return recorrido;
    }

    public void setRecorrido(String recorrido) {
        this.recorrido = recorrido;
    }

    public LocalTime getHorarioSalida() {
        return horarioSalida;
    }

    public void setHorarioSalida(LocalTime horarioSalida) {
        this.horarioSalida = horarioSalida;
    }

    public LocalTime getHorarioLlegada() {
        return horarioLlegada;
    }

    public void setHorarioLlegada(LocalTime horarioLlegada) {
        this.horarioLlegada = horarioLlegada;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Jornada getJornada() {
        return jornada;
    }

    public void setJornada(Jornada jornada) {
        this.jornada = jornada;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public Piloto getPiloto() {
        return piloto;
    }

    public void setPiloto(Piloto piloto) {
        this.piloto = piloto;
    }
}
