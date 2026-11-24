package co.com.crediya.api;

import co.com.crediya.api.dto.SolicitudRequest;
import co.com.crediya.api.dto.SolicitudResponse;
import co.com.crediya.api.dto.SolicitudRevisionResponse;
import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.solicitud.PaginaSolicitudes;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.SolicitudRevision;
import co.com.crediya.usecase.listarsolicitudes.ListarSolicitudesRevisionUseCase;
import co.com.crediya.usecase.registrarsolicitud.RegistrarSolicitudUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import java.util.List;
import java.util.Map;

/**
 * Controlador REST del microservicio de Préstamos.
 * Expone los endpoints para registrar solicitudes (HU02/HU03) y listarlas para revisión (HU04).
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/solicitud", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
@Tag(name = "Solicitudes", description = "Operaciones para el registro y gestión de solicitudes de préstamo")
public class ApiRest {

    private final RegistrarSolicitudUseCase registrarSolicitudUseCase;
    private final ListarSolicitudesRevisionUseCase listarSolicitudesRevisionUseCase;

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

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Listar solicitudes para revisión manual (HU04)",
            description = "Requiere rol ASESOR. Retorna una lista paginada y filtrable de solicitudes en estado 'Pendiente de revisión', 'Rechazadas' o 'Revision manual'."
    )
    public ResponseEntity<PaginaSolicitudes<SolicitudRevisionResponse>> listarSolicitudesRevision(
            @Parameter(description = "Filtro opcional por estado (Pendiente de revisión, Rechazadas, Revision manual)")
            @RequestParam(name = "estado", required = false) String estado,
            @Parameter(description = "Número de página (inicia en 0)")
            @RequestParam(name = "page", defaultValue = "0") int page,
            @Parameter(description = "Cantidad de registros por página")
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        log.info("Iniciando consulta GET /api/v1/solicitud para revisión manual. Filtro estado: '{}', page: {}, size: {}",
                estado, page, size);

        PaginaSolicitudes<SolicitudRevision> paginaDominio = listarSolicitudesRevisionUseCase.ejecutar(estado, page, size);

        List<SolicitudRevisionResponse> contenidoDto = paginaDominio.getContenido().stream()
                .map(this::toRevisionResponse)
                .toList();

        PaginaSolicitudes<SolicitudRevisionResponse> respuesta = PaginaSolicitudes.<SolicitudRevisionResponse>builder()
                .contenido(contenidoDto)
                .paginaActual(paginaDominio.getPaginaActual())
                .tamanioPagina(paginaDominio.getTamanioPagina())
                .totalElementos(paginaDominio.getTotalElementos())
                .totalPaginas(paginaDominio.getTotalPaginas())
                .build();

        log.info("Consulta de solicitudes completada. Total registros encontrados: {}, página actual: {}/{}",
                respuesta.getTotalElementos(), respuesta.getPaginaActual(), respuesta.getTotalPaginas());

        return ResponseEntity.ok(respuesta);
    }

    private SolicitudRevisionResponse toRevisionResponse(SolicitudRevision item) {
        return SolicitudRevisionResponse.builder()
                .id(item.getId())
                .monto(item.getMonto())
                .plazo(item.getPlazo())
                .email(item.getEmail())
                .nombre(item.getNombre())
                .tipoPrestamo(item.getTipoPrestamo())
                .tasaInteres(item.getTasaInteres())
                .estadoSolicitud(item.getEstadoSolicitud())
                .salarioBase(item.getSalarioBase())
                .deudaTotalMensualSolicitudesAprobadas(item.getDeudaTotalMensualSolicitudesAprobadas())
                .build();
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