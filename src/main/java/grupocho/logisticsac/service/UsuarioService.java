package grupocho.logisticsac.service;

import grupocho.logisticsac.config.ConexionDB;
import grupocho.logisticsac.dao.UsuarioDAO;
import grupocho.logisticsac.modelo.Usuario;
import grupocho.logisticsac.repository.UsuarioRepository;
import grupocho.logisticsac.validation.UsuarioValidator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioValidator usuarioValidator;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioValidator = new UsuarioValidator();
    }

    public void registrar(Usuario usuario) throws SQLException {

        usuarioValidator.validar(usuario);
        Connection conexion = null;

        try {
            conexion = ConexionDB.obtenerConexion();

            // Iniciamos la transacción
            conexion.setAutoCommit(false);

            // Insert Base de datos
            usuarioRepository.insertar(conexion, usuario);

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
            return usuarioRepository.buscarPorUsername(conexion, username);
        }
    }

    public boolean autenticar(String username, String password) throws SQLException {
        usuarioValidator.validarCredenciales(username, password);
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            return usuarioRepository.autenticar(conexion, username, password);
        }
    }

    public List<Usuario> listar() throws SQLException {
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            return usuarioRepository.listar(conexion);
        }
    }
}