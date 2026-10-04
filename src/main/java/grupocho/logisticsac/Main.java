package grupocho.logisticsac;

import grupocho.logisticsac.config.ConexionDB;
import grupocho.logisticsac.enums.Rol;
import grupocho.logisticsac.modelo.Almacen;
import grupocho.logisticsac.modelo.Usuario;
import grupocho.logisticsac.service.AlmacenService;
import grupocho.logisticsac.service.UsuarioService;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {

        Almacen almacen = new Almacen(
                "ALM001",
                "Almacén Principal",
                "Av. Lima - Lima"
        );

        AlmacenService almacenService = new AlmacenService();

        try {

            almacenService.registrar(almacen);
            System.out.println("Almacén registrado correctamente.");
            Almacen almacenEncontrado = almacenService.buscarPorCodigo("ALM001");

            if (almacenEncontrado != null) {
                System.out.println("Almacen encontrado:");
                System.out.println("ID: " + almacenEncontrado.getIdAlmacen());
                System.out.println("Código: " + almacenEncontrado.getCodigo());
                System.out.println("Nombre: " + almacenEncontrado.getNombre());
                System.out.println("Dirección: " + almacenEncontrado.getDireccion());
                System.out.println("Activo: " + almacenEncontrado.isActivo());

            } else {

                System.out.println("Almacén no encontrado.");
            }

        } catch (IllegalArgumentException e) {

            System.out.println("Error de validación: " + e.getMessage());

        } catch (Exception e) {

            System.out.println("Error: " + e.getMessage());
        }

    }
}