package grupocho.logisticsac;

import grupocho.logisticsac.config.ConexionDB;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {

        try (Connection conexion = ConexionDB.obtenerConexion()) {

            System.out.println("=================================");
            System.out.println("CONEXIÓN EXITOSA");
            System.out.println("Base de datos: logistic_sac");
            System.out.println("=================================");

        } catch (Exception e) {

            System.out.println("=================================");
            System.out.println("ERROR DE CONEXIÓN");
            System.out.println("=================================");
            e.printStackTrace();
        }
    }
}