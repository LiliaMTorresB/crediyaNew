package co.com.crediya.usecase.listarsolicitudes;

import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.solicitud.ClienteInfo;
import co.com.crediya.model.solicitud.PaginaSolicitudes;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.SolicitudRevision;
import co.com.crediya.model.solicitud.gateways.ClienteGateway;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

/**
 * Caso de Uso de la HU04: Listado de solicitudes para revisión manual.
 * Orquesta la consulta paginada y filtrable de solicitudes pendientes de decisión,
 * enriquece cada solicitud con los datos del cliente (nombre y salario base) y calcula
 * su deuda mensual total de solicitudes previamente aprobadas.
 */
@RequiredArgsConstructor
public class ListarSolicitudesRevisionUseCase {

    // Estados permitidos según la descripción de negocio de la HU04.
    // CORRECCIÓN: Se usa el código Unicode \u00f3 para representar la 'ó' de "revisión".
    // Así evitamos que Windows o la consola corrompan la tilde al compilar el archivo .java.
    public static final List<String> ESTADOS_REVISION_PERMITIDOS = List.of(
            "Pendiente de revisi\u00f3n",
            "Rechazadas",
            "Revision manual"
    );

    // Constante para consultar las solicitudes aprobadas del cliente al calcular su deuda mensual
    private static final String ESTADO_APROBADA = "Aprobada";

    // Puertos (interfaces) del dominio inyectados por constructor gracias a @RequiredArgsConstructor
    private final SolicitudRepository solicitudRepository;
    private final ClienteGateway clienteGateway;

    /**
     * Ejecuta la consulta paginada y filtrable para el Asesor.
     *
     * @param filtroEstado Estado opcional para filtrar (si es nulo o vacío, trae los 3 estados de revisión).
     * @param pagina       Número de página (base 0: la primera página es 0).
     * @param tamanio      Cantidad de registros por página.
     * @return Página con el detalle enriquecido de cada solicitud.
     */
    public PaginaSolicitudes<SolicitudRevision> ejecutar(String filtroEstado, int pagina, int tamanio) {
        // 1. Validar parámetros de paginación para evitar valores negativos o tamaños excesivos
        if (pagina < 0) {
            throw new BusinessException("El número de página no puede ser negativo.");
        }
        if (tamanio <= 0 || tamanio > 100) {
            throw new BusinessException("El tamaño de página debe estar entre 1 y 100 registros.");
        }

        // 2. Determinar la lista de estados a consultar según si el asesor envió filtro o no
        List<String> estadosConsulta = resolverEstadosConsulta(filtroEstado);

        // 3. Consultar las solicitudes paginadas desde el repositorio local (prestamosMs)
        PaginaSolicitudes<Solicitud> paginaBase = solicitudRepository.findByEstadosPaginado(
                estadosConsulta, pagina, tamanio
        );

        // 4. Por cada solicitud encontrada, buscar datos del cliente en autenticacionMs
        //    y calcular su deuda mensual actual por préstamos aprobados
        List<SolicitudRevision> itemsEnriquecidos = paginaBase.getContenido().stream()
                .map(this::construirDetalleRevision)
                .toList();

        // 5. Retornar la página reconstruida manteniendo los metadatos de paginación originales
        return PaginaSolicitudes.<SolicitudRevision>builder()
                .contenido(itemsEnriquecidos)
                .paginaActual(paginaBase.getPaginaActual())
                .tamanioPagina(paginaBase.getTamanioPagina())
                .totalElementos(paginaBase.getTotalElementos())
                .totalPaginas(paginaBase.getTotalPaginas())
                .build();
    }

    /**
     * Si el asesor envía un estado específico, valida que pertenezca a los 3 estados permitidos.
     * Si no envía filtro (null o vacío), devuelve los 3 estados definidos por negocio.
     */
    private List<String> resolverEstadosConsulta(String filtroEstado) {
        if (filtroEstado == null || filtroEstado.isBlank()) {
            return ESTADOS_REVISION_PERMITIDOS;
        }

        String estadoNormalizado = filtroEstado.trim();
        return ESTADOS_REVISION_PERMITIDOS.stream()
                .filter(permitido -> permitido.equalsIgnoreCase(estadoNormalizado))
                .findFirst()
                .map(List::of)
                .orElseThrow(() -> new BusinessException(
                        "El estado de filtro '" + filtroEstado + "' no es válido. Valores permitidos: "
                                + String.join(", ", ESTADOS_REVISION_PERMITIDOS)
                ));
    }

