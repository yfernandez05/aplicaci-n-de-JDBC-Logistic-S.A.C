package grupocho.logisticsac.validation;

import grupocho.logisticsac.modelo.Producto;

public class ProductoValidator {
    public void validar(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }
        if (producto.getCodigo() == null || producto.getCodigo().isBlank()) {
            throw new IllegalArgumentException("El código del producto es obligatorio.");
        }
        if (producto.getDescripcion() == null || producto.getDescripcion().isBlank()) {
            throw new IllegalArgumentException("La descripción del producto es obligatoria.");
        }
        if (producto.getUnidadMedida() == null || producto.getUnidadMedida().isBlank()) {
            throw new IllegalArgumentException("La unidad de medida es obligatoria.");
        }
    }
}