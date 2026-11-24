package co.com.crediya.model.solicitud;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Modelo de dominio que encapsula la información básica del solicitante
 * obtenida desde el microservicio de autenticación (autenticacionMs).
 * No tiene dependencias de frameworks externos (Clean Architecture).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteInfo {
    private String nombreCompleto;
    private BigDecimal salarioBase;
}