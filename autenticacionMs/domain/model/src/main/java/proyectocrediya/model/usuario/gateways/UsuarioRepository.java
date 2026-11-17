package proyectocrediya.model.usuario.gateways;

import proyectocrediya.model.usuario.Usuario;
import java.util.Optional;

/**
 * Puerto de salida (Gateway) del dominio.
 * Define las operaciones de persistencia requeridas por los casos de uso.
 */
public interface UsuarioRepository {
    
    // Guarda un nuevo usuario o actualiza uno existente
    Usuario guardar(Usuario usuario);
    
    // Verifica si un correo electrónico ya está registrado en el sistema
    boolean existePorCorreo(String correoElectronico);
    
    // Busca un usuario por su correo electrónico (útil para login en HU03)
    Optional<Usuario> buscarPorCorreo(String correoElectronico);
    
    // Busca un usuario por su documento de identidad (útil para solicitudes en HU02)
    Optional<Usuario> buscarPorDocumento(String documentoIdentidad);

    Optional<Usuario> buscarPorId(Long id);
}