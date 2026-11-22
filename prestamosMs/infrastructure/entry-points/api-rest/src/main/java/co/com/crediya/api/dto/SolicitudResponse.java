package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudResponse {
    private Long id;

    @JsonProperty("documento_identidad")
    private String documentoIdentidad;

    private String email;
    private BigDecimal monto;
    private Integer plazo;
    private String estado;

    @JsonProperty("tipo_prestamo")
    private String tipoPrestamo;
}