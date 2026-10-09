
package grupocho.logisticsac.validation;

import grupocho.logisticsac.modelo.Conductor;

public class ConductorValidator {

    public void validar(Conductor conductor) {
        if (conductor == null) {
            throw new IllegalArgumentException("El conductor no puede ser nulo.");
        }

        if (conductor.getDni() == null || conductor.getDni().isBlank()) {
            throw new IllegalArgumentException("El DNI del conductor es obligatorio.");
        }

        if (conductor.getNombres() == null || conductor.getNombres().isBlank()) {
            throw new IllegalArgumentException("Los nombres del conductor son obligatorios.");
        }

        if (conductor.getNumeroLicencia() == null || conductor.getNumeroLicencia().isBlank()) {
            throw new IllegalArgumentException("El número de licencia es obligatorio.");
        }
        if (conductor.getCategoriaLicencia() == null || conductor.getCategoriaLicencia().isBlank()) {
            throw new IllegalArgumentException("La categoría de licencia es obligatoria.");
        }
    }

    public void validarDni(String dni) {
        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("El DNI es obligatorio.");
        }
    }
}
