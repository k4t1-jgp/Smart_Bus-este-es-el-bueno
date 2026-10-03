package exception;

/**
 * Se lanza cuando se busca algo por id y no existe (ejemplo: "Estudiante no encontrado con id: 9").
 * El ManejadorGlobalExcepciones la convierte en una respuesta HTTP 404.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
