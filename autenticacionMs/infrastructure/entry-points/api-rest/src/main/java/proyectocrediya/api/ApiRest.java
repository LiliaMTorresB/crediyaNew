package proyectocrediya.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyectocrediya.api.dto.LoginRequest;
import proyectocrediya.api.dto.LoginResponse;
import proyectocrediya.api.dto.RegistroUsuarioRequest;
import proyectocrediya.api.dto.UsuarioResponse;
import proyectocrediya.model.usuario.Usuario;
import proyectocrediya.usecase.buscarusuario.BuscarUsuarioUseCase;
import proyectocrediya.usecase.login.LoginUseCase;
import proyectocrediya.usecase.registrarusuario.RegistrarUsuarioUseCase;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
@Tag(name = "Autenticación y Usuarios", description = "Login JWT, registro y consulta de usuarios")
public class ApiRest {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;
    private final BuscarUsuarioUseCase buscarUsuarioUseCase;
    private final LoginUseCase loginUseCase;

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Iniciar sesión", description = "Autentica usuario por correo y contraseña y retorna un token JWT.")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        log.info("Intento de inicio de sesión para correo: {}", request.getCorreoElectronico());
        String token = loginUseCase.ejecutar(request.getCorreoElectronico(), request.getPassword());
        log.info("Inicio de sesión exitoso para correo: {}", request.getCorreoElectronico());
        return ResponseEntity.ok(LoginResponse.builder()
                .token(token)
                .tipo("Bearer")
                .build());
    }

    @PostMapping(value = "/usuarios", consumes = MediaType.APPLICATION_JSON_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Registrar un nuevo usuario", description = "Requiere rol ADMIN o ASESOR. Crea un usuario validando correo único y rango salarial.")
    public ResponseEntity<UsuarioResponse> registrarUsuario(@RequestBody RegistroUsuarioRequest request) {
        log.info("Iniciando petición POST /api/v1/usuarios para correo: {}", request.getCorreoElectronico());

        Usuario usuarioDominio = Usuario.builder()
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .documentoIdentidad(request.getDocumentoIdentidad())
                .fechaNacimiento(request.getFechaNacimiento())
                .direccion(request.getDireccion())
                .telefono(request.getTelefono())
                .correoElectronico(request.getCorreoElectronico())
                .salarioBase(request.getSalarioBase())
                .password(request.getPassword())
                .rol(request.getRol())
                .build();

        Usuario registrado = registrarUsuarioUseCase.ejecutar(usuarioDominio);

        log.info("Usuario registrado exitosamente con ID: {}", registrado.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(registrado));
    }

    @GetMapping("/usuarios/documento/{documentoIdentidad}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Consultar usuario por documento de identidad", description = "Requiere estar autenticado. Busca un usuario registrado por su número de documento.")
    public ResponseEntity<UsuarioResponse> buscarPorDocumento(@PathVariable("documentoIdentidad") String documentoIdentidad) {
        log.info("Iniciando petición GET /api/v1/usuarios/documento/{}", documentoIdentidad);

        return buscarUsuarioUseCase.buscarPorDocumento(documentoIdentidad)
                .map(usuario -> ResponseEntity.ok(toResponse(usuario)))
                .orElseGet(() -> {
                    log.warn("No se encontró usuario con documento: {}", documentoIdentidad);
                    return ResponseEntity.notFound().build();
                });
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nombres(usuario.getNombres())
                .apellidos(usuario.getApellidos())
                .documentoIdentidad(usuario.getDocumentoIdentidad())
                .fechaNacimiento(usuario.getFechaNacimiento())
                .direccion(usuario.getDireccion())
                .telefono(usuario.getTelefono())
                .correoElectronico(usuario.getCorreoElectronico())
                .salarioBase(usuario.getSalarioBase())
                .rol(usuario.getRol())
                .build();
    }
}

