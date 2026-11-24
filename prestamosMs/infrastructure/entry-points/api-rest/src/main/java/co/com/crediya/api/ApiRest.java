package co.com.crediya.api;

import co.com.crediya.api.dto.SolicitudRequest;
import co.com.crediya.api.dto.SolicitudResponse;
import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.usecase.registrarsolicitud.RegistrarSolicitudUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/solicitud", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
@Tag(name = "Solicitudes", description = "Operaciones para el registro y gestión de solicitudes de préstamo")
public class ApiRest {

    private final RegistrarSolicitudUseCase registrarSolicitudUseCase;

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Registrar una nueva solicitud de préstamo",
            description = "Requiere rol SOLICITANTE/CLIENTE. El usuario autenticado solo puede registrar solicitudes a su propio nombre."
    )
    public ResponseEntity<SolicitudResponse> registrarSolicitud(@RequestBody SolicitudRequest request,
                                                              Authentication authentication) {
        log.info("Iniciando registro de solicitud de préstamo para documento: {}, monto: {}, plazo: {}, tipoPrestamoId: {}",
                request.getDocumentoIdentidad(), request.getMonto(), request.getPlazo(), request.getIdTipoPrestamo());

        validarPropietarioSolicitud(request, authentication);

        Solicitud domainRequest = Solicitud.builder()
                .documentoIdentidad(request.getDocumentoIdentidad())
                .email(request.getEmail())
                .monto(request.getMonto())
                .plazo(request.getPlazo())
                .idTipoPrestamo(request.getIdTipoPrestamo())
                .build();

        Solicitud registrada = registrarSolicitudUseCase.registrar(domainRequest);

        log.info("Solicitud registrada exitosamente con ID: {} y estado: {}", registrada.getId(), registrada.getNombreEstado());

        SolicitudResponse response = SolicitudResponse.builder()
                .id(registrada.getId())
                .documentoIdentidad(registrada.getDocumentoIdentidad())
                .email(registrada.getEmail())
                .monto(registrada.getMonto())
                .plazo(registrada.getPlazo())
                .estado(registrada.getNombreEstado())
                .tipoPrestamo(registrada.getNombreTipoPrestamo())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @SuppressWarnings("unchecked")
    private void validarPropietarioSolicitud(SolicitudRequest request, Authentication authentication) {
        if (authentication == null || !(authentication.getDetails() instanceof Map<?, ?> details)) {
            throw new BusinessException("No se pudo verificar la identidad del cliente autenticado.");
        }

        String correoToken = (String) details.get("correo");
        String documentoToken = (String) details.get("documento");

        if (documentoToken == null || documentoToken.isBlank() || !documentoToken.equals(request.getDocumentoIdentidad())) {
            log.warn("Intento no autorizado: usuario {} (doc {}) intentó crear solicitud para doc {}",
                    correoToken, documentoToken, request.getDocumentoIdentidad());
            throw new BusinessException("Un cliente solo puede crear solicitudes de préstamo para sí mismo (el documento de identidad no coincide con el usuario autenticado).");
        }

        if (correoToken == null || request.getEmail() == null || !correoToken.equalsIgnoreCase(request.getEmail().trim())) {
            log.warn("Intento no autorizado: usuario {} intentó crear solicitud con correo {}",
                    correoToken, request.getEmail());
            throw new BusinessException("Un cliente solo puede crear solicitudes de préstamo con su propio correo electrónico.");
        }
    }
}