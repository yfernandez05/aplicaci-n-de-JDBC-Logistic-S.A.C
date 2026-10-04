package grupocho.logisticsac.dao;

import grupocho.logisticsac.modelo.Almacen;
import grupocho.logisticsac.repository.AlmacenRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AlmacenDAO implements AlmacenRepository {

    public void insertar(Connection conexion, Almacen almacen)
            throws SQLException {

        String sql = """
                INSERT INTO almacen (codigo, nombre, direccion, activo)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, almacen.getCodigo());
            ps.setString(2, almacen.getNombre());
            ps.setString(3, almacen.getDireccion());
            ps.setBoolean(4, almacen.isActivo());

            ps.executeUpdate();
        }
    }

    public Almacen buscarPorCodigo(Connection conexion, String codigo)
            throws SQLException {

        String sql = """
                SELECT id_almacen, codigo, nombre, direccion, activo 
                FROM almacen WHERE codigo = ?
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, codigo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Almacen(
                            rs.getInt("id_almacen"),
                            rs.getString("codigo"),
                            rs.getString("nombre"),
                            rs.getString("direccion"),
                            rs.getBoolean("activo")
                    );
                }
            }
        }

        return null;
    }

    @Override
    public List<Almacen> listar(Connection conexion) throws SQLException {
        List<Almacen> lista = new ArrayList<>();
        String sql = "SELECT id_almacen, codigo, nombre, direccion, activo FROM almacen WHERE activo = TRUE ORDER BY nombre";

        try (PreparedStatement statement = conexion.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Almacen almacen = new Almacen();
                almacen.setIdAlmacen(resultSet.getInt("id_almacen"));
                almacen.setCodigo(resultSet.getString("codigo"));
                almacen.setNombre(resultSet.getString("nombre"));
                almacen.setDireccion(resultSet.getString("direccion"));
                almacen.setActivo(resultSet.getBoolean("activo"));
                lista.add(almacen);
            }
        }

        return lista;
    }

    @Override
    public void actualizar(Connection conexion, Almacen almacen) throws SQLException {
        String sql = "UPDATE almacen SET codigo = ?, nombre = ?, direccion = ? WHERE id_almacen = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, almacen.getCodigo());
            ps.setString(2, almacen.getNombre());
            ps.setString(3, almacen.getDireccion());
            ps.setInt(4, almacen.getIdAlmacen());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(Connection conexion, int idAlmacen) throws SQLException {
        String sql = "UPDATE almacen SET activo = FALSE WHERE id_almacen = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idAlmacen);
            ps.executeUpdate();
        }
    }
}