package co.com.crediya.jpa;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "estados")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado")
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    private String descripcion;
}