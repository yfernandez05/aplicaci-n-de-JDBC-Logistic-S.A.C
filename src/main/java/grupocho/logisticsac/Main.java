package grupocho.logisticsac;

import grupocho.logisticsac.config.ConexionDB;
import grupocho.logisticsac.enums.Rol;
import grupocho.logisticsac.modelo.Usuario;
import grupocho.logisticsac.service.UsuarioService;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {

        UsuarioService usuarioService = new UsuarioService();

        Usuario usuario = new Usuario(
                "admin",
                "123456",
                "Administrador del sistema",
                Rol.ADMINISTRADOR
        );

        try {
            usuarioService.registrar(usuario);
            System.out.println("Usuario registrado correctamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error de validación: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}