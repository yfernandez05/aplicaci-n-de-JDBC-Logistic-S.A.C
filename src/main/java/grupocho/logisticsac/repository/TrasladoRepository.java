package grupocho.logisticsac.repository;

import grupocho.logisticsac.modelo.Traslado;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface TrasladoRepository {
    void insertar(Connection conexion, Traslado traslado) throws SQLException;
    void actualizarEstado(Connection conexion, Traslado traslado) throws SQLException;
    List<Traslado> listar(Connection conexion) throws SQLException;
    List<Traslado> buscar( Connection conexion, String codigo, java.time.LocalDate fecha,
                           grupocho.logisticsac.enums.EstadoTraslado estado, Integer idVehiculo,
                           Integer idConductor, Integer idAlmacen) throws SQLException;
}