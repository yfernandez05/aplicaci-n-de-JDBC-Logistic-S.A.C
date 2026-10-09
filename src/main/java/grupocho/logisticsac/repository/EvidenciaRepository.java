package grupocho.logisticsac.repository;

import grupocho.logisticsac.modelo.Evidencia;
import grupocho.logisticsac.modelo.Inspeccion;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface EvidenciaRepository {
    void insertar(Connection conexion, Evidencia evidencia) throws SQLException;
    List<Evidencia> listarPorInspeccion(Connection conexion, Inspeccion inspeccion) throws SQLException;
}
