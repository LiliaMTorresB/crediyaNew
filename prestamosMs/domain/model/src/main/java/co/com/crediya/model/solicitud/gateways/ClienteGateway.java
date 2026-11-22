package co.com.crediya.model.solicitud.gateways;

public interface ClienteGateway {
    boolean existeClientePorDocumento(String documentoIdentidad);
}