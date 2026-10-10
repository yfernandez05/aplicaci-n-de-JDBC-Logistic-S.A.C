
package grupocho.logisticsac.interfaz;

import grupocho.logisticsac.dao.ConductorDAO;
import grupocho.logisticsac.modelo.Conductor;
import grupocho.logisticsac.modelo.Usuario;
import grupocho.logisticsac.repository.ConductorRepository;
import grupocho.logisticsac.service.ConductorService;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

public class ConductorView {

    private final Usuario usuario;
    private final DashboardLayout dashboardLayout;
    private final ConductorService conductorService;

    public ConductorView(Usuario usuario) {
        this(usuario, null);
    }

    public ConductorView(Usuario usuario, DashboardLayout dashboardLayout) {
        this.usuario = usuario;
        this.dashboardLayout = dashboardLayout;
        ConductorRepository repository = new ConductorDAO();
        this.conductorService = new ConductorService(repository);
    }

    public void mostrar(Stage stage) {

        TextField txtDni = new TextField();
        txtDni.setPromptText("DNI");

        TextField txtNombres = new TextField();
        txtNombres.setPromptText("Nombres completos");

        TextField txtLicencia = new TextField();
        txtLicencia.setPromptText("Número de licencia");

        TextField txtCategoria = new TextField();
        txtCategoria.setPromptText("Categoría de licencia");

        Button btnRegistrar = new Button("Registrar");
        btnRegistrar.getStyleClass().add("conductor-registrar");

        Button btnActualizar = new Button("Actualizar");
        Button btnEliminar = new Button("Eliminar");
        Button btnVolver = new Button("Volver");
        btnVolver.getStyleClass().add("conductor-volver");

        TableView<Conductor> tabla = new TableView<>();
        tabla.getStyleClass().add("conductor-tabla");
        tabla.setPrefHeight(300);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

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
        mensaje.getStyleClass().add("conductor-mensaje");
        mensaje.setWrapText(true);

        tabla.setOnMouseClicked(event -> {
            Conductor seleccionado = tabla.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                txtDni.setText(seleccionado.getDni());
                txtNombres.setText(seleccionado.getNombres());
                txtLicencia.setText(seleccionado.getNumeroLicencia());
                txtCategoria.setText(seleccionado.getCategoriaLicencia());
            }
        });

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
                limpiar(txtDni, txtNombres, txtLicencia, txtCategoria);
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
                limpiar(txtDni, txtNombres, txtLicencia, txtCategoria);
                mensaje.setText("Conductor eliminado correctamente.");
            } catch (SQLException e) {
                mensaje.setText("Error al eliminar conductor.");
            }
        });

        btnVolver.setOnAction(event -> {
            if (dashboardLayout != null) {
                new DashboardView(usuario).mostrarContenidoInicial(dashboardLayout);
            } else {
                new DashboardView(usuario).mostrar(stage);
            }
        });

        GridPane formulario = new GridPane();
        formulario.setHgap(12);
        formulario.setVgap(10);

        formulario.add(new Label("DNI:"), 0, 0);
        formulario.add(txtDni, 1, 0);

        formulario.add(new Label("Nombres:"), 2, 0);
        formulario.add(txtNombres, 3, 0);

        formulario.add(new Label("Licencia:"), 0, 1);
        formulario.add(txtLicencia, 1, 1);

        formulario.add(new Label("Categoría:"), 2, 1);
        formulario.add(txtCategoria, 3, 1);

        GridPane.setHgrow(txtDni, Priority.ALWAYS);
        GridPane.setHgrow(txtNombres, Priority.ALWAYS);
        GridPane.setHgrow(txtLicencia, Priority.ALWAYS);
        GridPane.setHgrow(txtCategoria, Priority.ALWAYS);

        formulario.setMaxWidth(750);

        VBox panelFormulario = crearPanel("DATOS DEL CONDUCTOR", formulario);
        HBox botones = new HBox(8, btnRegistrar, btnActualizar, btnEliminar);

        VBox panelTabla = crearPanel("CONDUCTORES REGISTRADOS", tabla);

        VBox contenido = new VBox(
                12, panelFormulario, botones,
                panelTabla, mensaje, btnVolver
        );
        contenido.getStyleClass().add("conductor-contenedor");
        contenido.setPadding(new Insets(16));

        ScrollPane scroll = new ScrollPane(contenido);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.getStyleClass().add("conductor-scroll");

        cargar(tabla, mensaje);

        if (dashboardLayout != null) {
            dashboardLayout.mostrarContenido("Conductores", scroll);
            Node actual = dashboardLayout.getContenido();
            if (actual != null && actual.getScene() != null) {
                String css = getClass().getResource("/style/conductor.css").toExternalForm();
                if (!actual.getScene().getStylesheets().contains(css)) {
                    actual.getScene().getStylesheets().add(css);
                }
            }
        } else {
            Scene scene = new Scene(scroll, 950, 700);
            scene.getStylesheets().add(getClass().getResource("/style/conductor.css").toExternalForm());
            stage.setTitle("Logistic S.A.C. - Conductores");
            stage.setScene(scene);
            stage.show();
            stage.centerOnScreen();
        }
    }

    private VBox crearPanel(String titulo, Node... elementos) {
        Label encabezado = new Label(titulo);
        VBox panel = new VBox(10, encabezado);
        panel.getStyleClass().add("conductor-panel");
        panel.getChildren().addAll(elementos);
        return panel;
    }

    private void cargar(TableView<Conductor> tabla, Label mensaje) {
        try {
            tabla.setItems(FXCollections.observableArrayList(conductorService.listar()));
        } catch (SQLException e) {
            mensaje.setText("Error al cargar conductores.");
        }
    }

    private void limpiar(TextField... campos) {
        for (TextField campo : campos) {
            campo.clear();
        }
    }
}
