package grupocho.logisticsac.interfaz;

import grupocho.logisticsac.dao.UsuarioDAO;
import grupocho.logisticsac.modelo.Usuario;
import grupocho.logisticsac.repository.UsuarioRepository;
import grupocho.logisticsac.service.UsuarioService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

public class UsuarioView {
    private final Usuario usuarioActual;
    private final UsuarioService usuarioService;

    public UsuarioView(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        UsuarioRepository repository = new UsuarioDAO();
        this.usuarioService = new UsuarioService(repository);
    }

    public void mostrar(Stage stage) {
        Label titulo = new Label("USUARIOS");

        TableView<Usuario> tabla = new TableView<>();

        TableColumn<Usuario, Integer> id = new TableColumn<>("ID");
        id.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getIdUsuario()));

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

        try {
            tabla.setItems(
                    FXCollections.observableArrayList(usuarioService.listar())
            );
        } catch (SQLException e) {
            mensaje.setText("Error al cargar los usuarios.");
        }

        Button btnVolver = new Button("Volver");
        btnVolver.setOnAction(event -> {
            DashboardView dashboardView = new DashboardView(usuarioActual);
            dashboardView.mostrar(stage);
        });

        VBox layout = new VBox(15, titulo, tabla, mensaje, btnVolver);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 750, 500);

        stage.setTitle("Logistic S.A.C. - Usuarios");
        stage.setScene(scene);
        stage.show();
    }
}