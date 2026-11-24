package co.com.crediya.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repositorio Spring Data JPA para la entidad SolicitudEntity.
 * Se extiende JpaRepository para habilitar paginación nativa (Pageable) y consultas JPQL.
 */
public interface JPARepository extends JpaRepository<SolicitudEntity, Long> {

    /**
     * Consulta paginada de solicitudes filtrando por el nombre del estado (insensible a mayúsculas).
     * Se usa un subquery contra EstadoEntity para mantener intacta la estructura de tablas actual.
     */
    @Query("""
            SELECT s FROM SolicitudEntity s
            WHERE s.idEstado IN (
                SELECT e.id FROM EstadoEntity e WHERE LOWER(e.nombre) IN :nombresEstadosLower
            )
            ORDER BY s.id DESC
            """)
    Page<SolicitudEntity> findByNombresEstadosPaginado(
            @Param("nombresEstadosLower") List<String> nombresEstadosLower,
            Pageable pageable
    );

    /**
     * Consulta todas las solicitudes de un cliente (por documento de identidad)
     * cuyo estado coincida con el nombre indicado (ej. "Aprobada").
     */
    @Query("""
            SELECT s FROM SolicitudEntity s
            WHERE s.documentoIdentidad = :documentoIdentidad
              AND s.idEstado IN (
                  SELECT e.id FROM EstadoEntity e WHERE LOWER(e.nombre) = LOWER(:nombreEstado)
              )
            """)
    List<SolicitudEntity> findByDocumentoYNombreEstado(
            @Param("documentoIdentidad") String documentoIdentidad,
            @Param("nombreEstado") String nombreEstado
    );
}