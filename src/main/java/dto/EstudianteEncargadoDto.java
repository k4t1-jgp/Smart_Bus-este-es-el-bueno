package dto;

/**
 * DTO ("Data Transfer Object") de EstudianteEncargado.
 *
 * Es la "forma" del JSON que llega en un POST o PUT. No es la entidad: aquí las relaciones se
 * reciben solo como el ID del objeto relacionado (por ejemplo "rolId": 1), y el servicio se
 * encarga de buscar el objeto completo en la base de datos.
 */
public class EstudianteEncargadoDto {

    private String tipoEncargado;

    private String parentesco;

    private String observaciones;

    private Long estudianteId;

    private Long encargadoId;

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

    public Long getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(Long estudianteId) {
        this.estudianteId = estudianteId;
    }

    public Long getEncargadoId() {
        return encargadoId;
    }

    public void setEncargadoId(Long encargadoId) {
        this.encargadoId = encargadoId;
    }
}
