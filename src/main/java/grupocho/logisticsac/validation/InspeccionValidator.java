
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

        // la inspeccion es conforme solo si el vehiculo y la carga estan conformes
        boolean conforme = inspeccion.getResultado() == ResultadoInspeccion.CONFORME && inspeccion.isCargaConforme();

        if (!conforme && (inspeccion.getObservacion() == null || inspeccion.getObservacion().isBlank())) {
            throw new IllegalArgumentException(
                    "Debe registrar una observación si el vehículo o la carga no son conformes."
            );
        }

        if (conforme && (inspeccion.getEvidencias() == null || inspeccion.getEvidencias().isEmpty())) {
            throw new IllegalArgumentException(
                    "La inspección conforme debe tener al menos una evidencia."
            );
        }
    }
}
