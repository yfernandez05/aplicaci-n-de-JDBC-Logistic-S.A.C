package grupocho.logisticsac.dao;

import grupocho.logisticsac.modelo.Producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductoDAO {

    public void insertar(Connection conexion, Producto producto) throws SQLException {

        String sql = """
                INSERT INTO producto (codigo, descripcion, unidad_medida, activo)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getDescripcion());
            ps.setString(3, producto.getUnidadMedida());
            ps.setBoolean(4, producto.isActivo());

            ps.executeUpdate();
        }
    }

    public Producto buscarPorCodigo(Connection conexion, String codigo)
            throws SQLException {

        String sql = """
                SELECT id_producto, codigo, descripcion, unidad_medida, activo
                FROM producto WHERE codigo = ?
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, codigo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Producto(
                        rs.getInt("id_producto"),
                        rs.getString("codigo"),
                        rs.getString("descripcion"),
                        rs.getString("unidad_medida"),
                        rs.getBoolean("activo")
                    );
                }
            }
        }

        return null;
    }
}