package grupocho.logisticsac.service;

import grupocho.logisticsac.config.ConexionDB;
import grupocho.logisticsac.dao.UsuarioDAO;
import grupocho.logisticsac.modelo.Usuario;

import java.sql.Connection;
import java.sql.SQLException;

public class UsuarioService {

    private final UsuarioDAO usuarioDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public void registrar(Usuario usuario) throws SQLException {

        if (usuario == null || !usuario.validar()) {
            throw new IllegalArgumentException("Los datos del usuario no son válidos.");
        }

        Connection conexion = null;

        try {
            conexion = ConexionDB.obtenerConexion();

            // Iniciamos la transacción
            conexion.setAutoCommit(false);

            // Insert Base de datos
            usuarioDAO.insertar(conexion, usuario);

            // confirmaos commit
            conexion.commit();

        } catch (SQLException e) {
            // rollback
            if (conexion != null) {conexion.rollback(); }
            throw e;

        } finally {
            // Cerrarmos conexion y restauramos
            if (conexion != null) {
                conexion.setAutoCommit(true);
                conexion.close();
            }
        }
    }

    public Usuario buscarPorUsername(String username) throws SQLException {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException( "El username es obligatorio." );
        }
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            return usuarioDAO.buscarPorUsername(conexion, username);
        }
    }
}