package grupocho.logisticsac.repository;

import grupocho.logisticsac.modelo.Almacen;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface AlmacenRepository {
    void insertar(Connection conexion, Almacen almacen) throws SQLException;
    Almacen buscarPorCodigo(Connection conexion, String codigo) throws SQLException;
    List<Almacen> listar(Connection conexion) throws SQLException;
    void actualizar(Connection conexion, Almacen almacen) throws SQLException;
    void eliminar(Connection conexion, int idAlmacen) throws SQLException;
}