package grupocho.logisticsac.service;

import grupocho.logisticsac.config.ConexionDB;
import grupocho.logisticsac.dao.DocumentoDAO;
import grupocho.logisticsac.modelo.Documento;
import grupocho.logisticsac.repository.DocumentoRepository;

import java.sql.Connection;
import java.sql.SQLException;

public class DocumentoService {
    private final DocumentoRepository documentoRepository;

    public DocumentoService(DocumentoRepository documentoRepository) {
        this.documentoRepository = documentoRepository;
    }

    public void registrar(Documento documento) throws SQLException {

        if (documento == null || !documento.validar()) {
            throw new IllegalArgumentException("Los datos del documento no son válidos.");
        }

        Connection conexion = null;

        try {
            conexion = ConexionDB.obtenerConexion();
            conexion.setAutoCommit(false);
            documentoRepository.insertar(conexion, documento);
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

    public Documento buscarPorNumero(String numero) throws SQLException {

        if (numero == null || numero.isBlank()) {
            throw new IllegalArgumentException( "El número del documento es obligatorio." );
        }

        try (Connection conexion = ConexionDB.obtenerConexion()) {
            return documentoRepository.buscarPorNumero(conexion,numero);
        }

    }
}