package grupocho.logisticsac.service;

import grupocho.logisticsac.config.ConexionDB;
import grupocho.logisticsac.enums.AmbitoDocumento;
import grupocho.logisticsac.enums.EstadoTraslado;
import grupocho.logisticsac.modelo.DetalleTraslado;
import grupocho.logisticsac.modelo.Documento;
import grupocho.logisticsac.modelo.FiltroTraslado;
import grupocho.logisticsac.modelo.Inspeccion;
import grupocho.logisticsac.modelo.Traslado;
import grupocho.logisticsac.modelo.Usuario;
import grupocho.logisticsac.repository.DocumentoRepository;
import grupocho.logisticsac.repository.TrasladoRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class TrasladoService {

    private final TrasladoRepository trasladoRepository;
    private final DocumentoRepository documentoRepository;

    public TrasladoService(TrasladoRepository trasladoRepository, DocumentoRepository documentoRepository) {
        this.trasladoRepository = trasladoRepository;
        this.documentoRepository = documentoRepository;
    }

    // Registra el traslado con su detalle de productos y sus documentos en una sola transaccion.
    public void registrar(Traslado traslado) throws SQLException {

        if (traslado == null || !traslado.validar()) {
            throw new IllegalArgumentException("Los datos del traslado no son válidos.");
        }

        for (Documento documento : traslado.getDocumentos()) {
            if (!documento.validar() || documento.getTipoDocumento().getAmbito() != AmbitoDocumento.TRASLADO) {
                throw new IllegalArgumentException("Los documentos del traslado no son válidos.");
            }
            documento.actualizarEstado(LocalDate.now());
        }

        Connection conexion = null;

        try {
            conexion = ConexionDB.obtenerConexion();

            if (trasladoRepository.existeCodigo(conexion, traslado.getCodigo())) {
                throw new IllegalArgumentException("Ya existe un traslado con el código " + traslado.getCodigo() + ".");
            }

            conexion.setAutoCommit(false);
            trasladoRepository.insertar(conexion,traslado);

            for (Documento documento : traslado.getDocumentos()) {
                documentoRepository.insertar(conexion, documento);
                documentoRepository.vincularTraslado(conexion, traslado.getIdTraslado(), documento.getIdDocumento());
            }

            conexion.commit();
        } catch (SQLException e) {
            if (conexion != null) {
                try {
                    conexion.rollback();
                } catch (SQLException rollbackException) {
                    e.addSuppressed(rollbackException);
                }
            }

            throw e;

        } finally {
            if (conexion != null) {
                conexion.setAutoCommit(true);
                conexion.close();
            }
        }
    }

    // documentos: lista de verificacion obtenida con DocumentoService.verificarDocumentos
    public void autorizarSalida(Traslado traslado, Usuario responsable, Inspeccion inspeccion,
                                List<Documento> documentos) throws SQLException {
        if (traslado == null) {
            throw new IllegalArgumentException("El traslado no puede ser nulo.");
        }

        if (responsable == null) {
            throw new IllegalArgumentException("El responsable de salida es obligatorio.");
        }

        if (inspeccion == null) {
            throw new IllegalArgumentException("La inspección es obligatoria para autorizar la salida.");
        }

        if (documentos == null) {
            throw new IllegalArgumentException("La verificación de documentos es obligatoria para autorizar la salida.");
        }

        if (traslado.getEstado() != EstadoTraslado.PROGRAMADO) {
            throw new IllegalStateException("Solo se puede autorizar un traslado PROGRAMADO.");
        }

        for (Documento documento : documentos) {
            if (!documento.estaVigente()) {
                throw new IllegalStateException(
                        "No se puede autorizar: existen documentos obligatorios faltantes o vencidos. Solo puede rechazar la salida."
                );
            }
        }

        if (!inspeccion.puedeAutorizar()) {
            throw new IllegalStateException(
                    "El traslado no puede ser autorizado porque la inspección no es conforme."
            );
        }

        traslado.marcarEnTransito(responsable);

        Connection conexion = null;

        try {
            conexion = ConexionDB.obtenerConexion();
            conexion.setAutoCommit(false);

            trasladoRepository.actualizarEstado(conexion, traslado);

            conexion.commit();

        } catch (SQLException e) {
            if (conexion != null) {
                try {
                    conexion.rollback();
                } catch (SQLException rollbackException) {
                    e.addSuppressed(rollbackException);
                }
            }

            throw e;

        } finally {
            if (conexion != null) {
                conexion.setAutoCommit(true);
                conexion.close();
            }
        }
    }

    public void rechazar(Traslado traslado, String motivo, Usuario responsable) throws SQLException {
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

        traslado.rechazar(motivo, responsable);

        Connection conexion = null;

        try {
            conexion = ConexionDB.obtenerConexion();
            conexion.setAutoCommit(false);

            trasladoRepository.actualizarEstado(conexion, traslado);

            conexion.commit();

        } catch (SQLException e) {
            if (conexion != null) {
                try {
                    conexion.rollback();
                } catch (SQLException rollbackException) {
                    e.addSuppressed(rollbackException);
                }
            }

            throw e;

        } finally {
            if (conexion != null) {
                conexion.setAutoCommit(true);
                conexion.close();
            }
        }
    }

    public List<Traslado> listar() throws SQLException {
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            return trasladoRepository.listar(conexion);
        }
    }

    public List<Traslado> buscar(FiltroTraslado filtro) throws SQLException {
        if (filtro == null || !filtro.validar()) {
            throw new IllegalArgumentException("La fecha inicial no puede ser mayor que la fecha final.");
        }

        try (Connection conexion = ConexionDB.obtenerConexion()) {
            return trasladoRepository.buscar(conexion, filtro);
        }
    }

    public List<DetalleTraslado> listarDetalles(Traslado traslado) throws SQLException {
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            return trasladoRepository.listarDetalles(conexion, traslado.getIdTraslado());
        }
    }

    public List<Documento> listarDocumentos(Traslado traslado) throws SQLException {
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            return documentoRepository.listarPorTraslado(conexion, traslado.getIdTraslado());
        }
    }
}
