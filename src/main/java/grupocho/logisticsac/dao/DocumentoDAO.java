package grupocho.logisticsac.dao;

import grupocho.logisticsac.enums.AmbitoDocumento;
import grupocho.logisticsac.modelo.Documento;
import grupocho.logisticsac.modelo.TipoDocumento;
import grupocho.logisticsac.repository.DocumentoRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class DocumentoDAO implements DocumentoRepository {

    public void insertar(Connection conexion, Documento documento) throws SQLException {

        String sql = """
                INSERT INTO documento (numero, fecha_emision, fecha_vencimiento, estado, observacion, id_tipo_documento)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, documento.getNumero());
            ps.setDate(2, Date.valueOf(documento.getFechaEmision()) );
            ps.setDate(3, Date.valueOf(documento.getFechaVencimiento()));
            ps.setString(4, documento.getEstado());
            ps.setString(5, documento.getObservacion());
            ps.setInt(6, documento.getTipoDocumento().getIdTipoDocumento());
            ps.executeUpdate();
        }
    }

    public Documento buscarPorNumero(Connection conexion, String numero) throws SQLException {

        String sql = """
                SELECT d.id_documento,
                    d.numero,
                    d.fecha_emision,
                    d.fecha_vencimiento,
                    d.estado,
                    d.observacion,
                    
                    td.id_tipo_documento,
                    td.nombre,
                    td.ambito,
                    td.obligatorio,
                    td.activo
                FROM documento d
                INNER JOIN tipo_documento td ON d.id_tipo_documento = td.id_tipo_documento
                WHERE d.numero = ?
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, numero);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TipoDocumento tipoDocumento = new TipoDocumento(
                        rs.getInt("id_tipo_documento"),
                        rs.getString("nombre"),
                        AmbitoDocumento.valueOf( rs.getString("ambito")),
                        rs.getBoolean("obligatorio"),
                        rs.getBoolean("activo")
                    );

                    return new Documento(
                        rs.getInt("id_documento"),
                        rs.getString("numero"),
                        rs.getDate("fecha_emision").toString(),
                        rs.getDate("fecha_vencimiento").toString(),
                        rs.getString("estado"),
                        rs.getString("observacion"),
                        tipoDocumento
                    );
                }
            }
        }

        return null;
    }
}