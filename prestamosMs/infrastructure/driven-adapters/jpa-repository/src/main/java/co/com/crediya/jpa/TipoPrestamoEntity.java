package co.com.crediya.jpa;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tipo_prestamo")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoPrestamoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_prestamo")
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(name = "monto_minimo", nullable = false)
    private BigDecimal montoMinimo;

    @Column(name = "monto_maximo", nullable = false)
    private BigDecimal montoMaximo;

    @Column(name = "tasa_interes", nullable = false)
    private BigDecimal tasaInteres;

    @Column(name = "validacion_automatica")
    private Boolean validacionAutomatica;
}