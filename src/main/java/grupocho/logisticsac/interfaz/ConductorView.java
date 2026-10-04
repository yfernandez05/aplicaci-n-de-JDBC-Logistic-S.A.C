package grupocho.logisticsac.interfaz;

import grupocho.logisticsac.dao.ConductorDAO;
import grupocho.logisticsac.modelo.Conductor;
import grupocho.logisticsac.modelo.Usuario;
import grupocho.logisticsac.repository.ConductorRepository;
import grupocho.logisticsac.service.ConductorService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;

public class ConductorView {
    private final Usuario usuario;
    private final ConductorService conductorService;

    public ConductorView(Usuario usuario) {
        this.usuario = usuario;
        ConductorRepository repository = new ConductorDAO();
        this.conductorService = new ConductorService(repository);
    }

    public void mostrar(Stage stage) {
        Label titulo = new Label("CONDUCTORES");

        TextField txtDni = new TextField();
        txtDni.setPromptText("DNI");

        TextField txtNombres = new TextField();
        txtNombres.setPromptText("Nombres");

        TextField txtLicencia = new TextField();
        txtLicencia.setPromptText("Número de licencia");

        TextField txtCategoria = new TextField();
        txtCategoria.setPromptText("Categoría de licencia");

        Button btnRegistrar = new Button("Registrar");
        Button btnActualizar = new Button("Actualizar");
        Button btnEliminar = new Button("Eliminar");
        Button btnVolver = new Button("Volver");

        TableView<Conductor> tabla = new TableView<>();

        tabla.setOnMouseClicked(event -> {
            Conductor seleccionado = tabla.getSelectionModel().getSelectedItem();

            if (seleccionado != null) {
                txtDni.setText(seleccionado.getDni());
                txtNombres.setText(seleccionado.getNombres());
                txtLicencia.setText(seleccionado.getNumeroLicencia());
                txtCategoria.setText(seleccionado.getCategoriaLicencia());
            }
        });

        TableColumn<Conductor, String> dni = new TableColumn<>("DNI");
        dni.setCellValueFactory(new PropertyValueFactory<>("dni"));

        TableColumn<Conductor, String> nombres = new TableColumn<>("Nombres");
        nombres.setCellValueFactory(new PropertyValueFactory<>("nombres"));

        TableColumn<Conductor, String> licencia = new TableColumn<>("Licencia");
        licencia.setCellValueFactory(new PropertyValueFactory<>("numeroLicencia"));

        TableColumn<Conductor, String> categoria = new TableColumn<>("Categoría");
        categoria.setCellValueFactory(new PropertyValueFactory<>("categoriaLicencia"));

        tabla.getColumns().addAll(dni, nombres, licencia, categoria);

        Label mensaje = new Label();
        cargar(tabla, mensaje);

        btnRegistrar.setOnAction(event -> {
            try {
                Conductor conductor = new Conductor(
                        txtDni.getText(),
                        txtNombres.getText(),
                        txtLicencia.getText(),
                        txtCategoria.getText()
                );

                conductorService.registrar(conductor);
                cargar(tabla, mensaje);

                txtDni.clear();
                txtNombres.clear();
                txtLicencia.clear();
                txtCategoria.clear();

                mensaje.setText("Conductor registrado correctamente.");
            } catch (IllegalArgumentException e) {
                mensaje.setText(e.getMessage());
            } catch (SQLException e) {
                mensaje.setText("Error al registrar conductor.");
            }
        });

        btnActualizar.setOnAction(event -> {
            try {
                Conductor seleccionado = tabla.getSelectionModel().getSelectedItem();

                if (seleccionado == null) {
                    mensaje.setText("Seleccione un conductor.");
                    return;
                }

                seleccionado.setDni(txtDni.getText());
                seleccionado.setNombres(txtNombres.getText());
                seleccionado.setNumeroLicencia(txtLicencia.getText());
                seleccionado.setCategoriaLicencia(txtCategoria.getText());

                conductorService.actualizar(seleccionado);
                cargar(tabla, mensaje);
                mensaje.setText("Conductor actualizado correctamente.");

            } catch (IllegalArgumentException e) {
                mensaje.setText(e.getMessage());
            } catch (SQLException e) {
                mensaje.setText("Error al actualizar conductor.");
            }
        });

        btnEliminar.setOnAction(event -> {
            try {
                Conductor seleccionado = tabla.getSelectionModel().getSelectedItem();

                if (seleccionado == null) {
                    mensaje.setText("Seleccione un conductor.");
                    return;
                }

                conductorService.eliminar(seleccionado.getIdConductor());
                cargar(tabla, mensaje);

                txtDni.clear();
                txtNombres.clear();
                txtLicencia.clear();
                txtCategoria.clear();

                mensaje.setText("Conductor eliminado correctamente.");

            } catch (SQLException e) {
                mensaje.setText("Error al eliminar conductor.");
            }
        });

        btnVolver.setOnAction(event -> new DashboardView(usuario).mostrar(stage));

        VBox layout = new VBox(
                10,
                titulo,
                txtDni,
                txtNombres,
                txtLicencia,
                txtCategoria,
                btnRegistrar,
                btnActualizar,
                btnEliminar,
                tabla,
                mensaje,
                btnVolver
        );

        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 750, 550);
        stage.setTitle("Logistic S.A.C. - Conductores");
        stage.setScene(scene);
        stage.show();
    }

    private void cargar(TableView<Conductor> tabla, Label mensaje) {
        try {
            tabla.setItems(FXCollections.observableArrayList(conductorService.listar()));
        } catch (SQLException e) {
            mensaje.setText("Error al cargar conductores.");
        }
    }
}