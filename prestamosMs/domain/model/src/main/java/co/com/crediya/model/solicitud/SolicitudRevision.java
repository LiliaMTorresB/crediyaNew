package co.com.crediya.model.solicitud;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Modelo de dominio que representa cada fila del listado de solicitudes
 * para revisión manual por parte del Asesor (HU04).
 * Contiene todos los campos exigidos por los criterios de aceptación.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudRevision {
    private Long id;
    private BigDecimal monto;
    private Integer plazo;
    private String email;
    private String nombre;
    private String tipoPrestamo;
    private BigDecimal tasaInteres;
    private String estadoSolicitud;
    private BigDecimal salarioBase;
    private BigDecimal deudaTotalMensualSolicitudesAprobadas;
}
