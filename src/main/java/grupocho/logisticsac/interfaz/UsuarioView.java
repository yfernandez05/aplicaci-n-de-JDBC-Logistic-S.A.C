package grupocho.logisticsac.interfaz;

import grupocho.logisticsac.dao.UsuarioDAO;
import grupocho.logisticsac.modelo.Usuario;
import grupocho.logisticsac.repository.UsuarioRepository;
import grupocho.logisticsac.service.UsuarioService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

public class UsuarioView {

    private final Usuario usuarioActual;
    private final UsuarioService usuarioService;
    private final DashboardLayout dashboardLayout;

    public UsuarioView(Usuario usuarioActual) {
        this(usuarioActual, null);
    }

    public UsuarioView(Usuario usuarioActual, DashboardLayout dashboardLayout) {
        this.usuarioActual = usuarioActual;
        this.dashboardLayout = dashboardLayout;

        UsuarioRepository repository = new UsuarioDAO();
        this.usuarioService = new UsuarioService(repository);
    }

    public void mostrar(Stage stage) {

        TableView<Usuario> tabla = new TableView<>();

        TableColumn<Usuario, Integer> id = new TableColumn<>("ID");
        id.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(
                        data.getValue().getIdUsuario()
                ));

        TableColumn<Usuario, String> username = new TableColumn<>("Usuario");
        username.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getUsername()));

        TableColumn<Usuario, String> nombre = new TableColumn<>("Nombre completo");
        nombre.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombreCompleto()));

        TableColumn<Usuario, String> rol = new TableColumn<>("Rol");
        rol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getRol().name()));

        TableColumn<Usuario, String> estado = new TableColumn<>("Estado");
        estado.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().isActivo() ? "Activo" : "Inactivo"
                ));

        tabla.getColumns().addAll(id, username, nombre, rol, estado);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Label mensaje = new Label();
        mensaje.getStyleClass().add("usuario-mensaje");

        try {
            tabla.setItems(
                    FXCollections.observableArrayList(usuarioService.listar())
            );
        } catch (SQLException e) {
            mensaje.setText("Error al cargar los usuarios.");
        }

        Button btnVolver = new Button("Volver");
        btnVolver.getStyleClass().add("usuario-boton-secundario");

        btnVolver.setOnAction(event -> {
            if (dashboardLayout != null) {
                dashboardLayout.mostrarContenido("Panel principal", null);
            } else {
                new DashboardView(usuarioActual).mostrar(stage);
            }
        });

        VBox contenido = new VBox(15, tabla, mensaje, btnVolver);
        tabla.getStyleClass().add("usuario-tabla");
        contenido.setPadding(new Insets(20));
        contenido.getStyleClass().add("usuario-contenedor");

        if (dashboardLayout != null) {
            Scene scene = stage.getScene();
            var css = getClass().getResource("/style/usuario.css");

            if (css != null && !scene.getStylesheets().contains(css.toExternalForm())) {
                scene.getStylesheets().add(css.toExternalForm());
            }

            dashboardLayout.mostrarContenido("Usuarios", contenido);

        } else {
            Scene scene = new Scene(contenido, 750, 500);

            var css = getClass().getResource("/style/usuario.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }

            stage.setTitle("Logistic S.A.C. - Usuarios");
            stage.setScene(scene);
            stage.show();
        }
    }
}