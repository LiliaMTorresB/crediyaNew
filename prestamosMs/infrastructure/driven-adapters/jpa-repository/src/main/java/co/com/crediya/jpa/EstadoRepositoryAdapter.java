package co.com.crediya.jpa;

import co.com.crediya.model.estado.Estado;
import co.com.crediya.model.estado.gateways.EstadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EstadoRepositoryAdapter implements EstadoRepository {

    private final EstadoJPARepository repository;

    @Override
    public Optional<Estado> findByNombre(String nombre) {
        return repository.findByNombreIgnoreCase(nombre)
                .map(entity -> Estado.builder()
                        .id(entity.getId())
                        .nombre(entity.getNombre())
                        .descripcion(entity.getDescripcion())
                        .build());
    }
}