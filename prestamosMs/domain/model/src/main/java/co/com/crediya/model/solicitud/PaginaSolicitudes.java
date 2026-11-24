package co.com.crediya.model.solicitud;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Objeto de dominio puro para transportar resultados paginados
 * sin acoplar la capa de dominio a clases de Spring Data (como Page).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginaSolicitudes<T> {
    private List<T> contenido;
    private int paginaActual;
    private int tamanioPagina;
    private long totalElementos;
    private int totalPaginas;
}