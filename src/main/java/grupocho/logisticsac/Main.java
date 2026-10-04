package grupocho.logisticsac;

import grupocho.logisticsac.dao.UsuarioDAO;
import grupocho.logisticsac.interfaz.LoginView;
import grupocho.logisticsac.repository.UsuarioRepository;
import grupocho.logisticsac.service.UsuarioService;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        UsuarioRepository usuarioRepository = new UsuarioDAO();
        UsuarioService usuarioService = new UsuarioService(usuarioRepository);
        LoginView loginView = new LoginView(usuarioService);
        loginView.mostrar(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}