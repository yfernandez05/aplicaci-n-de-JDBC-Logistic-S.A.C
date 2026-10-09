package grupocho.logisticsac.validation;

import grupocho.logisticsac.modelo.Vehiculo;

public class VehiculoValidator {
    public void validar(Vehiculo vehiculo) {
        if (vehiculo == null) {
            throw new IllegalArgumentException("El vehículo no puede ser nulo.");
        }
        if (vehiculo.getPlaca() == null || vehiculo.getPlaca().isBlank()) {
            throw new IllegalArgumentException("La placa del vehículo es obligatoria.");
        }
        if (vehiculo.getTipo() == null || vehiculo.getTipo().isBlank()) {
            throw new IllegalArgumentException("El tipo de vehículo es obligatorio.");
        }
        if (vehiculo.getCapacidadCarga() <= 0) {
            throw new IllegalArgumentException("La capacidad de carga debe ser mayor a cero.");
        }
        if (vehiculo.getCondicion() == null || vehiculo.getCondicion().isBlank()) {
            throw new IllegalArgumentException("La condición del vehículo es obligatoria.");
        }
        if (vehiculo.getEstado() == null || vehiculo.getEstado().isBlank()) {
            throw new IllegalArgumentException("El estado del vehículo es obligatorio.");
        }
    }
}