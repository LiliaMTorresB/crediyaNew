package co.com.crediya.jpa;

import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface TipoPrestamoJPARepository extends CrudRepository<TipoPrestamoEntity, Long> {
    Optional<TipoPrestamoEntity> findByNombreIgnoreCase(String nombre);
}