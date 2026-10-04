package grupocho.logisticsac.interfaz;

import grupocho.logisticsac.modelo.Usuario;
import grupocho.logisticsac.service.UsuarioService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;

public class LoginView {
    private final UsuarioService usuarioService;

    public LoginView(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public void mostrar(Stage stage) {
        Label titulo = new Label("LOGISTIC S.A.C.");
        Label subtitulo = new Label("Inicio de sesión");

        TextField txtUsuario = new TextField();
        txtUsuario.setPromptText("Usuario");

        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Contraseña");

        Button btnIngresar = new Button("Iniciar sesión");
        Label mensaje = new Label();

        btnIngresar.setOnAction(event -> {
            String username = txtUsuario.getText();
            String password = txtPassword.getText();

            try {
                boolean autenticado = usuarioService.autenticar(username, password);

                if (autenticado) {
                    Usuario usuario = usuarioService.buscarPorUsername(username);
                    DashboardView dashboardView = new DashboardView(usuario);
                    dashboardView.mostrar(stage);
                } else {
                    mensaje.setText("Usuario o contraseña incorrectos.");
                }
            } catch (IllegalArgumentException e) {
                mensaje.setText(e.getMessage());
            } catch (SQLException e) {
                mensaje.setText("Error al consultar la base de datos.");
            }
        });

        VBox layout = new VBox(15, titulo, subtitulo, txtUsuario, txtPassword, btnIngresar, mensaje);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));

        Scene scene = new Scene(layout, 400, 400);
        stage.setTitle("Logistic S.A.C. - Login");
        stage.setScene(scene);
        stage.show();
    }
}