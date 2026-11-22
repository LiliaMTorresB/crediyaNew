package co.com.crediya.usecase.validartipoprestamo;

import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.model.tipoprestamo.gateways.TipoPrestamoRepository;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;

@RequiredArgsConstructor
public class ValidarTipoPrestamoUseCase {

    private final TipoPrestamoRepository tipoPrestamoRepository;

    public TipoPrestamo validar(Long idTipoPrestamo, BigDecimal monto) {
        if (idTipoPrestamo == null) {
            throw new BusinessException("El tipo de préstamo es obligatorio.");
        }

        TipoPrestamo tipo = tipoPrestamoRepository.findById(idTipoPrestamo)
                .orElseThrow(() -> new BusinessException("El tipo de préstamo seleccionado no existe en el sistema."));

        if (tipo.getMontoMinimo() != null && monto.compareTo(tipo.getMontoMinimo()) < 0) {
            throw new BusinessException("El monto solicitado es inferior al mínimo permitido (" + tipo.getMontoMinimo() + ") para el tipo de préstamo " + tipo.getNombre() + ".");
        }

        if (tipo.getMontoMaximo() != null && monto.compareTo(tipo.getMontoMaximo()) > 0) {
            throw new BusinessException("El monto solicitado supera el máximo permitido (" + tipo.getMontoMaximo() + ") para el tipo de préstamo " + tipo.getNombre() + ".");
        }

        return tipo;
    }
}