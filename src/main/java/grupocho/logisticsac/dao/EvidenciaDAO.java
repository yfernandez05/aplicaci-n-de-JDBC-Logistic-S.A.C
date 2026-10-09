package grupocho.logisticsac.dao;

import grupocho.logisticsac.modelo.Evidencia;
import grupocho.logisticsac.modelo.Inspeccion;
import grupocho.logisticsac.repository.EvidenciaRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class EvidenciaDAO implements EvidenciaRepository {

    public void insertar(Connection conexion, Evidencia evidencia) throws SQLException {
        String sql = """
                INSERT INTO evidencia (ruta_archivo, descripcion, fecha_hora, id_inspeccion)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, evidencia.getRutaArchivo());
            ps.setString(2, evidencia.getDescripcion());
            ps.setTimestamp(3, Timestamp.valueOf(evidencia.getFechaHoraRegistro()));
            ps.setInt(4, evidencia.getInspeccion().getIdInspeccion());
            ps.executeUpdate();
        }
    }

    @Override
    public List<Evidencia> listarPorInspeccion(Connection conexion, Inspeccion inspeccion) throws SQLException {
        List<Evidencia> lista = new ArrayList<>();

        String sql = """
                SELECT id_evidencia, ruta_archivo, descripcion, fecha_hora
                FROM evidencia WHERE id_inspeccion = ? ORDER BY id_evidencia
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, inspeccion.getIdInspeccion());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Evidencia(
                            rs.getInt("id_evidencia"),
                            rs.getString("ruta_archivo"),
                            rs.getString("descripcion"),
                            rs.getTimestamp("fecha_hora").toLocalDateTime(),
                            inspeccion
                    ));
                }
            }
        }

        return lista;
    }
}
