
package grupocho.logisticsac.interfaz;

import grupocho.logisticsac.dao.UsuarioDAO;
import grupocho.logisticsac.enums.Rol;
import grupocho.logisticsac.modelo.Usuario;
import grupocho.logisticsac.service.UsuarioService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DashboardLayout {
    private final Usuario usuario;
    private final BorderPane principal;
    private final VBox contenido;
    private final Label tituloPanel;
    private final VBox menuModulos;
    private Button botonActivo;

    public DashboardLayout(Usuario usuario) {
        this.usuario = usuario;
        this.menuModulos = new VBox(8);
        menuModulos.getStyleClass().add("dashboard-menu-modulos");

        principal = new BorderPane();
        principal.getStyleClass().add("dashboard-fondo");

        VBox sidebar = crearSidebar();
        principal.setLeft(sidebar);

        tituloPanel = new Label("Panel principal");
        tituloPanel.getStyleClass().add("dashboard-titulo");

        contenido = new VBox(20);
        contenido.getStyleClass().add("dashboard-contenido");
        contenido.getChildren().add(tituloPanel);

        principal.setCenter(contenido);
    }



    private VBox crearSidebar() {

        Label logo = new Label("LOGISTIC S.A.C.");
        logo.getStyleClass().add("dashboard-logo");

        Label descripcion = new Label("Sistema de control logístico");
        descripcion.getStyleClass().add("dashboard-sidebar-descripcion");

        Label nombreUsuario = new Label(usuario.getNombreCompleto());
        nombreUsuario.getStyleClass().add("dashboard-usuario");
        nombreUsuario.setWrapText(true);

        Label rol = new Label("Rol: " + usuario.getRol());
        rol.getStyleClass().add("dashboard-rol");

        Button btnInicio = new Button("Panel principal");
        btnInicio.getStyleClass().add("dashboard-opcion");
        btnInicio.setMaxWidth(Double.MAX_VALUE);
        activarOpcion(btnInicio);
        btnInicio.setOnAction(event -> {
            activarOpcion(btnInicio);

            DashboardView dashboard = new DashboardView(usuario);
            dashboard.mostrarContenidoInicial(this);
        });

        Button btnCerrarSesion = new Button("Cerrar sesión");
        btnCerrarSesion.getStyleClass().add("dashboard-cerrar-sesion");
        btnCerrarSesion.setMaxWidth(Double.MAX_VALUE);

        // Opciones del menú
        menuModulos.getChildren().setAll(
                btnInicio,
                crearGrupo(
                        "Operaciones",
                        "Traslados",
                        "Inspección y salida",
                        "Autorización / rechazo",
                        "Recepción"
                ),
                crearGrupo(
                        "Gestión",
                        "Almacenes",
                        "Vehículos",
                        "Conductores",
                        "Productos"
                ),
                crearGrupo(
                        "Administración",
                        "Usuarios",
                        "Tipos de documento",
                        "Documentos"
                ),
                crearGrupo(
                        "Consultas",
                        "Historial de traslados"
                )
        );

        // Scroll exclusivo para las opciones del menú
        ScrollPane scrollMenu = new ScrollPane(menuModulos);
        scrollMenu.setFitToWidth(true);
        scrollMenu.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollMenu.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollMenu.getStyleClass().add("dashboard-scroll-menu");
        scrollMenu.setMinHeight(0);
        scrollMenu.setMaxHeight(Double.MAX_VALUE);

        VBox.setVgrow(scrollMenu, Priority.ALWAYS);

        btnCerrarSesion.setOnAction(event -> {
            UsuarioService usuarioService = new UsuarioService(new UsuarioDAO());
            new LoginView(usuarioService).mostrar((Stage) principal.getScene().getWindow());
        });

        VBox sidebar = new VBox(
                12,
                logo,
                descripcion,
                crearSeparador(),
                nombreUsuario,
                rol,
                crearSeparador(),
                scrollMenu,
                btnCerrarSesion
        );

        sidebar.getStyleClass().add("dashboard-sidebar");
        VBox.setVgrow(scrollMenu, Priority.ALWAYS);

        return sidebar;
    }


    private Region crearSeparador() {
        Region separador = new Region();
        separador.getStyleClass().add("dashboard-separador");
        return separador;
    }

    public void mostrarContenido(String titulo, Node nodo) {
        tituloPanel.setText(titulo);

        contenido.getChildren().clear();
        contenido.getChildren().add(tituloPanel);

        if (nodo != null) {
            contenido.getChildren().add(nodo);
        }
    }

    public void mostrar(Stage stage) {

        Scene scene = new Scene(principal, 1000, 650);

        var cssDashboard = getClass().getResource("/style/dashboard.css");

        scene.getStylesheets().add(cssDashboard.toExternalForm());

        var cssComponentes = getClass().getResource("/style/componentes.css");

        if (cssComponentes != null) {
            scene.getStylesheets().add(cssComponentes.toExternalForm());
        }

        stage.setTitle("Logistic S.A.C.");
        stage.setScene(scene);
        stage.setWidth(1200);
        stage.setHeight(650);
        stage.centerOnScreen();
        stage.show();
    }

    public VBox getContenido() {
        return contenido;
    }


    private VBox crearGrupo(String titulo, String... modulos) {

        Button btnGrupo = new Button(titulo + "  ▾");
        btnGrupo.getStyleClass().add("dashboard-grupo");
        btnGrupo.setMaxWidth(Double.MAX_VALUE);

        VBox opciones = new VBox(4);
        opciones.getStyleClass().add("dashboard-submenu");

        for (String modulo : modulos) {

            if (!moduloDisponible(modulo)) {
                continue;
            }

            Button boton = new Button(modulo);
            boton.getStyleClass().add("dashboard-subopcion");
            boton.setMaxWidth(Double.MAX_VALUE);

            boton.setOnAction(event -> {
                activarOpcion(boton);
                abrirModulo(modulo);
            });

            opciones.getChildren().add(boton);
        }

        opciones.setVisible(false);
        opciones.setManaged(false);

        btnGrupo.setOnAction(event -> {
            boolean expandido = opciones.isVisible();

            opciones.setVisible(!expandido);
            opciones.setManaged(!expandido);

            btnGrupo.setText(titulo + (expandido ? "  ▸" : "  ▾"));
        });

        return new VBox(4, btnGrupo, opciones);
    }

    private boolean moduloDisponible(String modulo) {

        if (usuario.tieneRol(Rol.ADMINISTRADOR)) {
            return true;
        }

        if (usuario.tieneRol(Rol.DESPACHADOR)) {
            return modulo.equals("Traslados") || modulo.equals("Recepción");
        }

        if (usuario.tieneRol(Rol.VIGILANTE)) {
            return modulo.equals("Inspección y salida") || modulo.equals("Autorización / rechazo");
        }

        if (usuario.tieneRol(Rol.JEFE_SEGURIDAD)) {
            return modulo.equals("Autorización / rechazo") || modulo.equals("Historial de traslados");
        }

        return false;
    }

    private void abrirModulo(String modulo) {

        Stage stage = (Stage) principal.getScene().getWindow();

        switch (modulo) {
            case "Traslados" -> new TrasladoView(usuario,this).mostrar((Stage) principal.getScene().getWindow());

            case "Historial de traslados" -> new HistorialTrasladoView(usuario, this).mostrar(stage);

            case "Inspección y salida" -> new InspeccionView(usuario, this).mostrar(stage);

            case "Autorización / rechazo" -> new AutorizacionView(usuario, this).mostrar(stage);

            case "Recepción" -> new RecepcionView(usuario, this).mostrar(stage);

            case "Productos" -> new ProductoView(usuario, this).mostrar(stage);

            case "Almacenes" -> new AlmacenView(usuario, this).mostrar(stage);

            case "Vehículos" -> new VehiculoView(usuario, this).mostrar(stage);

            case "Conductores" -> new ConductorView(usuario, this).mostrar(stage);

            case "Usuarios" -> new UsuarioView(usuario, this).mostrar(stage);

            case "Tipos de documento" -> new TipoDocumentoView(usuario, this).mostrar(stage);

            case "Documentos" -> new DocumentoView(usuario, this).mostrar(stage);
        }
    }

    private void activarOpcion(Button boton) {

        if (botonActivo != null) {
            botonActivo.getStyleClass().remove("dashboard-opcion-activa");
        }

        for (Node nodo : menuModulos.lookupAll(".dashboard-subopcion")) {
            nodo.getStyleClass().remove("dashboard-opcion-activa");
        }

        botonActivo = boton;

        if (!botonActivo.getStyleClass().contains("dashboard-opcion-activa")) {
            botonActivo.getStyleClass().add("dashboard-opcion-activa");
        }
    }

}
