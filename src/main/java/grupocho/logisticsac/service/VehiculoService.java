package grupocho.logisticsac.service;

import grupocho.logisticsac.config.ConexionDB;
import grupocho.logisticsac.dao.VehiculoDAO;
import grupocho.logisticsac.modelo.Vehiculo;
import java.sql.Connection;
import java.sql.SQLException;

public class VehiculoService {

    private final VehiculoDAO vehiculoDAO;

    public VehiculoService() {
        this.vehiculoDAO = new VehiculoDAO();
    }

    public void registrar(Vehiculo vehiculo) throws SQLException {

        if (vehiculo == null || !vehiculo.validar()) {
            throw new IllegalArgumentException("Los datos del vehículo no son válidos.");
        }

        Connection conexion = null;

        try {
            conexion = ConexionDB.obtenerConexion();
            conexion.setAutoCommit(false);
            vehiculoDAO.insertar(conexion, vehiculo);
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

    public Vehiculo buscarPorPlaca(String placa) throws SQLException {

        if (placa == null || placa.isBlank()) {
            throw new IllegalArgumentException("La placa es obligatoria.");
        }

        try (Connection conexion = ConexionDB.obtenerConexion()) {
            return vehiculoDAO.buscarPorPlaca(conexion, placa);
        }
    }
}