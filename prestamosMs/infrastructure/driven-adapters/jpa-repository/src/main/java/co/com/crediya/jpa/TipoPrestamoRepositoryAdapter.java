package co.com.crediya.jpa;

import co.com.crediya.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.model.tipoprestamo.gateways.TipoPrestamoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TipoPrestamoRepositoryAdapter implements TipoPrestamoRepository {

    private final TipoPrestamoJPARepository repository;

    @Override
    public Optional<TipoPrestamo> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<TipoPrestamo> findByNombre(String nombre) {
        return repository.findByNombreIgnoreCase(nombre).map(this::toDomain);
    }

    private TipoPrestamo toDomain(TipoPrestamoEntity entity) {
        return TipoPrestamo.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .montoMinimo(entity.getMontoMinimo())
                .montoMaximo(entity.getMontoMaximo())
                .tasaInteres(entity.getTasaInteres())
                .validacionAutomatica(entity.getValidacionAutomatica())
                .build();
    }
}