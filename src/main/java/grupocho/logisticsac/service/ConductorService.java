package grupocho.logisticsac.service;

import grupocho.logisticsac.config.ConexionDB;
import grupocho.logisticsac.dao.ConductorDAO;
import grupocho.logisticsac.modelo.Conductor;
import java.sql.Connection;
import java.sql.SQLException;

public class ConductorService {
    private final ConductorDAO conductorDAO;

    public ConductorService() {
        this.conductorDAO = new ConductorDAO();
    }

    public void registrar(Conductor conductor) throws SQLException {

        if (conductor == null || !conductor.validar()) {
            throw new IllegalArgumentException("Los datos del conductor no son válidos.");
        }

        Connection conexion = null;

        try {
            conexion = ConexionDB.obtenerConexion();
            conexion.setAutoCommit(false);
            conductorDAO.insertar(conexion, conductor);
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

    public Conductor buscarPorDni(String dni) throws SQLException {
        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("El DNI es obligatorio.");
        }

        try (Connection conexion = ConexionDB.obtenerConexion()) {
            return conductorDAO.buscarPorDni(conexion, dni);
        }
    }
}