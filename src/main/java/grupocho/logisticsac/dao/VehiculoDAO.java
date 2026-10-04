package grupocho.logisticsac.dao;
import grupocho.logisticsac.modelo.Vehiculo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VehiculoDAO {

    public void insertar(Connection conexion, Vehiculo vehiculo) throws SQLException {

        String sql = """
                INSERT INTO vehiculo (placa, tipo, capacidad_carga, condicion, estado, activo)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, vehiculo.getPlaca());
            ps.setString(2, vehiculo.getTipo());
            ps.setDouble(3, vehiculo.getCapacidadCarga());
            ps.setString(4, vehiculo.getCondicion());
            ps.setString(5, vehiculo.getEstado());
            ps.setBoolean(6, vehiculo.isActivo());

            ps.executeUpdate();
        }
    }

    public Vehiculo buscarPorPlaca(Connection conexion, String placa)
            throws SQLException {

        String sql = """
                SELECT id_vehiculo, placa, tipo, capacidad_carga, condicion, estado, activo
                FROM vehiculo WHERE placa = ?
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, placa);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Vehiculo(
                        rs.getInt("id_vehiculo"),
                        rs.getString("placa"),
                        rs.getString("tipo"),
                        rs.getDouble("capacidad_carga"),
                        rs.getString("condicion"),
                        rs.getString("estado"),
                        rs.getBoolean("activo")
                    );
                }
            }
        }

        return null;
    }
}