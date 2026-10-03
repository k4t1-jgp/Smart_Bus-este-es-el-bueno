package com.smartbus.smartbusapi.service;

import com.smartbus.smartbusapi.dto.LoginDto;
import com.smartbus.smartbusapi.dto.UsuarioDto;
import com.smartbus.smartbusapi.dto.UsuarioRespuestaDto;
import com.smartbus.smartbusapi.exception.NoAutorizadoException;
import com.smartbus.smartbusapi.exception.RecursoNoEncontradoException;
import com.smartbus.smartbusapi.exception.ReglaNegocioException;
import com.smartbus.smartbusapi.model.Rol;
import com.smartbus.smartbusapi.model.Usuario;
import com.smartbus.smartbusapi.repository.RolRepository;
import com.smartbus.smartbusapi.repository.UsuarioRepository;
import com.smartbus.smartbusapi.util.Validaciones;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de Usuario: gestión de usuarios e inicio de sesión.
 *
 * Reglas que aplica:
 *  - El nombre de usuario no se puede repetir.
 *  - La contraseña se guarda ENCRIPTADA con BCrypt.
 *  - Solo se puede asignar un rol que esté activo.
 *  - Las respuestas nunca incluyen la contraseña.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          RolRepository rolRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // LISTAR TODOS
    public List<UsuarioRespuestaDto> listarTodos() {
        return usuarioRepository.findAll().stream().map(this::aRespuesta).toList();
    }

    // BUSCAR POR ID
    public UsuarioRespuestaDto buscarPorId(Long id) {
        return aRespuesta(obtener(id));
    }

    // CREAR
    public UsuarioRespuestaDto crear(UsuarioDto dto) {
        Validaciones.textoObligatorio(dto.getNombreUsuario(), "nombreUsuario");
        Validaciones.textoObligatorio(dto.getContrasena(), "contrasena");
        if (usuarioRepository.existsByNombreUsuario(dto.getNombreUsuario())) {
            throw new ReglaNegocioException("Ya existe un usuario con el nombre: " + dto.getNombreUsuario());
        }
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(dto.getNombreUsuario());
        // encode(...) convierte la contraseña en el hash BCrypt que se guarda en la base de datos.
        usuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        usuario.setRol(obtenerRolActivo(dto.getRolId()));
        if (dto.getEstado() != null) {
            usuario.setEstado(dto.getEstado());
        }
        return aRespuesta(usuarioRepository.save(usuario));
    }

    // ACTUALIZAR. Si no se envía contraseña, se conserva la anterior.
    public UsuarioRespuestaDto actualizar(Long id, UsuarioDto dto) {
        Usuario usuario = obtener(id);
        Validaciones.textoObligatorio(dto.getNombreUsuario(), "nombreUsuario");
        boolean cambioNombre = !usuario.getNombreUsuario().equals(dto.getNombreUsuario());
        if (cambioNombre && usuarioRepository.existsByNombreUsuario(dto.getNombreUsuario())) {
            throw new ReglaNegocioException("Ya existe un usuario con el nombre: " + dto.getNombreUsuario());
        }
        usuario.setNombreUsuario(dto.getNombreUsuario());
        if (dto.getContrasena() != null && !dto.getContrasena().isBlank()) {
            usuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        }
        usuario.setRol(obtenerRolActivo(dto.getRolId()));
        if (dto.getEstado() != null) {
            usuario.setEstado(dto.getEstado());
        }
        return aRespuesta(usuarioRepository.save(usuario));
    }

    // DESACTIVAR (eliminación lógica)
    public void desactivar(Long id) {
        Usuario usuario = obtener(id);
        usuario.setEstado(false);
        usuarioRepository.save(usuario);
    }

    // INICIAR SESIÓN: verifica usuario, contraseña y que usuario y rol estén activos.
    public UsuarioRespuestaDto iniciarSesion(LoginDto login) {
        Validaciones.textoObligatorio(login.getNombreUsuario(), "nombreUsuario");
        Validaciones.textoObligatorio(login.getContrasena(), "contrasena");
        Usuario usuario = usuarioRepository.findByNombreUsuario(login.getNombreUsuario())
                .orElseThrow(() -> new NoAutorizadoException("Usuario o contraseña incorrectos"));
        // matches(...) compara la contraseña escrita contra el hash guardado.
        if (!passwordEncoder.matches(login.getContrasena(), usuario.getContrasena())) {
            throw new NoAutorizadoException("Usuario o contraseña incorrectos");
        }
        if (!Boolean.TRUE.equals(usuario.getEstado()) || !Boolean.TRUE.equals(usuario.getRol().getEstado())) {
            throw new NoAutorizadoException("El usuario o su rol están inactivos");
        }
        return aRespuesta(usuario);
    }

    // ---------- Métodos auxiliares privados ----------

    private Usuario obtener(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id: " + id));
    }

    private Rol obtenerRolActivo(Long rolId) {
        Rol rol = rolRepository.findById(Validaciones.idObligatorio(rolId, "rolId"))
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol no encontrado con id: " + rolId));
        if (!Boolean.TRUE.equals(rol.getEstado())) {
            throw new ReglaNegocioException("El rol '" + rol.getNombre() + "' está inactivo");
        }
        return rol;
    }

    // Convierte la entidad en el DTO de respuesta (sin contraseña).
    private UsuarioRespuestaDto aRespuesta(Usuario u) {
        return new UsuarioRespuestaDto(u.getId(), u.getNombreUsuario(), u.getEstado(),
                u.getRol().getId(), u.getRol().getNombre());
    }
}
