
package grupocho.logisticsac.validation;

import grupocho.logisticsac.modelo.Documento;

public class DocumentoValidator {

    public void validar(Documento documento) {
        if (documento == null) {
            throw new IllegalArgumentException("El documento no puede ser nulo.");
        }

        if (documento.getNumero() == null || documento.getNumero().isBlank()) {
            throw new IllegalArgumentException("El número del documento es obligatorio.");
        }

        if (documento.getTipoDocumento() == null) {
            throw new IllegalArgumentException("El tipo de documento es obligatorio.");
        }

        if (documento.getEstado() == null || documento.getEstado().isBlank()) {
            throw new IllegalArgumentException("El estado del documento es obligatorio.");
        }
    }

    public void validarNumero(String numero) {
        if (numero == null || numero.isBlank()) {
            throw new IllegalArgumentException("El número del documento es obligatorio.");
        }
    }
}
