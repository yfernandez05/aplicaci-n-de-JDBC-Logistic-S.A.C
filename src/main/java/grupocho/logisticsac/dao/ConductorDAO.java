package grupocho.logisticsac.dao;

import grupocho.logisticsac.modelo.Conductor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConductorDAO {

    public void insertar(Connection conexion, Conductor conductor) throws SQLException {

        String sql = """
                INSERT INTO conductor (dni, nombres, numero_licencia, categoria_licencia, activo)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, conductor.getDni());
            ps.setString(2, conductor.getNombres());
            ps.setString(3, conductor.getNumeroLicencia());
            ps.setString(4, conductor.getCategoriaLicencia());
            ps.setBoolean(5, conductor.isActivo());

            ps.executeUpdate();
        }
    }

    public Conductor buscarPorDni(Connection conexion, String dni) throws SQLException {

        String sql = """
                SELECT id_conductor, dni, nombres, numero_licencia, categoria_licencia, activo
                FROM conductor WHERE dni = ?
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, dni);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Conductor(
                        rs.getInt("id_conductor"),
                        rs.getString("dni"),
                        rs.getString("nombres"),
                        rs.getString("numero_licencia"),
                        rs.getString("categoria_licencia"),
                        rs.getBoolean("activo")
                    );
                }
            }
        }

        return null;
    }
}