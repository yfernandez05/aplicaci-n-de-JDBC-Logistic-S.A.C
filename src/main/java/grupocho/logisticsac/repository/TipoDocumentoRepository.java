package grupocho.logisticsac.repository;

import grupocho.logisticsac.modelo.TipoDocumento;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface TipoDocumentoRepository {
    void insertar(Connection conexion, TipoDocumento tipoDocumento) throws SQLException;
    TipoDocumento buscarPorNombre(Connection conexion, String nombre) throws SQLException;
    List<TipoDocumento> listar(Connection conexion) throws SQLException;
}
