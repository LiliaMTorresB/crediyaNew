package proyectocrediya.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class UsuarioResponse {
    private Long id;
    private String nombres;
    private String apellidos;

    @JsonProperty("documento_identidad")
    private String documentoIdentidad;

    @JsonProperty("fecha_nacimiento")
    private LocalDate fechaNacimiento;

    private String direccion;
    private String telefono;

    @JsonProperty("correo_electronico")
    private String correoElectronico;

    @JsonProperty("salario_base")
    private BigDecimal salarioBase;

    private String rol;
}

