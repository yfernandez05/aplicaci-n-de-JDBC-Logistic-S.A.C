package grupocho.logisticsac.service;

import grupocho.logisticsac.config.ConexionDB;
import grupocho.logisticsac.dao.AlmacenDAO;
import grupocho.logisticsac.modelo.Almacen;
import java.sql.Connection;
import java.sql.SQLException;

public class AlmacenService {
    private final AlmacenDAO almacenDAO;

    public AlmacenService() {
        this.almacenDAO = new AlmacenDAO();
    }

    public void registrar(Almacen almacen) throws SQLException {

        if (almacen == null || !almacen.validar()) {
            throw new IllegalArgumentException("Los datos del almacén no son válidos.");
        }

        Connection conexion = null;

        try {
            conexion = ConexionDB.obtenerConexion();

            conexion.setAutoCommit(false);

            almacenDAO.insertar(conexion, almacen);

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

    public Almacen buscarPorCodigo(String codigo) throws SQLException {

        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del almacén es obligatorio.");
        }

        try (Connection conexion = ConexionDB.obtenerConexion()) {
            return almacenDAO.buscarPorCodigo(conexion, codigo);
        }
    }
}