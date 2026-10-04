package grupocho.logisticsac.dao;

import grupocho.logisticsac.enums.ResultadoInspeccion;
import grupocho.logisticsac.modelo.Inspeccion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class InspeccionDAO {

    public void insertar(Connection conexion, Inspeccion inspeccion) throws SQLException {

        String sql = """
                INSERT INTO inspeccion (fecha_hora, resultado, carga_conforme, observacion, id_traslado, id_vigilante)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setTimestamp( 1, Timestamp.valueOf( inspeccion.getFechaHora()));
            ps.setString(2, inspeccion.getResultado().name());
            ps.setBoolean(3, inspeccion.isCargaConforme());
            ps.setString(4,inspeccion.getObservacion());
            ps.setInt(5,inspeccion.getTraslado().getIdTraslado());
            ps.setInt(6,inspeccion.getVigilante().getIdUsuario());
            ps.executeUpdate();
        }
    }
}