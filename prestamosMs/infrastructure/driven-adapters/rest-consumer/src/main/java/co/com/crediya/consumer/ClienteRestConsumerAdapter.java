package co.com.crediya.consumer;

import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.solicitud.gateways.ClienteGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class ClienteRestConsumerAdapter implements ClienteGateway {

    private final RestClient restClient;

    public ClienteRestConsumerAdapter(@Value("${adapter.restconsumer.url}") String authServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(authServiceUrl)
                .build();
    }

    @Override
    public boolean existeClientePorDocumento(String documentoIdentidad) {
        log.info("Consultando existencia de cliente en autenticacionMs para documento: {}", documentoIdentidad);
        try {
            restClient.get()
                    .uri("/api/v1/usuarios/documento/{documento}", documentoIdentidad)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Cliente con documento {} validado correctamente.", documentoIdentidad);
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Cliente no encontrado en autenticacionMs para documento: {}", documentoIdentidad);
            return false;
        } catch (Exception e) {
            log.error("Error de comunicación al validar cliente en autenticacionMs: {}", e.getMessage(), e);
            throw new BusinessException("No fue posible validar la información del cliente en este momento.");
        }
    }
}