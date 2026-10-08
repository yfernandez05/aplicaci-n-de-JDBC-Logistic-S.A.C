
package grupocho.logisticsac.validation;

import grupocho.logisticsac.modelo.TipoDocumento;

public class TipoDocumentoValidator {
    public void validar(TipoDocumento tipoDocumento) {
        if (tipoDocumento == null) {
            throw new IllegalArgumentException("El tipo de documento no puede ser nulo.");
        }
        if (tipoDocumento.getNombre() == null || tipoDocumento.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del tipo de documento es obligatorio.");
        }
        if (tipoDocumento.getAmbito() == null) {
            throw new IllegalArgumentException("El ámbito del documento es obligatorio.");
        }
    }

    public void validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del tipo de documento es obligatorio."
            );
        }
    }
}
