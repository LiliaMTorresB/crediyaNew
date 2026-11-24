package proyectocrediya.usecase.login;

import lombok.RequiredArgsConstructor;
import proyectocrediya.model.exceptions.BusinessException;
import proyectocrediya.model.usuario.Usuario;
import proyectocrediya.model.usuario.gateways.PasswordEncoderGateway;
import proyectocrediya.model.usuario.gateways.TokenGateway;
import proyectocrediya.model.usuario.gateways.UsuarioRepository;

@RequiredArgsConstructor
public class LoginUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoderGateway passwordEncoderGateway;
    private final TokenGateway tokenGateway;

    public String ejecutar(String correoElectronico, String password) {
        if (correoElectronico == null || correoElectronico.isBlank() || password == null || password.isBlank()) {
            throw new BusinessException("Debe ingresar correo electrónico y contraseña.");
        }

        Usuario usuario = usuarioRepository.buscarPorCorreo(correoElectronico.trim())
                .orElseThrow(() -> new BusinessException("Credenciales inválidas: correo o contraseña incorrectos."));

        if (usuario.getPassword() == null || !passwordEncoderGateway.matches(password, usuario.getPassword())) {
            throw new BusinessException("Credenciales inválidas: correo o contraseña incorrectos.");
        }

        return tokenGateway.generarToken(usuario);
    }
}

