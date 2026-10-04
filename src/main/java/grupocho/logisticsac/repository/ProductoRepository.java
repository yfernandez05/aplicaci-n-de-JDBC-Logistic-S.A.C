package grupocho.logisticsac.repository;

import grupocho.logisticsac.modelo.Producto;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ProductoRepository {
    void insertar(Connection conexion, Producto producto) throws SQLException;
    Producto buscarPorCodigo(Connection conexion, String codigo) throws SQLException;
    List<Producto> listar(Connection conexion) throws SQLException;
    void actualizar(Connection conexion, Producto producto) throws SQLException;
    void eliminar(Connection conexion, int idProducto) throws SQLException;
}