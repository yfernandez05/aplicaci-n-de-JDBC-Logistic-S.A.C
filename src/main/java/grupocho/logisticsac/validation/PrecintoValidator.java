
package grupocho.logisticsac.validation;

import grupocho.logisticsac.modelo.Precinto;

public class PrecintoValidator {
    public void validar(Precinto precinto) {
        if (precinto == null) {
            throw new IllegalArgumentException("El precinto no puede ser nulo.");
        }
        if (precinto.getNumero() == null || precinto.getNumero().isBlank()) {
            throw new IllegalArgumentException("El número del precinto es obligatorio.");
        }
        if (precinto.getFechaColocacion() == null) {
            throw new IllegalArgumentException("La fecha de colocación es obligatoria.");
        }

        if (precinto.getTraslado() == null) {
            throw new IllegalArgumentException("El traslado asociado al precinto es obligatorio.");
        }
    }
}
