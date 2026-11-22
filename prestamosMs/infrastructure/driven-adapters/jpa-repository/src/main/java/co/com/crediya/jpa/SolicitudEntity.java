package co.com.crediya.jpa;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "solicitud")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Long id;

    @Column(nullable = false)
    private BigDecimal monto;

    @Column(nullable = false)
    private Integer plazo;

    @Column(nullable = false)
    private String email;

    @Column(name = "documento_identidad", nullable = false)
    private String documentoIdentidad;

    @Column(name = "id_estado", nullable = false)
    private Long idEstado;

    @Column(name = "id_tipo_prestamo", nullable = false)
    private Long idTipoPrestamo;
}