package proyectocrediya.model.usuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Modelo de dominio que representa a un usuario en la plataforma CrediYa.
 * Es un objeto Java puro (POJO) sin anotaciones de base de datos ni de web.
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    private Long id;
    private String nombres;
    private String apellidos;
    private String documentoIdentidad; // Servirá para cruzar con solicitudes en HU02
    private LocalDate fechaNacimiento;
    private String direccion;
    private String telefono;
    private String correoElectronico;
    private BigDecimal salarioBase;
    private String password;           // Se usará en HU03 para iniciar sesión
    private String rol;                // Roles: SOLICITANTE, ASESOR, ADMIN
}
