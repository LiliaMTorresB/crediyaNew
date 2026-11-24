package co.com.crediya.model.solicitud;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Entidad principal de dominio que representa una solicitud de préstamo.
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Solicitud {
    private Long id;
    private BigDecimal monto;
    private Integer plazo;
    private String email;
    private String documentoIdentidad;
    private Long idEstado;
    private String nombreEstado;
    private Long idTipoPrestamo;
    private String nombreTipoPrestamo;
    private BigDecimal tasaInteres;
}