package grupocho.logisticsac.dao;

import grupocho.logisticsac.modelo.Almacen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AlmacenDAO {

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
}