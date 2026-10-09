
package grupocho.logisticsac.validation;

import grupocho.logisticsac.enums.EstadoTraslado;
import grupocho.logisticsac.modelo.Recepcion;

public class RecepcionValidator {
    public void validar(Recepcion recepcion) {
        if (recepcion == null) {
            throw new IllegalArgumentException("La recepción no puede ser nula.");
        }
        if (recepcion.getTraslado() == null) {
            throw new IllegalArgumentException("El traslado es obligatorio para registrar la recepción.");
        }
        if (recepcion.getDespachador() == null) {
            throw new IllegalArgumentException("El despachador es obligatorio.");
        }
        if (recepcion.tieneObservaciones() && (recepcion.getObservacion() == null || recepcion.getObservacion().isBlank())) {
            throw new IllegalArgumentException(
                    "Debe registrar una observación cuando el precinto o la carga no son conformes."
            );
        }
    }

    public void validarEstadoTraslado(Recepcion recepcion) {
        if (recepcion.getTraslado().getEstado() != EstadoTraslado.EN_TRANSITO) {
            throw new IllegalStateException(
                    "Solo se puede registrar la recepción de un traslado EN_TRANSITO."
            );
        }
    }
}
