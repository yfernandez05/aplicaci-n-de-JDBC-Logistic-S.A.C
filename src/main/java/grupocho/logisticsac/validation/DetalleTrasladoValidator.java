
package grupocho.logisticsac.validation;

import grupocho.logisticsac.modelo.DetalleTraslado;

public class DetalleTrasladoValidator {
    public void validar(DetalleTraslado detalle) {
        if (detalle == null) {
            throw new IllegalArgumentException("El detalle del traslado no puede ser nulo.");
        }
        if (detalle.getProducto() == null) {
            throw new IllegalArgumentException("El producto es obligatorio.");
        }
        if (detalle.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
    }
}
