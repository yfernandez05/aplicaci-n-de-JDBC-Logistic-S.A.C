package grupocho.logisticsac.repository;

import grupocho.logisticsac.modelo.Evidencia;
import java.sql.Connection;
import java.sql.SQLException;

public interface EvidenciaRepository {
    void insertar(Connection conexion, Evidencia evidencia) throws SQLException;
}