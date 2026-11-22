package proyectocrediya.usecase.buscarusuario;

import lombok.RequiredArgsConstructor;
import proyectocrediya.model.usuario.Usuario;
import proyectocrediya.model.usuario.gateways.UsuarioRepository;

import java.util.Optional;

@RequiredArgsConstructor
public class BuscarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;

    public Optional<Usuario> buscarPorDocumento(String documentoIdentidad) {
        if (documentoIdentidad == null || documentoIdentidad.isBlank()) {
            return Optional.empty();
        }
        return usuarioRepository.buscarPorDocumento(documentoIdentidad.trim());
    }
}