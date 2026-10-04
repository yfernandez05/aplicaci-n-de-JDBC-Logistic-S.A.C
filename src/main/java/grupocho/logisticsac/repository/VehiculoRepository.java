package grupocho.logisticsac.repository;

import grupocho.logisticsac.modelo.Vehiculo;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface VehiculoRepository {
    void insertar(Connection conexion, Vehiculo vehiculo) throws SQLException;
    Vehiculo buscarPorPlaca(Connection conexion, String placa) throws SQLException;
    List<Vehiculo> listar(Connection conexion) throws SQLException;
    void actualizar(Connection conexion, Vehiculo vehiculo) throws SQLException;
    void eliminar(Connection conexion, int idVehiculo) throws SQLException;
}