    /**
     * Combina la información de la solicitud, los datos del cliente desde autenticacionMs
     * y la suma de cuotas mensuales de sus préstamos aprobados.
     */
    private SolicitudRevision construirDetalleRevision(Solicitud solicitud) {
        // Consultar nombre completo y salario base en autenticacionMs usando el documento del cliente
        ClienteInfo clienteInfo = clienteGateway.obtenerInfoClientePorDocumento(solicitud.getDocumentoIdentidad())
                .orElse(ClienteInfo.builder()
                        .nombreCompleto("Cliente no encontrado")
                        .salarioBase(BigDecimal.ZERO)
                        .build());

        // Calcular la deuda mensual total de todas las solicitudes en estado "Aprobada" de este cliente
        BigDecimal deudaMensualAprobada = calcularDeudaMensualSolicitudesAprobadas(solicitud.getDocumentoIdentidad());

        // Armar el objeto de dominio con los 9 campos exigidos en la HU04
        return SolicitudRevision.builder()
                .id(solicitud.getId())
                .monto(solicitud.getMonto())
                .plazo(solicitud.getPlazo())
                .email(solicitud.getEmail())
                .nombre(clienteInfo.getNombreCompleto())
                .tipoPrestamo(solicitud.getNombreTipoPrestamo())
                .tasaInteres(solicitud.getTasaInteres())
                .estadoSolicitud(solicitud.getNombreEstado())
                .salarioBase(clienteInfo.getSalarioBase())
                .deudaTotalMensualSolicitudesAprobadas(deudaMensualAprobada)
                .build();
    }

    /**
     * Busca todas las solicitudes en estado "Aprobada" del cliente y suma sus cuotas mensuales.
     */
    private BigDecimal calcularDeudaMensualSolicitudesAprobadas(String documentoIdentidad) {
        List<Solicitud> aprobadas = solicitudRepository.findByDocumentoYEstado(documentoIdentidad, ESTADO_APROBADA);

        return aprobadas.stream()
                .map(this::calcularCuotaMensual)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula la cuota mensual de un préstamo usando la fórmula financiera estándar (amortización francesa):
     * Cuota = (Monto * tasaMensual) / (1 - (1 + tasaMensual)^(-plazo))
     * Se asume que tasaInteres viene expresada en porcentaje (ej. 1.5 -> 1.5% = 0.015).
     */
    private BigDecimal calcularCuotaMensual(Solicitud solicitud) {
        BigDecimal monto = solicitud.getMonto();
        Integer plazo = solicitud.getPlazo();
        BigDecimal tasaPorcentaje = solicitud.getTasaInteres();

        if (monto == null || plazo == null || plazo <= 0) {
            return BigDecimal.ZERO;
        }

        // Si la tasa es nula o 0%, la cuota mensual es simplemente monto / plazo
        if (tasaPorcentaje == null || tasaPorcentaje.compareTo(BigDecimal.ZERO) <= 0) {
            return monto.divide(BigDecimal.valueOf(plazo), 2, RoundingMode.HALF_UP);
        }

        // Convertir porcentaje a decimal (ej. 1.5 -> 0.015)
        BigDecimal tasaDecimal = tasaPorcentaje.divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP);

        // Factor (1 + i)^n
        BigDecimal unoMasTasa = BigDecimal.ONE.add(tasaDecimal);
        BigDecimal potencia = unoMasTasa.pow(plazo, MathContext.DECIMAL128);

        // Denominador: 1 - (1 + i)^(-n) = 1 - (1 / potencia)
        BigDecimal inversoPotencia = BigDecimal.ONE.divide(potencia, 12, RoundingMode.HALF_UP);
        BigDecimal denominador = BigDecimal.ONE.subtract(inversoPotencia);

        if (denominador.compareTo(BigDecimal.ZERO) <= 0) {
            return monto.divide(BigDecimal.valueOf(plazo), 2, RoundingMode.HALF_UP);
        }

        // Numerador: Monto * i
        BigDecimal numerador = monto.multiply(tasaDecimal);
        return numerador.divide(denominador, 2, RoundingMode.HALF_UP);
    }
}