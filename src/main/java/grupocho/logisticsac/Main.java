package grupocho.logisticsac;

import grupocho.logisticsac.config.ConexionDB;
import grupocho.logisticsac.enums.AmbitoDocumento;
import grupocho.logisticsac.enums.Rol;
import grupocho.logisticsac.modelo.Almacen;
import grupocho.logisticsac.modelo.TipoDocumento;
import grupocho.logisticsac.modelo.Usuario;
import grupocho.logisticsac.service.AlmacenService;
import grupocho.logisticsac.service.TipoDocumentoService;
import grupocho.logisticsac.service.UsuarioService;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {

        TipoDocumentoService tipoDocumentoService = new TipoDocumentoService();

        TipoDocumento tipoDocumento = new TipoDocumento(
                "Guía de Remisión",
                AmbitoDocumento.TRASLADO,
                true
        );

        try {
            tipoDocumentoService.registrar(tipoDocumento);

            System.out.println("Tipo de documento registrado correctamente.");

            TipoDocumento encontrado = tipoDocumentoService.buscarPorNombre("Guía de Remisión");

            if (encontrado != null) {
                System.out.println("Tipo de documento encontrado:");
                System.out.println("ID: " + encontrado.getIdTipoDocumento());
                System.out.println("Nombre: " + encontrado.getNombre());
                System.out.println("Ámbito: " + encontrado.getAmbito());
                System.out.println("Obligatorio: " + encontrado.isObligatorio());
                System.out.println("Activo: " + encontrado.isActivo());
            } else {
                System.out.println("Tipo de documento no encontrado.");
            }

        } catch (IllegalArgumentException e) {
            System.out.println("Error de validación: " + e.getMessage());

        } catch (SQLException e) {
            System.out.println("Error de base de datos: " + e.getMessage());
        }


    }
}