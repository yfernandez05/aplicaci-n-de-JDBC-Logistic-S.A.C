package grupocho.logisticsac.dao;

import grupocho.logisticsac.enums.ResultadoInspeccion;
import grupocho.logisticsac.enums.Rol;
import grupocho.logisticsac.modelo.Inspeccion;
import grupocho.logisticsac.modelo.Traslado;
import grupocho.logisticsac.modelo.Usuario;
import grupocho.logisticsac.repository.InspeccionRepository;

import java.sql.*;

public class InspeccionDAO implements InspeccionRepository {

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

    @Override
    public Inspeccion buscarPorTraslado(Connection conexion, int idTraslado) throws SQLException {
        String sql = """
        SELECT i.id_inspeccion,
               i.fecha_hora,
               i.resultado,
               i.carga_conforme,
               i.observacion,
               t.id_traslado,
               t.codigo,
               u.id_usuario,
               u.username,
               u.nombre_completo,
               u.rol,
               u.activo
        FROM inspeccion i
        INNER JOIN traslado t ON i.id_traslado = t.id_traslado
        INNER JOIN usuario u ON i.id_vigilante = u.id_usuario
        WHERE i.id_traslado = ?
        """;

        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setInt(1, idTraslado);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Inspeccion inspeccion = new Inspeccion();
                    inspeccion.setIdInspeccion(rs.getInt("id_inspeccion"));
                    inspeccion.setFechaHora(rs.getTimestamp("fecha_hora").toLocalDateTime());
                    inspeccion.setResultado(ResultadoInspeccion.valueOf(rs.getString("resultado")));
                    inspeccion.setCargaConforme(rs.getBoolean("carga_conforme"));
                    inspeccion.setObservacion(rs.getString("observacion"));

                    Traslado traslado = new Traslado();
                    traslado.setIdTraslado(rs.getInt("id_traslado"));
                    traslado.setCodigo(rs.getString("codigo"));

                    Usuario vigilante = new Usuario();
                    vigilante.setIdUsuario(rs.getInt("id_usuario"));
                    vigilante.setUsername(rs.getString("username"));
                    vigilante.setNombreCompleto(rs.getString("nombre_completo"));
                    vigilante.setRol(Rol.valueOf(rs.getString("rol")));
                    vigilante.setActivo(rs.getBoolean("activo"));

                    inspeccion.setTraslado(traslado);
                    inspeccion.setVigilante(vigilante);

                    return inspeccion;
                }
            }
        }

        return null;
    }
}