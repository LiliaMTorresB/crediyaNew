package proyectocrediya.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import proyectocrediya.model.usuario.Usuario;
import proyectocrediya.model.usuario.gateways.PasswordEncoderGateway;
import proyectocrediya.model.usuario.gateways.UsuarioRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    @Bean
    public CommandLineRunner initAdminUser(UsuarioRepository usuarioRepository,
                                           PasswordEncoderGateway passwordEncoderGateway) {
        return args -> {
            String adminEmail = "admin@crediya.com";
            if (!usuarioRepository.existePorCorreo(adminEmail)) {
                Usuario admin = Usuario.builder()
                        .nombres("Administrador")
                        .apellidos("CrediYa")
                        .documentoIdentidad("1000000001")
                        .fechaNacimiento(LocalDate.of(1990, 1, 1))
                        .direccion("Sede Principal")
                        .telefono("3000000000")
                        .correoElectronico(adminEmail)
                        .salarioBase(new BigDecimal("8000000"))
                        .password(passwordEncoderGateway.encode("Admin123*"))
                        .rol("ADMIN")
                        .build();
                usuarioRepository.guardar(admin);
                log.info("Usuario ADMIN inicial creado: {} / Admin123*", adminEmail);
            }
        };
    }
}
