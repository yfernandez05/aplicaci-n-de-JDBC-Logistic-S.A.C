package grupocho.logisticsac.service;

import grupocho.logisticsac.config.ConexionDB;
import grupocho.logisticsac.enums.AmbitoDocumento;
import grupocho.logisticsac.modelo.DetalleTraslado;
import grupocho.logisticsac.modelo.Documento;
import grupocho.logisticsac.modelo.FiltroTraslado;
import grupocho.logisticsac.modelo.Inspeccion;
import grupocho.logisticsac.modelo.Traslado;
import grupocho.logisticsac.modelo.Usuario;
import grupocho.logisticsac.repository.DocumentoRepository;
import grupocho.logisticsac.repository.TrasladoRepository;
import grupocho.logisticsac.validation.DocumentoValidator;
import grupocho.logisticsac.validation.TrasladoValidator;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class TrasladoService {

    private final TrasladoRepository trasladoRepository;
    private final DocumentoRepository documentoRepository;
    private final TrasladoValidator trasladoValidator;
    private final DocumentoValidator documentoValidator;

    public TrasladoService(TrasladoRepository trasladoRepository, DocumentoRepository documentoRepository) {
        this.trasladoRepository = trasladoRepository;
        this.documentoRepository = documentoRepository;
        this.trasladoValidator = new TrasladoValidator();
        this.documentoValidator = new DocumentoValidator();
    }

    // Registra el traslado con su detalle de productos y sus documentos en una sola transaccion.
    public void registrar(Traslado traslado) throws SQLException {

        trasladoValidator.validar(traslado);

        for (Documento documento : traslado.getDocumentos()) {
            documentoValidator.validar(documento);
            documentoValidator.validarAmbito(documento, AmbitoDocumento.TRASLADO);
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
        trasladoValidator.validarDocumentosVigentes(documentos);
        trasladoValidator.validarAutorizacion(traslado, responsable, inspeccion);

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
        trasladoValidator.validarRechazo(traslado, motivo, responsable);

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
        trasladoValidator.validarFiltro(filtro);

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
