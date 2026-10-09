package grupocho.logisticsac.validation;

import grupocho.logisticsac.modelo.Almacen;

public class AlmacenValidator {
    public void validar(Almacen almacen) {
        if (almacen == null) {
            throw new IllegalArgumentException("El almacén no puede ser nulo.");
        }
        if (almacen.getCodigo() == null || almacen.getCodigo().isBlank()) {
            throw new IllegalArgumentException("El código del almacén es obligatorio.");
        }
        if (almacen.getNombre() == null || almacen.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del almacén es obligatorio.");
        }
        if (almacen.getDireccion() == null || almacen.getDireccion().isBlank()) {
            throw new IllegalArgumentException("La dirección del almacén es obligatoria.");
        }
    }
}