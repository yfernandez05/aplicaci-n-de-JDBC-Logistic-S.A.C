package grupocho.logisticsac.service;

import grupocho.logisticsac.config.ConexionDB;
import grupocho.logisticsac.dao.TipoDocumentoDAO;
import grupocho.logisticsac.modelo.TipoDocumento;
import java.sql.Connection;
import java.sql.SQLException;

public class TipoDocumentoService {
    private final TipoDocumentoDAO tipoDocumentoDAO;

    public TipoDocumentoService() {
        this.tipoDocumentoDAO = new TipoDocumentoDAO();
    }

    public void registrar(TipoDocumento tipoDocumento) throws SQLException {

        if (tipoDocumento == null || !tipoDocumento.validar()) {
            throw new IllegalArgumentException("Los datos del tipo de documento no son válidos.");
        }

        Connection conexion = null;

        try {
            conexion = ConexionDB.obtenerConexion();
            conexion.setAutoCommit(false);
            tipoDocumentoDAO.insertar(conexion, tipoDocumento);
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

    public TipoDocumento buscarPorNombre(String nombre)
            throws SQLException {

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del tipo de documento es obligatorio."
            );
        }

        try (Connection conexion = ConexionDB.obtenerConexion()) {

            return tipoDocumentoDAO.buscarPorNombre(
                    conexion,
                    nombre
            );
        }
    }
}