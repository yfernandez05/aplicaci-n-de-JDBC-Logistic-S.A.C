package grupocho.logisticsac.repository;

import grupocho.logisticsac.modelo.Usuario;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface UsuarioRepository {
    void insertar(Connection conexion, Usuario usuario) throws SQLException;
    Usuario buscarPorUsername(Connection conexion, String username) throws SQLException;
    boolean autenticar(Connection conexion, String username, String password) throws SQLException;
    List<Usuario> listar(Connection conexion) throws SQLException;
}