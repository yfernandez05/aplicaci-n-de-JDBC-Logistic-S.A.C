package grupocho.logisticsac.dao;

import grupocho.logisticsac.modelo.Conductor;
import grupocho.logisticsac.repository.ConductorRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConductorDAO implements ConductorRepository {

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

    @Override
    public List<Conductor> listar(Connection conexion) throws SQLException {
        List<Conductor> lista = new ArrayList<>();
        String sql = "SELECT id_conductor, dni, nombres, numero_licencia, categoria_licencia, activo FROM conductor WHERE activo = TRUE ORDER BY nombres";

        try (PreparedStatement statement = conexion.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Conductor conductor = new Conductor();
                conductor.setIdConductor(resultSet.getInt("id_conductor"));
                conductor.setDni(resultSet.getString("dni"));
                conductor.setNombres(resultSet.getString("nombres"));
                conductor.setNumeroLicencia(resultSet.getString("numero_licencia"));
                conductor.setCategoriaLicencia(resultSet.getString("categoria_licencia"));
                conductor.setActivo(resultSet.getBoolean("activo"));
                lista.add(conductor);
            }
        }

        return lista;
    }

    @Override
    public void actualizar(Connection conexion, Conductor conductor) throws SQLException {
        String sql = "UPDATE conductor SET dni = ?, nombres = ?, numero_licencia = ?, categoria_licencia = ? WHERE id_conductor = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, conductor.getDni());
            ps.setString(2, conductor.getNombres());
            ps.setString(3, conductor.getNumeroLicencia());
            ps.setString(4, conductor.getCategoriaLicencia());
            ps.setInt(5, conductor.getIdConductor());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(Connection conexion, int idConductor) throws SQLException {
        String sql = "UPDATE conductor SET activo = FALSE WHERE id_conductor = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idConductor);
            ps.executeUpdate();
        }
    }
}