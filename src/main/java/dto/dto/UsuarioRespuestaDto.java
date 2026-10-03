package dto.dto;

/**
 * Lo que la API devuelve cuando se consulta un usuario.
 *
 * Se usa en lugar de la entidad Usuario para NUNCA enviar la contraseña (ni siquiera encriptada)
 * de vuelta al cliente.
 */
public class UsuarioRespuestaDto {

    private Long id;
    private String nombreUsuario;
    private Boolean estado;
    private Long rolId;
    private String rolNombre;

    public UsuarioRespuestaDto(Long id, String nombreUsuario, Boolean estado, Long rolId, String rolNombre) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.estado = estado;
        this.rolId = rolId;
        this.rolNombre = rolNombre;
    }

    public Long getId() {
        return id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public Boolean getEstado() {
        return estado;
    }

    public Long getRolId() {
        return rolId;
    }

    public String getRolNombre() {
        return rolNombre;
    }
}
