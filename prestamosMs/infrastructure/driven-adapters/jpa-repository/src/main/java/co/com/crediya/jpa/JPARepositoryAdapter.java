package co.com.crediya.jpa;

import co.com.crediya.jpa.helper.AdapterOperations;
import co.com.crediya.model.solicitud.PaginaSolicitudes;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Adaptador de persistencia que implementa el puerto SolicitudRepository del dominio.
 * Traduce las entidades JPA hacia objetos de dominio enriquecidos con el nombre del estado,
 * nombre del tipo de préstamo y tasa de interés.
 */
@Slf4j
@Repository
public class JPARepositoryAdapter extends AdapterOperations<Solicitud, SolicitudEntity, Long, JPARepository>
        implements SolicitudRepository {

    private final EstadoJPARepository estadoJPARepository;
    private final TipoPrestamoJPARepository tipoPrestamoJPARepository;

    public JPARepositoryAdapter(JPARepository repository,
                                ObjectMapper mapper,
                                EstadoJPARepository estadoJPARepository,
                                TipoPrestamoJPARepository tipoPrestamoJPARepository) {
        super(repository, mapper, d -> mapper.map(d, Solicitud.class));
        this.estadoJPARepository = estadoJPARepository;
        this.tipoPrestamoJPARepository = tipoPrestamoJPARepository;
    }

    @Override
    public PaginaSolicitudes<Solicitud> findByEstadosPaginado(List<String> nombresEstados, int pagina, int tamanio) {
        log.debug("Consultando solicitudes paginadas en BD. Estados: {}, página: {}, tamaño: {}",
                nombresEstados, pagina, tamanio);

        List<String> estadosLower = nombresEstados.stream()
                .map(String::toLowerCase)
                .toList();

        Page<SolicitudEntity> pageResult = repository.findByNombresEstadosPaginado(
                estadosLower,
                PageRequest.of(pagina, tamanio)
        );

        // Cargar catálogos en memoria para evitar consultas N+1 por cada fila
        Map<Long, String> mapaEstados = StreamSupport.stream(estadoJPARepository.findAll().spliterator(), false)
                .collect(Collectors.toMap(EstadoEntity::getId, EstadoEntity::getNombre));

        Map<Long, TipoPrestamoEntity> mapaTiposPrestamo = StreamSupport.stream(tipoPrestamoJPARepository.findAll().spliterator(), false)
                .collect(Collectors.toMap(TipoPrestamoEntity::getId, tipo -> tipo));

        List<Solicitud> solicitudesDominio = pageResult.getContent().stream()
                .map(entity -> mapearADominioEnriquecido(entity, mapaEstados, mapaTiposPrestamo))
                .toList();

        return PaginaSolicitudes.<Solicitud>builder()
                .contenido(solicitudesDominio)
                .paginaActual(pageResult.getNumber())
                .tamanioPagina(pageResult.getSize())
                .totalElementos(pageResult.getTotalElements())
                .totalPaginas(pageResult.getTotalPages())
                .build();
    }

    @Override
    public List<Solicitud> findByDocumentoYEstado(String documentoIdentidad, String nombreEstado) {
        log.debug("Consultando solicitudes del documento {} con estado {}", documentoIdentidad, nombreEstado);

        List<SolicitudEntity> entidades = repository.findByDocumentoYNombreEstado(documentoIdentidad, nombreEstado);

        if (entidades.isEmpty()) {
            return List.of();
        }

        Map<Long, String> mapaEstados = StreamSupport.stream(estadoJPARepository.findAll().spliterator(), false)
                .collect(Collectors.toMap(EstadoEntity::getId, EstadoEntity::getNombre));

        Map<Long, TipoPrestamoEntity> mapaTiposPrestamo = StreamSupport.stream(tipoPrestamoJPARepository.findAll().spliterator(), false)
                .collect(Collectors.toMap(TipoPrestamoEntity::getId, tipo -> tipo));

        return entidades.stream()
                .map(entity -> mapearADominioEnriquecido(entity, mapaEstados, mapaTiposPrestamo))
                .toList();
    }

    private Solicitud mapearADominioEnriquecido(SolicitudEntity entity,
                                                Map<Long, String> mapaEstados,
                                                Map<Long, TipoPrestamoEntity> mapaTiposPrestamo) {
        TipoPrestamoEntity tipo = mapaTiposPrestamo.get(entity.getIdTipoPrestamo());

        return Solicitud.builder()
                .id(entity.getId())
                .monto(entity.getMonto())
                .plazo(entity.getPlazo())
                .email(entity.getEmail())
                .documentoIdentidad(entity.getDocumentoIdentidad())
                .idEstado(entity.getIdEstado())
                .nombreEstado(mapaEstados.getOrDefault(entity.getIdEstado(), "Desconocido"))
                .idTipoPrestamo(entity.getIdTipoPrestamo())
                .nombreTipoPrestamo(tipo != null ? tipo.getNombre() : "Desconocido")
                .tasaInteres(tipo != null ? tipo.getTasaInteres() : null)
                .build();
    }
}