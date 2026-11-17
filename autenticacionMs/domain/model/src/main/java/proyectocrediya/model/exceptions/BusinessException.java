package proyectocrediya.model.exceptions;

/**
 * Excepción personalizada para representar incumplimientos en las reglas de negocio.
 * Permite capturar errores de validación y devolver mensajes amigables al cliente.
 */
public class BusinessException extends RuntimeException {
    
    public BusinessException(String message) {
        super(message);
    }
}

