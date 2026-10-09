package grupocho.logisticsac.dao;

import grupocho.logisticsac.enums.AmbitoDocumento;
import grupocho.logisticsac.modelo.TipoDocumento;
import grupocho.logisticsac.repository.TipoDocumentoRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TipoDocumentoDAO implements TipoDocumentoRepository {

    public void insertar(Connection conexion, TipoDocumento tipoDocumento) throws SQLException {

        String sql = """
                INSERT INTO tipo_documento (nombre, ambito, obligatorio, activo)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, tipoDocumento.getNombre());
            ps.setString(2, tipoDocumento.getAmbito().name());
            ps.setBoolean(3, tipoDocumento.isObligatorio());
            ps.setBoolean(4, tipoDocumento.isActivo());

            ps.executeUpdate();
        }
    }

    public TipoDocumento buscarPorNombre( Connection conexion, String nombre) throws SQLException {

        String sql = """
                SELECT id_tipo_documento, nombre, ambito, obligatorio, activo
                FROM tipo_documento WHERE nombre = ?
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, nombre);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new TipoDocumento(
                        rs.getInt("id_tipo_documento"),
                        rs.getString("nombre"),
                        AmbitoDocumento.valueOf(rs.getString("ambito")),
                        rs.getBoolean("obligatorio"),
                        rs.getBoolean("activo")
                    );
                }
            }
        }

        return null;
    }

    @Override
    public List<TipoDocumento> listar(Connection conexion) throws SQLException {
        List<TipoDocumento> lista = new ArrayList<>();
        String sql = "SELECT id_tipo_documento, nombre, ambito, obligatorio, activo FROM tipo_documento WHERE activo = TRUE ORDER BY ambito, nombre";

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new TipoDocumento(
                        rs.getInt("id_tipo_documento"),
                        rs.getString("nombre"),
                        AmbitoDocumento.valueOf(rs.getString("ambito")),
                        rs.getBoolean("obligatorio"),
                        rs.getBoolean("activo")
                ));
            }
        }

        return lista;
    }
}