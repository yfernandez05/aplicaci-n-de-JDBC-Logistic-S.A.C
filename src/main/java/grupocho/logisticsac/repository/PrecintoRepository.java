package grupocho.logisticsac.repository;

import grupocho.logisticsac.modelo.Precinto;
import java.sql.Connection;
import java.sql.SQLException;

public interface PrecintoRepository {
    void insertar(Connection conexion, Precinto precinto) throws SQLException;
}