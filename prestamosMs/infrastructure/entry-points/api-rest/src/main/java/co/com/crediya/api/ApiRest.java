package co.com.crediya.api;

import co.com.crediya.api.dto.SolicitudRequest;
import co.com.crediya.api.dto.SolicitudResponse;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.usecase.registrarsolicitud.RegistrarSolicitudUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/solicitud", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Solicitudes", description = "Operaciones para el registro y gestión de solicitudes de préstamo")
public class ApiRest {

    private final RegistrarSolicitudUseCase registrarSolicitudUseCase;

    @PostMapping
    @Operation(
            summary = "Registrar una nueva solicitud de préstamo",
            description = "Valida la existencia del cliente y el tipo de préstamo, asignando el estado inicial 'Pendiente de revisión'."
    )
    public ResponseEntity<SolicitudResponse> registrarSolicitud(@RequestBody SolicitudRequest request) {
        log.info("Iniciando registro de solicitud de préstamo para documento: {}, monto: {}, plazo: {}, tipoPrestamoId: {}",
                request.getDocumentoIdentidad(), request.getMonto(), request.getPlazo(), request.getIdTipoPrestamo());

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
}