package grupocho.logisticsac.dao;

import grupocho.logisticsac.modelo.Producto;
import grupocho.logisticsac.repository.ProductoRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO implements ProductoRepository {

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

    @Override
    public List<Producto> listar(Connection conexion) throws SQLException {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT id_producto, codigo, descripcion, unidad_medida, activo FROM producto WHERE activo = TRUE ORDER BY descripcion";

        try (PreparedStatement statement = conexion.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Producto producto = new Producto();
                producto.setIdProducto(resultSet.getInt("id_producto"));
                producto.setCodigo(resultSet.getString("codigo"));
                producto.setDescripcion(resultSet.getString("descripcion"));
                producto.setUnidadMedida(resultSet.getString("unidad_medida"));
                producto.setActivo(resultSet.getBoolean("activo"));
                lista.add(producto);
            }
        }

        return lista;
    }

    @Override
    public void actualizar(Connection conexion, Producto producto) throws SQLException {
        String sql = "UPDATE producto SET codigo = ?, descripcion = ?, unidad_medida = ? WHERE id_producto = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getDescripcion());
            ps.setString(3, producto.getUnidadMedida());
            ps.setInt(4, producto.getIdProducto());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(Connection conexion, int idProducto) throws SQLException {
        String sql = "UPDATE producto SET activo = FALSE WHERE id_producto = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ps.executeUpdate();
        }
    }
}