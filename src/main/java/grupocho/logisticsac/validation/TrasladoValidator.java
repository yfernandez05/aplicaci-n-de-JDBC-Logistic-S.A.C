package grupocho.logisticsac.validation;

import grupocho.logisticsac.enums.EstadoTraslado;
import grupocho.logisticsac.modelo.DetalleTraslado;
import grupocho.logisticsac.modelo.Inspeccion;
import grupocho.logisticsac.modelo.Traslado;
import grupocho.logisticsac.modelo.Usuario;

public class TrasladoValidator {
    private final DetalleTrasladoValidator detalleTrasladoValidator = new DetalleTrasladoValidator();

    public void validar(Traslado traslado) {
        if (traslado == null) {
            throw new IllegalArgumentException("El traslado no puede ser nulo.");
        }

        if (traslado.getCodigo() == null || traslado.getCodigo().isBlank()) {
            throw new IllegalArgumentException("El código del traslado es obligatorio.");
        }

        if (traslado.getFechaProgramada() == null) {
            throw new IllegalArgumentException("La fecha programada es obligatoria.");
        }

        if (traslado.getAlmacenOrigen() == null) {
            throw new IllegalArgumentException("El almacén de origen es obligatorio.");
        }

        if (traslado.getAlmacenDestino() == null) {
            throw new IllegalArgumentException("El almacén de destino es obligatorio.");
        }

        if (traslado.getAlmacenOrigen().getIdAlmacen() == traslado.getAlmacenDestino().getIdAlmacen()) {
            throw new IllegalArgumentException(
                    "El almacén de origen y destino no pueden ser iguales."
            );
        }

        if (traslado.getVehiculo() == null) {
            throw new IllegalArgumentException("El vehículo es obligatorio.");
        }
        if (traslado.getConductor() == null) {
            throw new IllegalArgumentException("El conductor es obligatorio.");
        }

        if (traslado.getDetalles() == null || traslado.getDetalles().isEmpty()) {
            throw new IllegalArgumentException(
                    "El traslado debe tener al menos un detalle."
            );
        }

        for (DetalleTraslado detalle : traslado.getDetalles()) {
            detalleTrasladoValidator.validar(detalle);
        }
    }


    public void validarAutorizacion(Traslado traslado, Usuario responsable, Inspeccion inspeccion) {
        if (traslado == null) {
            throw new IllegalArgumentException("El traslado no puede ser nulo.");
        }
        if (responsable == null) {
            throw new IllegalArgumentException("El responsable de salida es obligatorio.");
        }
        if (inspeccion == null) {
            throw new IllegalArgumentException("La inspección es obligatoria para autorizar la salida.");
        }
        if (traslado.getEstado() != EstadoTraslado.PROGRAMADO) {
            throw new IllegalStateException("Solo se puede autorizar un traslado PROGRAMADO.");
        }
        if (!inspeccion.puedeAutorizar()) {
            throw new IllegalStateException("El traslado no puede ser autorizado porque la inspección no es conforme.");
        }
    }

    public void validarRechazo(Traslado traslado, String motivo) {
        if (traslado == null) {
            throw new IllegalArgumentException("El traslado no puede ser nulo.");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("El motivo de rechazo es obligatorio.");
        }
        if (traslado.getEstado() != EstadoTraslado.PROGRAMADO) {
            throw new IllegalStateException("Solo se puede rechazar un traslado PROGRAMADO.");
        }

        if (traslado.getDetalles() == null || traslado.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("El traslado debe tener al menos un detalle.");
        }

        for (DetalleTraslado detalle : traslado.getDetalles()) {
            detalleTrasladoValidator.validar(detalle);
        }
    }
}