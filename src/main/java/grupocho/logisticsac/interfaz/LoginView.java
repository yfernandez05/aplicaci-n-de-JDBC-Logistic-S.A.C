
package grupocho.logisticsac.interfaz;

import grupocho.logisticsac.modelo.Usuario;
import grupocho.logisticsac.service.UsuarioService;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

public class LoginView {

    private final UsuarioService usuarioService;

    public LoginView(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public void mostrar(Stage stage) {

        // Títulos
        Label titulo = new Label("LOGISTIC S.A.C.");
        titulo.getStyleClass().add("login-titulo");

        Label subtitulo = new Label("Inicio de sesión");
        subtitulo.getStyleClass().add("login-subtitulo");

        Label descripcion = new Label("Ingresa tus credenciales para continuar.");
        descripcion.getStyleClass().add("login-descripcion");

        // Campos
        Label lblUsuario = new Label("Usuario");
        lblUsuario.getStyleClass().add("login-etiqueta");

        TextField txtUsuario = new TextField();
        txtUsuario.setPromptText("Ingresa tu usuario");
        txtUsuario.getStyleClass().add("login-campo");
        txtUsuario.setMaxWidth(Double.MAX_VALUE);

        Label lblPassword = new Label("Contraseña");
        lblPassword.getStyleClass().add("login-etiqueta");

        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Ingresa tu contraseña");
        txtPassword.getStyleClass().add("login-campo");
        txtPassword.setMaxWidth(Double.MAX_VALUE);

        // Mensaje
        Label mensaje = new Label();
        mensaje.getStyleClass().add("login-mensaje");
        mensaje.setWrapText(true);
        mensaje.setMaxWidth(Double.MAX_VALUE);
        mensaje.setVisible(false);
        mensaje.setManaged(false);

        // Botón
        Button btnIngresar = new Button("Iniciar sesión");
        btnIngresar.getStyleClass().add("login-boton");
        btnIngresar.setMaxWidth(Double.MAX_VALUE);
        btnIngresar.setDefaultButton(true);

        // Autenticación
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
                    mostrarMensaje( mensaje, "Usuario o contraseña incorrectos.");
                }

            } catch (IllegalArgumentException e) {
                mostrarMensaje(mensaje, e.getMessage());
            } catch (SQLException e) {
                mostrarMensaje( mensaje, "Error al consultar la base de datos.");
                e.printStackTrace();
            }
        });

        // Formulario
        VBox layout = new VBox(
                10,
                titulo,
                subtitulo,
                descripcion,
                lblUsuario,
                txtUsuario,
                lblPassword,
                txtPassword,
                btnIngresar,
                mensaje
        );

        layout.setAlignment(Pos.CENTER_LEFT);
        layout.getStyleClass().add("login-tarjeta");
        layout.setFillWidth(true);

        // Fondo
        StackPane fondo = new StackPane(layout);
        fondo.getStyleClass().add("login-fondo");

        // Escena y CSS
        Scene scene = new Scene(fondo, 900, 600);

        var recursoCss = getClass().getResource("/style/login.css");

        if (recursoCss == null) {
            throw new IllegalStateException("No se encontró el archivo /style/login.css");
        }

        scene.getStylesheets().add(recursoCss.toExternalForm());

        // Ventana
        stage.setTitle("Logistic S.A.C. | Inicio de sesión");
        stage.setMinWidth(700);
        stage.setMinHeight(500);
        stage.setScene(scene);
        stage.show();

        txtUsuario.requestFocus();
    }

    private void mostrarMensaje(Label mensaje, String texto) {
        mensaje.setText(texto);
        mensaje.setVisible(true);
        mensaje.setManaged(true);
    }
}
