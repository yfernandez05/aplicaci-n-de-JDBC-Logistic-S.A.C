package grupocho.logisticsac.repository;

import grupocho.logisticsac.modelo.Recepcion;
import java.sql.Connection;
import java.sql.SQLException;

public interface RecepcionRepository {
    void insertar(Connection conexion, Recepcion recepcion) throws SQLException;
    Recepcion buscarPorTraslado(Connection conexion, int idTraslado) throws SQLException;
}
