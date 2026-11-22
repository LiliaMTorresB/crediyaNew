package co.com.crediya.usecase.validarcliente;

import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.solicitud.gateways.ClienteGateway;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ValidarClienteUseCase {

    private final ClienteGateway clienteGateway;

    public void validarExistencia(String documentoIdentidad) {
        if (documentoIdentidad == null || documentoIdentidad.isBlank()) {
            throw new BusinessException("El documento de identidad del cliente es obligatorio.");
        }

        boolean existe = clienteGateway.existeClientePorDocumento(documentoIdentidad.trim());
        if (!existe) {
            throw new BusinessException("No existe un cliente registrado con el documento de identidad: " + documentoIdentidad);
        }
    }
}