package grupocho.logisticsac.service;

import grupocho.logisticsac.config.ConexionDB;
import grupocho.logisticsac.dao.ProductoDAO;
import grupocho.logisticsac.modelo.Producto;
import grupocho.logisticsac.repository.ProductoRepository;
import grupocho.logisticsac.validation.ProductoValidator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ProductoService {
    private final ProductoRepository productoRepository;
    private final ProductoValidator productoValidator;
    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
        this.productoValidator = new ProductoValidator();
    }

    public void registrar(Producto producto) throws SQLException {
        productoValidator.validar(producto);
        Connection conexion = null;
        try {
            conexion = ConexionDB.obtenerConexion();
            conexion.setAutoCommit(false);
            productoRepository.insertar(conexion, producto);
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
            throw new IllegalArgumentException("El código del producto es obligatorio.");
        }
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            return productoRepository.buscarPorCodigo(conexion, codigo);
        }
    }

    public List<Producto> listar() throws SQLException {
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            return productoRepository.listar(conexion);
        }
    }

    public void actualizar(Producto producto) throws SQLException {
        productoValidator.validar(producto);
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            productoRepository.actualizar(conexion, producto);
        }
    }

    public void eliminar(int idProducto) throws SQLException {
        if (idProducto <= 0) {
            throw new IllegalArgumentException("El producto seleccionado no es válido.");
        }
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            productoRepository.eliminar(conexion, idProducto);
        }
    }
}