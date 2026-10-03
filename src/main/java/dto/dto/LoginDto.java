package dto.dto;

/**
 * Datos que se envían para iniciar sesión: POST /api/auth/login
 */
public class LoginDto {

    private String nombreUsuario;
    private String contrasena;

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
