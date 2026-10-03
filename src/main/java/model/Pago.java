package model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Pago de la cuota mensual de transporte de un estudiante. Se registra por mes y año, y no se permiten pagos duplicados del mismo periodo.
 *
 * Esta clase es una ENTIDAD JPA: cada objeto Pago corresponde a una fila de la tabla "pago".
 * Spring Boot (con Hibernate) crea la tabla automáticamente a partir de esta clase.
 */
@Entity
@Table(name = "pago")
public class Pago {

    // Llave primaria. IDENTITY = MySQL la genera sola (AUTO_INCREMENT).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Long id;

    // Monto pagado (BigDecimal porque es dinero).
    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    // Mes que se paga (1 a 12).
    @Column(name = "mes", nullable = false)
    private Integer mes;

    // Año que se paga.
    @Column(name = "anio", nullable = false)
    private Integer anio;

    // Día en que se recibió el pago (por defecto, hoy).
    @Column(name = "fecha_pago")
    private LocalDate fechaPago;

    // true = pago vigente; false = pago anulado (se conserva como historial).
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    // RELACIÓN muchos-a-uno: Estudiante que paga.
    // En la tabla se guarda solo el id en la columna "id_estudiante" (llave foránea).
    @ManyToOne
    @JoinColumn(name = "id_estudiante", nullable = false)
    private Estudiante estudiante;

    // Constructor vacío: JPA lo necesita para crear objetos al leer de la base de datos.
    public Pago() {
    }

    // ---------- Getters y setters ----------
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
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
}
