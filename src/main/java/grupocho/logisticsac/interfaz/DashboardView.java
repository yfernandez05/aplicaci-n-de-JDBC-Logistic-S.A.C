
package grupocho.logisticsac.interfaz;

import grupocho.logisticsac.dao.AlmacenDAO;
import grupocho.logisticsac.dao.ConductorDAO;
import grupocho.logisticsac.dao.DocumentoDAO;
import grupocho.logisticsac.dao.TrasladoDAO;
import grupocho.logisticsac.dao.VehiculoDAO;
import grupocho.logisticsac.enums.Rol;
import grupocho.logisticsac.modelo.Usuario;
import grupocho.logisticsac.service.AlmacenService;
import grupocho.logisticsac.service.ConductorService;
import grupocho.logisticsac.service.TrasladoService;
import grupocho.logisticsac.service.VehiculoService;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

public class DashboardView {

    private final Usuario usuario;

    public DashboardView(Usuario usuario) {
        this.usuario = usuario;
    }

    public void mostrar(Stage stage) {
        DashboardLayout layout = new DashboardLayout(usuario);
        mostrarContenidoInicial(layout);
        layout.mostrar(stage);
    }

    public void mostrarContenidoInicial(DashboardLayout layout) {

        Label bienvenida = new Label(
                "Hola, " + usuario.getNombreCompleto()
        );
        bienvenida.getStyleClass().add("dashboard-bienvenida");
        bienvenida.setWrapText(true);

        Label descripcion = new Label(
                "Sistema de Control de Salida de Vehículos "
                        + "en los Traslados entre Almacenes"
        );
        descripcion.getStyleClass().add("dashboard-descripcion");
        descripcion.setWrapText(true);

        VBox contenido;

        if (usuario.tieneRol(Rol.ADMINISTRADOR)) {

            Label titulo = new Label("Resumen ejecutivo");
            titulo.getStyleClass().add("inicio-titulo-panel");

            Label mensaje = new Label(
                    "Panel de indicadores generales del sistema logístico. "
                            + "Permite consultar la cantidad de conductores, "
                            + "vehículos, almacenes y traslados registrados "
                            + "para facilitar el seguimiento y control "
                            + "de las operaciones."
            );
            mensaje.getStyleClass().add("inicio-descripcion-panel");
            mensaje.setWrapText(true);

            Label rol = new Label("Perfil de acceso: " + obtenerNombreRol());
            rol.getStyleClass().add("inicio-rol");

            VBox bienvenidaCard = new VBox(
                    12, titulo, mensaje, rol
            );
            bienvenidaCard.getStyleClass().add("inicio-card");
            bienvenidaCard.setPadding(new Insets(22));

            String totalConductores = obtenerTotalConductores();
            String totalVehiculos = obtenerTotalVehiculos();
            String totalAlmacenes = obtenerTotalAlmacenes();
            String totalTraslados = obtenerTotalTraslados();

            GridPane indicadores = new GridPane();
            indicadores.getStyleClass().add("inicio-indicadores");
            indicadores.setHgap(14);
            indicadores.setVgap(14);

            agregarIndicador(
                    indicadores,
                    "Conductores",
                    totalConductores,
                    "Conductores registrados",
                    0, 0
            );

            agregarIndicador(
                    indicadores,
                    "Vehículos",
                    totalVehiculos,
                    "Vehículos activos registrados",
                    1, 0
            );

            agregarIndicador(
                    indicadores,
                    "Almacenes",
                    totalAlmacenes,
                    "Almacenes registrados",
                    2, 0
            );

            agregarIndicador(
                    indicadores,
                    "Traslados",
                    totalTraslados,
                    "Operaciones registradas",
                    3, 0
            );

            contenido = new VBox(
                    18,
                    bienvenida,
                    descripcion,
                    bienvenidaCard,
                    indicadores
            );

        } else {

            Label titulo = new Label("Bienvenido al sistema");
            titulo.getStyleClass().add("inicio-titulo-panel");

            Label mensaje = new Label(
                    "Desde el menú lateral puedes acceder a las "
                            + "funciones habilitadas para tu perfil "
                            + "y realizar las operaciones correspondientes "
                            + "al control logístico."
            );
            mensaje.getStyleClass().add("inicio-descripcion-panel");
            mensaje.setWrapText(true);

            VBox tarjeta = new VBox(
                    12, titulo, mensaje
            );
            tarjeta.getStyleClass().add("inicio-card");
            tarjeta.setPadding(new Insets(22));

            contenido = new VBox(
                    18,
                    bienvenida,
                    descripcion,
                    tarjeta
            );
        }

        contenido.getStyleClass().add("inicio-contenido");
        contenido.setPadding(new Insets(10));
        contenido.setFillWidth(true);

        layout.mostrarContenido("Panel principal", contenido);
    }


    private String obtenerTotalConductores() {

        try {
            ConductorService servicio =
                    new ConductorService(new ConductorDAO());

            return String.valueOf(servicio.listar().size());

        } catch (SQLException e) {
            e.printStackTrace();
            return "N/D";
        }
    }

    private String obtenerTotalVehiculos() {

        try {
            VehiculoService servicio =
                    new VehiculoService(new VehiculoDAO());

            return String.valueOf(servicio.listar().size());

        } catch (SQLException e) {
            e.printStackTrace();
            return "N/D";
        }
    }

    private String obtenerTotalAlmacenes() {

        try {
            AlmacenService servicio =
                    new AlmacenService(new AlmacenDAO());

            return String.valueOf(servicio.listar().size());

        } catch (SQLException e) {
            e.printStackTrace();
            return "N/D";
        }
    }

    private String obtenerTotalTraslados() {

        try {
            TrasladoService servicio = new TrasladoService(
                    new TrasladoDAO(),
                    new DocumentoDAO()
            );

            return String.valueOf(servicio.listar().size());

        } catch (SQLException e) {
            e.printStackTrace();
            return "N/D";
        }
    }

    private void agregarIndicador(
            GridPane grid,
            String titulo,
            String cantidad,
            String descripcion,
            int columna,
            int fila
    ) {

        Label nombre = new Label(titulo);
        nombre.getStyleClass().add("inicio-indicador-titulo");

        Label numero = new Label(cantidad);
        numero.getStyleClass().add("inicio-indicador-numero");

        Label detalle = new Label(descripcion);
        detalle.getStyleClass().add("inicio-indicador-descripcion");
        detalle.setWrapText(true);

        VBox card = new VBox(
                8, nombre, numero, detalle
        );
        card.getStyleClass().add("inicio-indicador-card");
        card.setPadding(new Insets(18));
        card.setMinHeight(120);
        card.setMaxWidth(Double.MAX_VALUE);

        grid.add(card, columna, fila);
        GridPane.setHgrow(card, Priority.ALWAYS);
    }

    private String obtenerNombreRol() {

        if (usuario.tieneRol(Rol.ADMINISTRADOR)) {
            return "Administrador";
        }

        if (usuario.tieneRol(Rol.DESPACHADOR)) {
            return "Despachador";
        }

        if (usuario.tieneRol(Rol.VIGILANTE)) {
            return "Vigilante";
        }

        if (usuario.tieneRol(Rol.JEFE_SEGURIDAD)) {
            return "Jefe de seguridad";
        }

        return usuario.getRol().toString();
    }
}
