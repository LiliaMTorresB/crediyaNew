package co.com.crediya.model.solicitud.gateways;

import co.com.crediya.model.solicitud.PaginaSolicitudes;
import co.com.crediya.model.solicitud.Solicitud;

import java.util.List;

/**
 * Puerto de salida (Gateway) en el dominio para persistencia y consulta de solicitudes.
 * La capa de infraestructura (jpa-repository) implementa este contrato.
 */
public interface SolicitudRepository {

    Solicitud save(Solicitud solicitud);

    /**
     * Consulta de forma paginada las solicitudes cuyos estados estén dentro
     * de la lista proporcionada (ej: Pendiente de revisión, Rechazadas, Revision manual).
     */
    PaginaSolicitudes<Solicitud> findByEstadosPaginado(List<String> nombresEstados, int pagina, int tamanio);

    /**
     * Obtiene todas las solicitudes de un cliente (por documento) que se encuentren
     * en un estado específico (ej: "Aprobada") para calcular su deuda mensual actual.
     */
    List<Solicitud> findByDocumentoYEstado(String documentoIdentidad, String nombreEstado);
}