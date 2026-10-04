package grupocho.logisticsac.service;

import grupocho.logisticsac.config.ConexionDB;
import grupocho.logisticsac.dao.DocumentoDAO;
import grupocho.logisticsac.modelo.Documento;
import java.sql.Connection;
import java.sql.SQLException;

public class DocumentoService {
    private final DocumentoDAO documentoDAO;

    public DocumentoService() {
        this.documentoDAO = new DocumentoDAO();
    }

    public void registrar(Documento documento) throws SQLException {

        if (documento == null || !documento.validar()) {
            throw new IllegalArgumentException("Los datos del documento no son válidos.");
        }

        Connection conexion = null;

        try {
            conexion = ConexionDB.obtenerConexion();
            conexion.setAutoCommit(false);
            documentoDAO.insertar(conexion, documento);
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
            return documentoDAO.buscarPorNumero(conexion,numero);
        }

    }
}