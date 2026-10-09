package grupocho.logisticsac.dao;

import grupocho.logisticsac.modelo.Precinto;
import grupocho.logisticsac.modelo.Traslado;
import grupocho.logisticsac.repository.PrecintoRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class PrecintoDAO implements PrecintoRepository {

    public void insertar(Connection conexion, Precinto precinto) throws SQLException {
        String sql = """
                INSERT INTO precinto (numero, fecha_registro, estado, id_traslado)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, precinto.getNumero());
            ps.setTimestamp(2, Timestamp.valueOf(precinto.getFechaRegistro()));
            ps.setString(3, precinto.getEstado());
            ps.setInt(4, precinto.getTraslado().getIdTraslado());
            ps.executeUpdate();
        }
    }

    @Override
    public Precinto buscarPorTraslado(Connection conexion, int idTraslado) throws SQLException {
        String sql = """
                SELECT id_precinto, numero, fecha_registro, estado, id_traslado
                FROM precinto WHERE id_traslado = ?
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idTraslado);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Traslado traslado = new Traslado();
                    traslado.setIdTraslado(rs.getInt("id_traslado"));

                    return new Precinto(
                            rs.getInt("id_precinto"),
                            rs.getString("numero"),
                            rs.getTimestamp("fecha_registro").toLocalDateTime(),
                            rs.getString("estado"),
                            traslado
                    );
                }
            }
        }

        return null;
    }
}
