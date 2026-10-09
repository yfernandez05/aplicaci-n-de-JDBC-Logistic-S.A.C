
package grupocho.logisticsac.validation;

import grupocho.logisticsac.modelo.Camion;

public class CamionValidator {

    private final VehiculoValidator vehiculoValidator = new VehiculoValidator();

    public void validar(Camion camion) {
        if (camion == null) {
            throw new IllegalArgumentException("El camión no puede ser nulo.");
        }

        vehiculoValidator.validar(camion);

        if (camion.getNumeroEjes() <= 0) {
            throw new IllegalArgumentException("El número de ejes debe ser mayor a cero." );
        }
    }
}
