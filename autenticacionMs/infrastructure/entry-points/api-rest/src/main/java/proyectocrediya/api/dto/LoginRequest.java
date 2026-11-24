package proyectocrediya.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginRequest {
    @JsonProperty("correo_electronico")
    @Schema(example = "admin@crediya.com")
    private String correoElectronico;

    @Schema(example = "Admin123*")
    private String password;
}