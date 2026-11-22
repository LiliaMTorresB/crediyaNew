package co.com.crediya.jpa;

import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface EstadoJPARepository extends CrudRepository<EstadoEntity, Long> {
    Optional<EstadoEntity> findByNombreIgnoreCase(String nombre);
}