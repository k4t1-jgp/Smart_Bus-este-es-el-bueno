package dto;

/**
 * DTO ("Data Transfer Object") de Rol.
 *
 * Es la "forma" del JSON que llega en un POST o PUT. No es la entidad: aquí las relaciones se
 * reciben solo como el ID del objeto relacionado (por ejemplo "rolId": 1), y el servicio se
 * encarga de buscar el objeto completo en la base de datos.
 */
public class RolDto {

    private String nombre;

    private String descripcion;

    private Boolean estado;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}
