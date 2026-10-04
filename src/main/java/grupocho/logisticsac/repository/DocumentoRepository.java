package grupocho.logisticsac.repository;

import grupocho.logisticsac.modelo.Documento;
import java.sql.Connection;
import java.sql.SQLException;

public interface DocumentoRepository {
    void insertar(Connection conexion, Documento documento) throws SQLException;
    Documento buscarPorNumero(Connection conexion, String numero) throws SQLException;
}