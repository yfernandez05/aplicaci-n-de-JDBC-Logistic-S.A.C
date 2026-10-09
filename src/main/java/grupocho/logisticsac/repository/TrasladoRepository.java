package grupocho.logisticsac.repository;

import grupocho.logisticsac.modelo.DetalleTraslado;
import grupocho.logisticsac.modelo.FiltroTraslado;
import grupocho.logisticsac.modelo.Traslado;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface TrasladoRepository {
    void insertar(Connection conexion, Traslado traslado) throws SQLException;
    void actualizarEstado(Connection conexion, Traslado traslado) throws SQLException;
    List<Traslado> listar(Connection conexion) throws SQLException;
    List<Traslado> buscar(Connection conexion, FiltroTraslado filtro) throws SQLException;
    boolean existeCodigo(Connection conexion, String codigo) throws SQLException;
    List<DetalleTraslado> listarDetalles(Connection conexion, int idTraslado) throws SQLException;
}
