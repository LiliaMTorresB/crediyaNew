package proyectocrediya.jpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import proyectocrediya.model.usuario.Usuario;
import proyectocrediya.model.usuario.gateways.UsuarioRepository;

import java.util.Optional;

/**
 * Adaptador de infraestructura que implementa el puerto UsuarioRepository
 * utilizando Spring Data JPA y PostgreSQL.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class JPARepositoryAdapter implements UsuarioRepository {

    private final JPARepository repository;

    @Override
    @Transactional // Garantiza la atomicidad en la base de datos según criterio técnico HU01
    public Usuario guardar(Usuario usuario) {
        log.info("Persistiendo nuevo usuario en BD con correo: {}", usuario.getCorreoElectronico());
        UsuarioEntity entity = toEntity(usuario);
        UsuarioEntity guardado = repository.save(entity);
        return toDomain(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorCorreo(String correoElectronico) {
        return repository.existsByCorreoElectronico(correoElectronico);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorCorreo(String correoElectronico) {
        return repository.findByCorreoElectronico(correoElectronico).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorDocumento(String documentoIdentidad) {
        return repository.findByDocumentoIdentidad(documentoIdentidad).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    // Métodos de mapeo (Mapper) entre Entidad y Dominio
    private UsuarioEntity toEntity(Usuario domain) {
        return UsuarioEntity.builder()
                .id(domain.getId())
                .nombres(domain.getNombres())
                .apellidos(domain.getApellidos())
                .documentoIdentidad(domain.getDocumentoIdentidad())
                .fechaNacimiento(domain.getFechaNacimiento())
                .direccion(domain.getDireccion())
                .telefono(domain.getTelefono())
                .correoElectronico(domain.getCorreoElectronico())
                .salarioBase(domain.getSalarioBase())
                .password(domain.getPassword())
                .rol(domain.getRol())
                .build();
    }

    private Usuario toDomain(UsuarioEntity entity) {
        return Usuario.builder()
                .id(entity.getId())
                .nombres(entity.getNombres())
                .apellidos(entity.getApellidos())
                .documentoIdentidad(entity.getDocumentoIdentidad())
                .fechaNacimiento(entity.getFechaNacimiento())
                .direccion(entity.getDireccion())
                .telefono(entity.getTelefono())
                .correoElectronico(entity.getCorreoElectronico())
                .salarioBase(entity.getSalarioBase())
                .password(entity.getPassword())
                .rol(entity.getRol())
                .build();
    }
}