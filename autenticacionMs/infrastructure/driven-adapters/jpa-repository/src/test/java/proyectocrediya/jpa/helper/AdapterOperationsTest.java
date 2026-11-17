package proyectocrediya.jpa.helper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import proyectocrediya.jpa.JPARepository;
import proyectocrediya.jpa.JPARepositoryAdapter;
import proyectocrediya.jpa.UsuarioEntity;
import proyectocrediya.model.usuario.Usuario;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para verificar el mapeo y persistencia en JPARepositoryAdapter.
 */
class AdapterOperationsTest {

    private JPARepository repository;
    private JPARepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(JPARepository.class);
        adapter = new JPARepositoryAdapter(repository);
    }

    @Test
    void deberiaGuardarYMapearUsuarioCorrectamente() {
        Usuario usuario = Usuario.builder()
                .nombres("Lilia")
                .apellidos("Torres")
                .fechaNacimiento(LocalDate.of(1988, 5, 10))
                .direccion("Calle 10 # 20-30")
                .telefono("3001234567")
                .correoElectronico("lilia@crediya.com")
                .salarioBase(new BigDecimal("4500000"))
                .rol("SOLICITANTE")
                .build();

        UsuarioEntity entidadGuardada = UsuarioEntity.builder()
                .id(1L)
                .nombres("Lilia")
                .apellidos("Torres")
                .fechaNacimiento(LocalDate.of(1988, 5, 10))
                .direccion("Calle 10 # 20-30")
                .telefono("3001234567")
                .correoElectronico("lilia@crediya.com")
                .salarioBase(new BigDecimal("4500000"))
                .rol("SOLICITANTE")
                .build();

        when(repository.save(any(UsuarioEntity.class))).thenReturn(entidadGuardada);

        Usuario resultado = adapter.guardar(usuario);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("lilia@crediya.com", resultado.getCorreoElectronico());
        verify(repository, times(1)).save(any(UsuarioEntity.class));
    }

    @Test
    void deberiaVerificarSiExistePorCorreo() {
        when(repository.existsByCorreoElectronico("lilia@crediya.com")).thenReturn(true);

        boolean existe = adapter.existePorCorreo("lilia@crediya.com");

        assertTrue(existe);
        verify(repository, times(1)).existsByCorreoElectronico("lilia@crediya.com");
    }

    @Test
    void deberiaBuscarUsuarioPorId() {
        UsuarioEntity entidad = UsuarioEntity.builder()
                .id(1L)
                .nombres("Lilia")
                .apellidos("Torres")
                .correoElectronico("lilia@crediya.com")
                .salarioBase(new BigDecimal("4500000"))
                .rol("SOLICITANTE")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(entidad));

        Optional<Usuario> resultado = adapter.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Lilia", resultado.get().getNombres());
        verify(repository, times(1)).findById(1L);
    }
}