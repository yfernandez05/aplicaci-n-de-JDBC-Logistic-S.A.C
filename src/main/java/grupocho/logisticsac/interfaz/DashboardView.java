package grupocho.logisticsac.interfaz;

import grupocho.logisticsac.enums.Rol;
import grupocho.logisticsac.modelo.Usuario;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DashboardView {
    private final Usuario usuario;

    public DashboardView(Usuario usuario) {
        this.usuario = usuario;
    }

    public void mostrar(Stage stage) {
        Label titulo = new Label("LOGISTIC S.A.C.");
        Label bienvenida = new Label("Bienvenido, " + usuario.getNombreCompleto());
        Label rol = new Label("Rol: " + usuario.getRol());

        GridPane opciones = new GridPane();
        opciones.setHgap(15);
        opciones.setVgap(12);
        opciones.setAlignment(Pos.CENTER);

        crearOpciones(opciones);

        Button btnCerrarSesion = new Button("Cerrar sesión");
        btnCerrarSesion.setPrefWidth(220);

        btnCerrarSesion.setOnAction(event -> stage.close());

        VBox layout = new VBox(15, titulo, bienvenida, rol, opciones, btnCerrarSesion);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));

        Scene scene = new Scene(layout, 650, 550);

        stage.setTitle("Logistic S.A.C. - Panel principal");
        stage.setScene(scene);
        stage.show();
        stage.centerOnScreen();
    }

    private void crearOpciones(GridPane opciones) {
        int fila = 0;
        int columna = 0;

        if (usuario.tieneRol(Rol.ADMINISTRADOR)) {
            agregarBoton(opciones, "Usuarios", columna++, fila);
            agregarBoton(opciones, "Almacenes", columna++, fila++);
            columna = 0;

            agregarBoton(opciones, "Vehículos", columna++, fila);
            agregarBoton(opciones, "Conductores", columna++, fila++);
            columna = 0;

            agregarBoton(opciones, "Productos", columna++, fila);
            agregarBoton(opciones, "Traslados", columna++, fila++);
            columna = 0;

            agregarBoton(opciones, "Historial de traslados", columna++, fila);
        }

        if (usuario.tieneRol(Rol.DESPACHADOR)) {
            agregarBoton(opciones, "Traslados", columna++, fila);
            agregarBoton(opciones, "Recepción", columna++, fila);
        }

        if (usuario.tieneRol(Rol.VIGILANTE)) {
            agregarBoton(opciones, "Inspección y salida", columna++, fila);
        }

        if (usuario.tieneRol(Rol.JEFE_SEGURIDAD)) {
            agregarBoton(opciones, "Autorización / rechazo", columna++, fila);
        }
    }

    private void agregarBoton(GridPane opciones, String texto, int columna, int fila) {
        Button boton = new Button(texto);
        boton.setPrefWidth(220);
        boton.setPrefHeight(35);

        if (texto.equals("Traslados")) {
            boton.setOnAction(event ->
                    new TrasladoView(usuario).mostrar((Stage) boton.getScene().getWindow()));
        }

        if (texto.equals("Historial de traslados")) {
            boton.setOnAction(event ->
                    new HistorialTrasladoView(usuario).mostrar((Stage) boton.getScene().getWindow()));
        }

        if (texto.equals("Inspección y salida")) {
            boton.setOnAction(event ->
                    new InspeccionView(usuario).mostrar((Stage) boton.getScene().getWindow()));
        }

        if (texto.equals("Autorización / rechazo")) {
            boton.setOnAction(event ->
                    new AutorizacionView(usuario).mostrar((Stage) boton.getScene().getWindow()));
        }

        if (texto.equals("Recepción")) {
            boton.setOnAction(event ->
                    new RecepcionView(usuario).mostrar((Stage) boton.getScene().getWindow()));
        }

        if (texto.equals("Productos")) {
            boton.setOnAction(event ->
                    new ProductoView(usuario).mostrar((Stage) boton.getScene().getWindow()));
        }

        if (texto.equals("Almacenes")) {
            boton.setOnAction(event ->
                    new AlmacenView(usuario).mostrar((Stage) boton.getScene().getWindow()));
        }

        if (texto.equals("Vehículos")) {
            boton.setOnAction(event ->
                    new VehiculoView(usuario).mostrar((Stage) boton.getScene().getWindow()));
        }

        if (texto.equals("Conductores")) {
            boton.setOnAction(event ->
                    new ConductorView(usuario).mostrar((Stage) boton.getScene().getWindow()));
        }

        if (texto.equals("Usuarios")) {
            boton.setOnAction(event ->
                    new UsuarioView(usuario).mostrar((Stage) boton.getScene().getWindow()));
        }

        opciones.add(boton, columna, fila);
    }
}