package co.com.crediya.model.solicitud.gateways;

import co.com.crediya.model.solicitud.ClienteInfo;

import java.util.Optional;

/**
 * Puerto de salida (Gateway) en el dominio para consultar datos de clientes
 * en el microservicio externo de autenticación (autenticacionMs).
 */
public interface ClienteGateway {

    boolean existeClientePorDocumento(String documentoIdentidad);

    /**
     * Obtiene el nombre completo y el salario base de un cliente por su documento.
     */
    Optional<ClienteInfo> obtenerInfoClientePorDocumento(String documentoIdentidad);
}