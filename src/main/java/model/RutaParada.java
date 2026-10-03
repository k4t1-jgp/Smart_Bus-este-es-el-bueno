package model;

import jakarta.persistence.*;

/**
 * Tabla intermedia (clase asociativa del UML) que dice QUÉ paradas tiene una ruta y en QUÉ ORDEN se visitan.
 *
 * Esta clase es una ENTIDAD JPA: cada objeto RutaParada corresponde a una fila de la tabla "ruta_parada".
 * Spring Boot (con Hibernate) crea la tabla automáticamente a partir de esta clase.
 */
@Entity
@Table(name = "ruta_parada",
       uniqueConstraints = @UniqueConstraint(columnNames = {"id_ruta", "id_parada"}))
public class RutaParada {

    // Llave primaria. IDENTITY = MySQL la genera sola (AUTO_INCREMENT).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ruta_parada")
    private Long id;

    // Posición de la parada dentro de la ruta (1, 2, 3...).
    @Column(name = "orden", nullable = false)
    private Integer orden;

    // RELACIÓN muchos-a-uno: Ruta a la que pertenece la parada.
    // En la tabla se guarda solo el id en la columna "id_ruta" (llave foránea).
    @ManyToOne
    @JoinColumn(name = "id_ruta", nullable = false)
    private Ruta ruta;

    // RELACIÓN muchos-a-uno: Parada que se visita.
    // En la tabla se guarda solo el id en la columna "id_parada" (llave foránea).
    @ManyToOne
    @JoinColumn(name = "id_parada", nullable = false)
    private Parada parada;

    // Constructor vacío: JPA lo necesita para crear objetos al leer de la base de datos.
    public RutaParada() {
    }

    // ---------- Getters y setters ----------
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
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
