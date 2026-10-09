package grupocho.logisticsac.repository;

import grupocho.logisticsac.modelo.Documento;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface DocumentoRepository {
    void insertar(Connection conexion, Documento documento) throws SQLException;
    Documento buscarPorNumero(Connection conexion, String numero) throws SQLException;
    void vincularVehiculo(Connection conexion, int idVehiculo, int idDocumento) throws SQLException;
    void vincularConductor(Connection conexion, int idConductor, int idDocumento) throws SQLException;
    void vincularTraslado(Connection conexion, int idTraslado, int idDocumento) throws SQLException;
    List<Documento> listarPorVehiculo(Connection conexion, int idVehiculo) throws SQLException;
    List<Documento> listarPorConductor(Connection conexion, int idConductor) throws SQLException;
    List<Documento> listarPorTraslado(Connection conexion, int idTraslado) throws SQLException;
}
