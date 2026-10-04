package grupocho.logisticsac.repository;

import grupocho.logisticsac.modelo.Inspeccion;
import java.sql.Connection;
import java.sql.SQLException;

public interface InspeccionRepository {
    void insertar(Connection conexion, Inspeccion inspeccion) throws SQLException;
    Inspeccion buscarPorTraslado(Connection conexion, int idTraslado) throws SQLException;

}