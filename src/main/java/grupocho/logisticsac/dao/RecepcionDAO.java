package grupocho.logisticsac.dao;

import grupocho.logisticsac.modelo.Recepcion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class RecepcionDAO {

    public void insertar(Connection conexion, Recepcion recepcion) throws SQLException {
        String sql = """
                INSERT INTO recepcion (fecha_hora_llegada, precinto_conforme, carga_conforme, observacion, id_traslado, id_despachador)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(recepcion.getFechaHoraRecepcion()));
            ps.setBoolean(2, recepcion.isPrecintoConforme());
            ps.setBoolean(3, recepcion.isCargaConforme());
            ps.setString(4, recepcion.getObservacion());
            ps.setInt(5, recepcion.getTraslado().getIdTraslado());
            ps.setInt(6, recepcion.getDespachador().getIdUsuario());
            ps.executeUpdate();
        }
    }
}