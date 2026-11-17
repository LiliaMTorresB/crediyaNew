package proyectocrediya.jpa;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import java.util.Optional;

public interface JPARepository extends CrudRepository<UsuarioEntity, Long>, QueryByExampleExecutor<UsuarioEntity> {
    boolean existsByCorreoElectronico(String correoElectronico);
    Optional<UsuarioEntity> findByCorreoElectronico(String correoElectronico);
    Optional<UsuarioEntity> findByDocumentoIdentidad(String documentoIdentidad);
}