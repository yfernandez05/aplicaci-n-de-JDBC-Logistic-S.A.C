package grupocho.logisticsac.dao;

import grupocho.logisticsac.config.ConexionDB;
import grupocho.logisticsac.enums.Rol;
import grupocho.logisticsac.modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public void insertar(Connection conn, Usuario usuario) throws SQLException {

        String sql = """
            INSERT INTO usuario (username, password_hash, nombre_completo, rol, activo) VALUES (?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario.getUsername());
            ps.setString(2, usuario.getPasswordHash());
            ps.setString(3, usuario.getNombreCompleto());
            ps.setString(4, usuario.getRol().name());
            ps.setBoolean(5, usuario.isActivo());

            ps.executeUpdate();
        }
    }

    public Usuario buscarPorUsername(Connection conn, String username) throws SQLException {

        String sql = """
            SELECT id_usuario, username, password_hash, nombre_completo, rol, activo
            FROM usuario WHERE username = ?
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("nombre_completo"),
                        Rol.valueOf(rs.getString("rol")),
                        rs.getBoolean("activo")
                    );
                }
            }
        }

        return null;
    }
}