package grupocho.logisticsac.service;

import grupocho.logisticsac.config.ConexionDB;
import grupocho.logisticsac.dao.ProductoDAO;
import grupocho.logisticsac.modelo.Producto;
import java.sql.Connection;
import java.sql.SQLException;

public class ProductoService {
    private final ProductoDAO productoDAO;

    public ProductoService() {
        this.productoDAO = new ProductoDAO();
    }

    public void registrar(Producto producto) throws SQLException {

        if (producto == null || !producto.validar()) {
            throw new IllegalArgumentException( "Los datos del producto no son válidos." );
        }

        Connection conexion = null;

        try {
            conexion = ConexionDB.obtenerConexion();
            conexion.setAutoCommit(false);
            productoDAO.insertar(conexion, producto);
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

    public Producto buscarPorCodigo(String codigo) throws SQLException {

        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException( "El código del producto es obligatorio.");
        }

        try (Connection conexion = ConexionDB.obtenerConexion()) {
            return productoDAO.buscarPorCodigo(conexion, codigo);
        }
    }
}