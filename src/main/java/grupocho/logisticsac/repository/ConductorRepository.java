package grupocho.logisticsac.repository;

import grupocho.logisticsac.modelo.Conductor;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ConductorRepository {
    void insertar(Connection conexion, Conductor conductor) throws SQLException;
    Conductor buscarPorDni(Connection conexion, String dni) throws SQLException;
    List<Conductor> listar(Connection conexion) throws SQLException;
    void actualizar(Connection conexion, Conductor conductor) throws SQLException;
    void eliminar(Connection conexion, int idConductor) throws SQLException;
}