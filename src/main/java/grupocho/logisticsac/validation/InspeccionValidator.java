
package grupocho.logisticsac.validation;

import grupocho.logisticsac.enums.ResultadoInspeccion;
import grupocho.logisticsac.modelo.Inspeccion;

public class InspeccionValidator {

    public void validar(Inspeccion inspeccion) {
        if (inspeccion == null) {
            throw new IllegalArgumentException("La inspección no puede ser nula.");
        }

        if (inspeccion.getTraslado() == null) {
            throw new IllegalArgumentException("El traslado es obligatorio.");
        }
        if (inspeccion.getVigilante() == null) {
            throw new IllegalArgumentException("El vigilante es obligatorio.");
        }
        if (inspeccion.getResultado() == null) {
            throw new IllegalArgumentException("El resultado de la inspección es obligatorio.");
        }

        if (!inspeccion.isCargaConforme() && (inspeccion.getObservacion() == null || inspeccion.getObservacion().isBlank())) {
            throw new IllegalArgumentException(
                    "Debe registrar una observación si la carga no es conforme."
            );
        }

        if (inspeccion.getResultado() == ResultadoInspeccion.CONFORME && (inspeccion.getEvidencias() == null || inspeccion.getEvidencias().isEmpty())) {
            throw new IllegalArgumentException(
                    "La inspección conforme debe tener al menos una evidencia."
            );
        }
    }
}
