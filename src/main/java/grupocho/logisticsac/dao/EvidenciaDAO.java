package grupocho.logisticsac.dao;

import grupocho.logisticsac.modelo.Evidencia;
import grupocho.logisticsac.repository.EvidenciaRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class EvidenciaDAO implements EvidenciaRepository {

    public void insertar(Connection conexion, Evidencia evidencia) throws SQLException {
        String sql = """
                INSERT INTO evidencia (ruta_archivo, descripcion, fecha_hora_registro, id_inspeccion)
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
}