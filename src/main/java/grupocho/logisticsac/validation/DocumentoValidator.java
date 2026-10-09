
package grupocho.logisticsac.validation;

import grupocho.logisticsac.enums.AmbitoDocumento;
import grupocho.logisticsac.modelo.Documento;

import java.time.LocalDate;

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

        if (documento.getFechaEmision() == null || documento.getFechaVencimiento() == null) {
            throw new IllegalArgumentException("Las fechas de emisión y vencimiento son obligatorias.");
        }

        if (LocalDate.parse(documento.getFechaVencimiento()).isBefore(LocalDate.parse(documento.getFechaEmision()))) {
            throw new IllegalArgumentException("La fecha de vencimiento no puede ser anterior a la de emisión.");
        }
    }

    public void validarAmbito(Documento documento, AmbitoDocumento ambito) {
        if (documento.getTipoDocumento().getAmbito() != ambito) {
            throw new IllegalArgumentException("El tipo de documento no corresponde a " + ambito + ".");
        }
    }

    public void validarNumero(String numero) {
        if (numero == null || numero.isBlank()) {
            throw new IllegalArgumentException("El número del documento es obligatorio.");
        }
    }
}
