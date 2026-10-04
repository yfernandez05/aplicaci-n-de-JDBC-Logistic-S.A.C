package grupocho.logisticsac.dao;
import grupocho.logisticsac.modelo.Vehiculo;
import grupocho.logisticsac.repository.VehiculoRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VehiculoDAO implements VehiculoRepository {

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

    @Override
    public List<Vehiculo> listar(Connection conexion) throws SQLException {
        List<Vehiculo> lista = new ArrayList<>();
        String sql = "SELECT id_vehiculo, placa, tipo, capacidad_carga, condicion, estado, activo FROM vehiculo WHERE activo = TRUE ORDER BY placa";

        try (PreparedStatement statement = conexion.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Vehiculo vehiculo = new Vehiculo();
                vehiculo.setIdVehiculo(resultSet.getInt("id_vehiculo"));
                vehiculo.setPlaca(resultSet.getString("placa"));
                vehiculo.setTipo(resultSet.getString("tipo"));
                vehiculo.setCapacidadCarga(resultSet.getDouble("capacidad_carga"));
                vehiculo.setCondicion(resultSet.getString("condicion"));
                vehiculo.setEstado(resultSet.getString("estado"));
                vehiculo.setActivo(resultSet.getBoolean("activo"));
                lista.add(vehiculo);
            }
        }

        return lista;
    }

    @Override
    public void actualizar(Connection conexion, Vehiculo vehiculo) throws SQLException {
        String sql = "UPDATE vehiculo SET placa = ?, tipo = ?, capacidad_carga = ?, condicion = ?, estado = ? WHERE id_vehiculo = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, vehiculo.getPlaca());
            ps.setString(2, vehiculo.getTipo());
            ps.setDouble(3, vehiculo.getCapacidadCarga());
            ps.setString(4, vehiculo.getCondicion());
            ps.setString(5, vehiculo.getEstado());
            ps.setInt(6, vehiculo.getIdVehiculo());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(Connection conexion, int idVehiculo) throws SQLException {
        String sql = "UPDATE vehiculo SET activo = FALSE WHERE id_vehiculo = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idVehiculo);
            ps.executeUpdate();
        }
    }
}