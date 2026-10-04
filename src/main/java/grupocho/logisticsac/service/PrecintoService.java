package grupocho.logisticsac.service;

import grupocho.logisticsac.config.ConexionDB;
import grupocho.logisticsac.dao.PrecintoDAO;
import grupocho.logisticsac.modelo.Precinto;
import java.sql.Connection;
import java.sql.SQLException;

public class PrecintoService {

    private final PrecintoDAO precintoDAO;

    public PrecintoService() {
        this.precintoDAO = new PrecintoDAO();
    }

    public void registrar(Precinto precinto) throws SQLException {
        if (precinto == null || !precinto.validar()) {
            throw new IllegalArgumentException("Los datos del precinto no son válidos.");
        }

        Connection conexion = null;

        try {
            conexion = ConexionDB.obtenerConexion();
            conexion.setAutoCommit(false);
            precintoDAO.insertar(conexion, precinto);
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
}