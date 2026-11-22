package co.com.crediya.usecase.registrarsolicitud;

import co.com.crediya.model.estado.Estado;
import co.com.crediya.model.estado.gateways.EstadoRepository;
import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.usecase.validarcliente.ValidarClienteUseCase;
import co.com.crediya.usecase.validartipoprestamo.ValidarTipoPrestamoUseCase;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;

@RequiredArgsConstructor
public class RegistrarSolicitudUseCase {

    private static final String ESTADO_INICIAL = "Pendiente de revisi\u00f3n";

    private final SolicitudRepository solicitudRepository;
    private final EstadoRepository estadoRepository;
    private final ValidarClienteUseCase validarClienteUseCase;
    private final ValidarTipoPrestamoUseCase validarTipoPrestamoUseCase;

    public Solicitud registrar(Solicitud solicitud) {
        validarDatosBasicos(solicitud);

        // 1. Validar que el cliente exista en el microservicio de autenticación
        validarClienteUseCase.validarExistencia(solicitud.getDocumentoIdentidad());

        // 2. Validar que el tipo de préstamo exista y el monto esté dentro de sus límites
        TipoPrestamo tipoPrestamo = validarTipoPrestamoUseCase.validar(
                solicitud.getIdTipoPrestamo(),
                solicitud.getMonto()
        );

        // 3. Buscar el estado inicial obligatorio: "Pendiente de revisión"
        Estado estadoInicial = estadoRepository.findByNombre(ESTADO_INICIAL)
                .orElseThrow(() -> new BusinessException("No se encontró el estado inicial configurado: " + ESTADO_INICIAL));

        // 4. Armar y guardar la solicitud
        Solicitud nuevaSolicitud = solicitud.toBuilder()
                .documentoIdentidad(solicitud.getDocumentoIdentidad().trim())
                .idEstado(estadoInicial.getId())
                .nombreEstado(estadoInicial.getNombre())
                .idTipoPrestamo(tipoPrestamo.getId())
                .nombreTipoPrestamo(tipoPrestamo.getNombre())
                .build();

        Solicitud guardada = solicitudRepository.save(nuevaSolicitud);
        guardada.setNombreEstado(estadoInicial.getNombre());
        guardada.setNombreTipoPrestamo(tipoPrestamo.getNombre());
        return guardada;
    }

    private void validarDatosBasicos(Solicitud solicitud) {
        if (solicitud == null) {
            throw new BusinessException("La solicitud no puede ser nula.");
        }
        if (solicitud.getMonto() == null || solicitud.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El monto solicitado debe ser mayor a cero.");
        }
        if (solicitud.getPlazo() == null || solicitud.getPlazo() <= 0) {
            throw new BusinessException("El plazo en meses debe ser mayor a cero.");
        }
        if (solicitud.getEmail() == null || solicitud.getEmail().isBlank() || !solicitud.getEmail().contains("@")) {
            throw new BusinessException("Debe proporcionar un correo electrónico válido.");
        }
    }
}