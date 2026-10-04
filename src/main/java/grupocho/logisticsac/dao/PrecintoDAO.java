package grupocho.logisticsac.dao;

import grupocho.logisticsac.modelo.Precinto;
import grupocho.logisticsac.repository.PrecintoRepository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PrecintoDAO implements PrecintoRepository {

    public void insertar(Connection conexion, Precinto precinto) throws SQLException {
        String sql = """
                INSERT INTO precinto (numero, fecha_colocacion, estado, id_traslado)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, precinto.getNumero());
            ps.setDate(2, Date.valueOf(precinto.getFechaColocacion()));
            ps.setString(3, precinto.getEstado());
            ps.setInt(4, precinto.getTraslado().getIdTraslado());
            ps.executeUpdate();
        }
    }
}