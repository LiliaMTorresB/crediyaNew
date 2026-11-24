package proyectocrediya.usecase.registrarusuario;

import lombok.RequiredArgsConstructor;
import proyectocrediya.model.exceptions.BusinessException;
import proyectocrediya.model.usuario.Usuario;
import proyectocrediya.model.usuario.gateways.PasswordEncoderGateway;
import proyectocrediya.model.usuario.gateways.UsuarioRepository;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Caso de uso encargado de orquestar las reglas de negocio
 * para el registro de un nuevo usuario en la plataforma.
 */
@RequiredArgsConstructor
public class RegistrarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoderGateway passwordEncoderGateway;

    // Constantes de negocio para evitar "números mágicos" (buena práctica SonarLint)
    private static final BigDecimal SALARIO_MINIMO_PERMITIDO = BigDecimal.ZERO;
    private static final BigDecimal SALARIO_MAXIMO_PERMITIDO = new BigDecimal("15000000");
    private static final String REGEX_EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String ROL_POR_DEFECTO = "SOLICITANTE";

    public Usuario ejecutar(Usuario usuario) {
        validarCamposObligatorios(usuario);
        validarFormatoCorreo(usuario.getCorreoElectronico());
        validarRangoSalario(usuario.getSalarioBase());

        // Validar que el correo no esté registrado previamente
        if (usuarioRepository.existePorCorreo(usuario.getCorreoElectronico())) {
            throw new BusinessException("El correo electrónico ya se encuentra registrado en el sistema.");
        }

        // Si no viene un rol definido, se asigna SOLICITANTE por defecto
        if (usuario.getRol() == null || usuario.getRol().isBlank()) {
            usuario.setRol(ROL_POR_DEFECTO);
        } else {
            usuario.setRol(usuario.getRol().trim().toUpperCase());
        }

        // Encriptar la contraseña antes de persistir en BD
        usuario.setPassword(passwordEncoderGateway.encode(usuario.getPassword()));

        return usuarioRepository.guardar(usuario);
    }

    private void validarCamposObligatorios(Usuario usuario) {
        if (esNuloOVacio(usuario.getNombres())) {
            throw new BusinessException("El campo 'nombres' es obligatorio.");
        }
        if (esNuloOVacio(usuario.getApellidos())) {
            throw new BusinessException("El campo 'apellidos' es obligatorio.");
        }
        if (esNuloOVacio(usuario.getCorreoElectronico())) {
            throw new BusinessException("El campo 'correo_electronico' es obligatorio.");
        }
        if (esNuloOVacio(usuario.getPassword())) {
            throw new BusinessException("El campo 'password' es obligatorio.");
        }
        if (usuario.getSalarioBase() == null) {
            throw new BusinessException("El campo 'salario_base' es obligatorio.");
        }
    }

    private void validarFormatoCorreo(String correo) {
        if (!Pattern.matches(REGEX_EMAIL, correo)) {
            throw new BusinessException("El formato del correo electrónico no es válido.");
        }
    }

    private void validarRangoSalario(BigDecimal salario) {
        if (salario.compareTo(SALARIO_MINIMO_PERMITIDO) < 0 || salario.compareTo(SALARIO_MAXIMO_PERMITIDO) > 0) {
            throw new BusinessException("El salario base debe estar entre 0 y 15.000.000.");
        }
    }

    private boolean esNuloOVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}