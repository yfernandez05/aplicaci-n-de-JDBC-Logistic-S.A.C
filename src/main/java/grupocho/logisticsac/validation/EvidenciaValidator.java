
package grupocho.logisticsac.validation;

import grupocho.logisticsac.modelo.Evidencia;

public class EvidenciaValidator {
    public void validar(Evidencia evidencia) {
        if (evidencia == null) {
            throw new IllegalArgumentException("La evidencia no puede ser nula.");
        }
        if (evidencia.getRutaArchivo() == null
                || evidencia.getRutaArchivo().isBlank()) {
            throw new IllegalArgumentException("La ruta del archivo es obligatoria.");
        }
        if (evidencia.getInspeccion() == null) {
            throw new IllegalArgumentException("La inspección asociada es obligatoria.");
        }
    }
}
