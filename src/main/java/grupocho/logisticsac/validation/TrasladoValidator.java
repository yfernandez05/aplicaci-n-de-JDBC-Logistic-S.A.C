package grupocho.logisticsac.validation;

import grupocho.logisticsac.enums.EstadoTraslado;
import grupocho.logisticsac.modelo.DetalleTraslado;
import grupocho.logisticsac.modelo.Documento;
import grupocho.logisticsac.modelo.FiltroTraslado;
import grupocho.logisticsac.modelo.Inspeccion;
import grupocho.logisticsac.modelo.Traslado;
import grupocho.logisticsac.modelo.Usuario;

import java.util.List;

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

    // documentos: lista de verificacion de garita, todos deben estar vigentes
    public void validarDocumentosVigentes(List<Documento> documentos) {
        if (documentos == null) {
            throw new IllegalArgumentException("La verificación de documentos es obligatoria para autorizar la salida.");
        }
        for (Documento documento : documentos) {
            if (!documento.estaVigente()) {
                throw new IllegalStateException(
                        "No se puede autorizar: existen documentos obligatorios faltantes o vencidos. Solo puede rechazar la salida."
                );
            }
        }
    }

    public void validarRechazo(Traslado traslado, String motivo, Usuario responsable) {
        if (traslado == null) {
            throw new IllegalArgumentException("El traslado no puede ser nulo.");
        }
        if (responsable == null) {
            throw new IllegalArgumentException("El responsable del rechazo es obligatorio.");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("El motivo de rechazo es obligatorio.");
        }
        if (traslado.getEstado() != EstadoTraslado.PROGRAMADO) {
            throw new IllegalStateException("Solo se puede rechazar un traslado PROGRAMADO.");
        }
    }

    public void validarFiltro(FiltroTraslado filtro) {
        if (filtro == null) {
            throw new IllegalArgumentException("El filtro de búsqueda no puede ser nulo.");
        }
        if (filtro.getFechaDesde() != null && filtro.getFechaHasta() != null
                && filtro.getFechaDesde().isAfter(filtro.getFechaHasta())) {
            throw new IllegalArgumentException("La fecha inicial no puede ser mayor que la fecha final.");
        }
    }
}