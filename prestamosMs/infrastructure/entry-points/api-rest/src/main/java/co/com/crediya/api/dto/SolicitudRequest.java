package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudRequest {

    @Schema(description = "Documento de identidad del cliente solicitante", example = "1017123456")
    @JsonProperty("documento_identidad")
    private String documentoIdentidad;

    @Schema(description = "Correo electrónico de contacto del cliente", example = "lilia.torres@crediya.com")
    private String email;

    @Schema(description = "Monto solicitado del préstamo", example = "5000000")
    private BigDecimal monto;

    @Schema(description = "Plazo deseado en meses", example = "24")
    private Integer plazo;

    @Schema(description = "ID del tipo de préstamo seleccionado", example = "1")
    @JsonProperty("id_tipo_prestamo")
    private Long idTipoPrestamo;
}