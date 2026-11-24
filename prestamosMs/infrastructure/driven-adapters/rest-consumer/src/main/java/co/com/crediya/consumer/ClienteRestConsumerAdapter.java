package co.com.crediya.consumer;

import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.solicitud.ClienteInfo;
import co.com.crediya.model.solicitud.gateways.ClienteGateway;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Adaptador REST que implementa ClienteGateway comunicándose de forma síncrona (imperativa)
 * con autenticacionMs, propagando el token JWT de la petición entrante.
 */
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
        return obtenerInfoClientePorDocumento(documentoIdentidad).isPresent();
    }

    @Override
    public Optional<ClienteInfo> obtenerInfoClientePorDocumento(String documentoIdentidad) {
        log.info("Consultando información de cliente en autenticacionMs para documento: {}", documentoIdentidad);
        try {
            String authHeader = obtenerHeaderAutorizacionActual();

            UsuarioAuthDto usuarioDto = restClient.get()
                    .uri("/api/v1/usuarios/documento/{documento}", documentoIdentidad)
                    .headers(headers -> {
                        if (authHeader != null && !authHeader.isBlank()) {
                            headers.set(HttpHeaders.AUTHORIZATION, authHeader);
                        }
                    })
                    .retrieve()
                    .body(UsuarioAuthDto.class);

            if (usuarioDto == null) {
                return Optional.empty();
            }

            String nombreCompleto = ((usuarioDto.getNombres() != null ? usuarioDto.getNombres() : "") + " "
                    + (usuarioDto.getApellidos() != null ? usuarioDto.getApellidos() : "")).trim();

            ClienteInfo info = ClienteInfo.builder()
                    .nombreCompleto(nombreCompleto)
                    .salarioBase(usuarioDto.getSalarioBase() != null ? usuarioDto.getSalarioBase() : BigDecimal.ZERO)
                    .build();

            log.info("Información obtenida exitosamente para documento {}: nombre={}, salarioBase={}",
                    documentoIdentidad, info.getNombreCompleto(), info.getSalarioBase());
            return Optional.of(info);

        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Cliente no encontrado en autenticacionMs para documento: {}", documentoIdentidad);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error de comunicación al consultar cliente en autenticacionMs: {}", e.getMessage(), e);
            throw new BusinessException("No fue posible consultar la información del cliente en el servicio de autenticación.");
        }
    }

    private String obtenerHeaderAutorizacionActual() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs) {
            HttpServletRequest request = attrs.getRequest();
            return request.getHeader(HttpHeaders.AUTHORIZATION);
        }
        return null;
    }

    /**
     * DTO interno privado para deserializar la respuesta JSON de autenticacionMs.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class UsuarioAuthDto {
        private String nombres;
        private String apellidos;

        @JsonProperty("salario_base")
        private BigDecimal salarioBase;
    }
}