package proyectocrediya.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RegistroUsuarioRequest {

    @Schema(example = "Lilia")
    private String nombres;

    @Schema(example = "Torres")
    private String apellidos;

    @JsonProperty("documento_identidad")
    @Schema(example = "1020304050")
    private String documentoIdentidad;

    @JsonProperty("fecha_nacimiento")
    @Schema(example = "1988-05-15")
    private LocalDate fechaNacimiento;

    @Schema(example = "Calle 10 # 40-20, Medellín")
    private String direccion;

    @Schema(example = "3001234567")
    private String telefono;

    @JsonProperty("correo_electronico")
    @Schema(example = "lilia.torres@ejemplo.com")
    private String correoElectronico;

    @JsonProperty("salario_base")
    @Schema(example = "4500000")
    private BigDecimal salarioBase;

    @Schema(example = "ClaveSegura123*")
    private String password;

    @Schema(example = "SOLICITANTE")
    private String rol;
}

