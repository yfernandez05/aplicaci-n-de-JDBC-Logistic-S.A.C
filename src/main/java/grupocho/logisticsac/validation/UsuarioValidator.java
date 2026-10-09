
package grupocho.logisticsac.validation;

import grupocho.logisticsac.modelo.Usuario;

public class UsuarioValidator {

    public void validar(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }

        if (usuario.getUsername() == null || usuario.getUsername().isBlank()) {
            throw new IllegalArgumentException("El username es obligatorio.");
        }

        if (usuario.getPasswordHash() == null || usuario.getPasswordHash().isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }
    }

    public void validarCredenciales(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("El username es obligatorio.");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }
    }
}
