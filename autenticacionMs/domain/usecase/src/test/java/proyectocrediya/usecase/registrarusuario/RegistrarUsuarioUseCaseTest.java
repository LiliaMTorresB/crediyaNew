package proyectocrediya.usecase.registrarusuario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import proyectocrediya.model.exceptions.BusinessException;
import proyectocrediya.model.usuario.Usuario;
import proyectocrediya.model.usuario.gateways.UsuarioRepository;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RegistrarUsuarioUseCaseTest {

    private UsuarioRepository usuarioRepository;
    private RegistrarUsuarioUseCase useCase;

    @BeforeEach
    void setUp() {
        usuarioRepository = Mockito.mock(UsuarioRepository.class);
        useCase = new RegistrarUsuarioUseCase(usuarioRepository);
    }

    @Test
    void deberiaRegistrarUsuarioExitosamente() {
        Usuario usuario = Usuario.builder()
                .nombres("Lilia")
                .apellidos("Torres")
                .correoElectronico("lilia@test.com")
                .salarioBase(new BigDecimal("3000000"))
                .build();

        when(usuarioRepository.existePorCorreo("lilia@test.com")).thenReturn(false);
        when(usuarioRepository.guardar(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        Usuario resultado = useCase.ejecutar(usuario);

        assertNotNull(resultado.getId());
        assertEquals("SOLICITANTE", resultado.getRol());
        verify(usuarioRepository, times(1)).guardar(any(Usuario.class));
    }

    @Test
    void deberiaLanzarExcepcionCuandoSalarioEsMayorAlLimite() {
        Usuario usuario = Usuario.builder()
                .nombres("Lilia")
                .apellidos("Torres")
                .correoElectronico("lilia@test.com")
                .salarioBase(new BigDecimal("20000000")) // Supera los 15 millones
                .build();

        BusinessException exception = assertThrows(BusinessException.class, () -> useCase.ejecutar(usuario));
        assertEquals("El salario base debe estar entre 0 y 15.000.000.", exception.getMessage());
        verify(usuarioRepository, never()).guardar(any());
    }

    @Test
    void deberiaLanzarExcepcionCuandoCorreoYaExiste() {
        Usuario usuario = Usuario.builder()
                .nombres("Lilia")
                .apellidos("Torres")
                .correoElectronico("repetido@test.com")
                .salarioBase(new BigDecimal("2000000"))
                .build();

        when(usuarioRepository.existePorCorreo("repetido@test.com")).thenReturn(true);

        assertThrows(BusinessException.class, () -> useCase.ejecutar(usuario));
        verify(usuarioRepository, never()).guardar(any());
    }
}