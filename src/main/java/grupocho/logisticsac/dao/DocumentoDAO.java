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
import java.util.ArrayList;
import java.util.List;

public class DocumentoDAO implements DocumentoRepository {

    private static final String SELECT_BASE = """
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
            """;

    public void insertar(Connection conexion, Documento documento) throws SQLException {

        String sql = """
                INSERT INTO documento (numero, fecha_emision, fecha_vencimiento, estado, observacion, id_tipo_documento)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, documento.getNumero());
            ps.setDate(2, Date.valueOf(documento.getFechaEmision()) );
            ps.setDate(3, Date.valueOf(documento.getFechaVencimiento()));
            ps.setString(4, documento.getEstado());
            ps.setString(5, documento.getObservacion());
            ps.setInt(6, documento.getTipoDocumento().getIdTipoDocumento());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (!rs.next()) {
                    throw new SQLException("No se pudo obtener el ID del documento.");
                }
                documento.setIdDocumento(rs.getInt(1));
            }
        }
    }

    public Documento buscarPorNumero(Connection conexion, String numero) throws SQLException {

        String sql = SELECT_BASE + " WHERE d.numero = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, numero);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }

        return null;
    }

    @Override
    public void vincularVehiculo(Connection conexion, int idVehiculo, int idDocumento) throws SQLException {
        String sql = "INSERT INTO vehiculo_documento (id_vehiculo, id_documento) VALUES (?, ?)";
        vincular(conexion, sql, idVehiculo, idDocumento);
    }

    @Override
    public void vincularConductor(Connection conexion, int idConductor, int idDocumento) throws SQLException {
        String sql = "INSERT INTO conductor_documento (id_conductor, id_documento) VALUES (?, ?)";
        vincular(conexion, sql, idConductor, idDocumento);
    }

    @Override
    public void vincularTraslado(Connection conexion, int idTraslado, int idDocumento) throws SQLException {
        String sql = "INSERT INTO traslado_documento (id_traslado, id_documento) VALUES (?, ?)";
        vincular(conexion, sql, idTraslado, idDocumento);
    }

    @Override
    public List<Documento> listarPorVehiculo(Connection conexion, int idVehiculo) throws SQLException {
        String sql = SELECT_BASE + """
                INNER JOIN vehiculo_documento r ON r.id_documento = d.id_documento
                WHERE r.id_vehiculo = ? ORDER BY td.nombre
                """;
        return listar(conexion, sql, idVehiculo);
    }

    @Override
    public List<Documento> listarPorConductor(Connection conexion, int idConductor) throws SQLException {
        String sql = SELECT_BASE + """
                INNER JOIN conductor_documento r ON r.id_documento = d.id_documento
                WHERE r.id_conductor = ? ORDER BY td.nombre
                """;
        return listar(conexion, sql, idConductor);
    }

    @Override
    public List<Documento> listarPorTraslado(Connection conexion, int idTraslado) throws SQLException {
        String sql = SELECT_BASE + """
                INNER JOIN traslado_documento r ON r.id_documento = d.id_documento
                WHERE r.id_traslado = ? ORDER BY td.nombre
                """;
        return listar(conexion, sql, idTraslado);
    }

    private void vincular(Connection conexion, String sql, int idPropietario, int idDocumento) throws SQLException {
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idPropietario);
            ps.setInt(2, idDocumento);
            ps.executeUpdate();
        }
    }

    private List<Documento> listar(Connection conexion, String sql, int idPropietario) throws SQLException {
        List<Documento> lista = new ArrayList<>();

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idPropietario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }

        return lista;
    }

    private Documento mapear(ResultSet rs) throws SQLException {
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
