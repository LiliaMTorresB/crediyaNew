package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de salida para el endpoint GET /api/v1/solicitud (HU04).
 * Mapea los nombres de propiedades exactos requeridos en los criterios de aceptación.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudRevisionResponse {

    private Long id;
    private BigDecimal monto;
    private Integer plazo;
    private String email;
    private String nombre;

    @JsonProperty("tipo_prestamo")
    private String tipoPrestamo;

    @JsonProperty("tasa_interes")
    private BigDecimal tasaInteres;

    @JsonProperty("estado_solicitud")
    private String estadoSolicitud;

    @JsonProperty("salario_base")
    private BigDecimal salarioBase;

    @JsonProperty("deuda_total_mensual_solicitudes_aprobadas")
    private BigDecimal deudaTotalMensualSolicitudesAprobadas;
}
