package grupocho.logisticsac.dao;

import grupocho.logisticsac.modelo.Recepcion;
import grupocho.logisticsac.modelo.Traslado;
import grupocho.logisticsac.modelo.Usuario;
import grupocho.logisticsac.repository.RecepcionRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class RecepcionDAO implements RecepcionRepository {

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

    @Override
    public Recepcion buscarPorTraslado(Connection conexion, int idTraslado) throws SQLException {
        String sql = """
                SELECT r.id_recepcion, r.fecha_hora_llegada, r.precinto_conforme, r.carga_conforme,
                       r.observacion, r.id_traslado, u.id_usuario, u.nombre_completo
                FROM recepcion r
                INNER JOIN usuario u ON r.id_despachador = u.id_usuario
                WHERE r.id_traslado = ?
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idTraslado);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Traslado traslado = new Traslado();
                    traslado.setIdTraslado(rs.getInt("id_traslado"));

                    Usuario despachador = new Usuario();
                    despachador.setIdUsuario(rs.getInt("id_usuario"));
                    despachador.setNombreCompleto(rs.getString("nombre_completo"));

                    return new Recepcion(
                            rs.getInt("id_recepcion"),
                            rs.getTimestamp("fecha_hora_llegada").toLocalDateTime(),
                            rs.getBoolean("precinto_conforme"),
                            rs.getBoolean("carga_conforme"),
                            rs.getString("observacion"),
                            traslado,
                            despachador
                    );
                }
            }
        }

        return null;
    }
}
