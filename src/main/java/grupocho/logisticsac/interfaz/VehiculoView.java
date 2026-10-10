
package grupocho.logisticsac.interfaz;

import grupocho.logisticsac.dao.VehiculoDAO;
import grupocho.logisticsac.modelo.Usuario;
import grupocho.logisticsac.modelo.Vehiculo;
import grupocho.logisticsac.repository.VehiculoRepository;
import grupocho.logisticsac.service.VehiculoService;

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

public class VehiculoView {

    private final Usuario usuario;
    private final DashboardLayout dashboardLayout;
    private final VehiculoService vehiculoService;

    public VehiculoView(Usuario usuario) {
        this(usuario, null);
    }

    public VehiculoView(Usuario usuario, DashboardLayout dashboardLayout) {
        this.usuario = usuario;
        this.dashboardLayout = dashboardLayout;

        VehiculoRepository repository = new VehiculoDAO();
        this.vehiculoService = new VehiculoService(repository);
    }

    public void mostrar(Stage stage) {

        Label subtitulo = new Label("Gestión de vehículos y capacidad de carga");
        subtitulo.getStyleClass().add("vehiculo-subtitulo");

        TextField txtPlaca = new TextField();
        txtPlaca.setPromptText("Placa");

        TextField txtTipo = new TextField();
        txtTipo.setPromptText("Tipo de vehículo");

        TextField txtCapacidad = new TextField();
        txtCapacidad.setPromptText("Capacidad de carga");

        TextField txtCondicion = new TextField();
        txtCondicion.setPromptText("Condición");

        TextField txtEstado = new TextField();
        txtEstado.setPromptText("Estado");

        Button btnRegistrar = new Button("Registrar");
        btnRegistrar.getStyleClass().add("vehiculo-registrar");

        Button btnActualizar = new Button("Actualizar");
        Button btnEliminar = new Button("Eliminar");

        Button btnVolver = new Button("Volver");
        btnVolver.getStyleClass().add("vehiculo-volver");

        TableView<Vehiculo> tabla = new TableView<>();
        tabla.getStyleClass().add("vehiculo-tabla");
        tabla.setPrefHeight(280);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Vehiculo, String> placa = new TableColumn<>("Placa");
        placa.setCellValueFactory(new PropertyValueFactory<>("placa"));

        TableColumn<Vehiculo, String> tipo = new TableColumn<>("Tipo");
        tipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        TableColumn<Vehiculo, Double> capacidad = new TableColumn<>("Capacidad");
        capacidad.setCellValueFactory(new PropertyValueFactory<>("capacidadCarga"));

        TableColumn<Vehiculo, String> condicion = new TableColumn<>("Condición");
        condicion.setCellValueFactory(new PropertyValueFactory<>("condicion"));

        TableColumn<Vehiculo, String> estado = new TableColumn<>("Estado");
        estado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        tabla.getColumns().addAll(placa, tipo, capacidad, condicion, estado);

        Label mensaje = new Label();
        mensaje.getStyleClass().add("vehiculo-mensaje");
        mensaje.setWrapText(true);

        tabla.setOnMouseClicked(event -> {
            Vehiculo seleccionado = tabla.getSelectionModel().getSelectedItem();

            if (seleccionado != null) {
                txtPlaca.setText(seleccionado.getPlaca());
                txtTipo.setText(seleccionado.getTipo());
                txtCapacidad.setText(String.valueOf(seleccionado.getCapacidadCarga()));
                txtCondicion.setText(seleccionado.getCondicion());
                txtEstado.setText(seleccionado.getEstado());
            }
        });

        btnRegistrar.setOnAction(event -> {
            try {
                Vehiculo vehiculo = new Vehiculo(
                        txtPlaca.getText(),
                        txtTipo.getText(),
                        Double.parseDouble(txtCapacidad.getText()),
                        txtCondicion.getText(),
                        txtEstado.getText()
                );

                vehiculoService.registrar(vehiculo);
                cargar(tabla, mensaje);
                limpiar(txtPlaca, txtTipo, txtCapacidad,txtCondicion, txtEstado);

                mensaje.setText("Vehículo registrado correctamente.");

            } catch (NumberFormatException e) {
                mensaje.setText("La capacidad debe ser numérica.");
            } catch (IllegalArgumentException e) {
                mensaje.setText(e.getMessage());
            } catch (SQLException e) {
                mensaje.setText("Error al registrar vehículo.");
            }
        });

        btnActualizar.setOnAction(event -> {
            try {
                Vehiculo seleccionado = tabla.getSelectionModel().getSelectedItem();

                if (seleccionado == null) {
                    mensaje.setText("Seleccione un vehículo.");
                    return;
                }

                seleccionado.setPlaca(txtPlaca.getText());
                seleccionado.setTipo(txtTipo.getText());
                seleccionado.setCapacidadCarga(Double.parseDouble(txtCapacidad.getText()));
                seleccionado.setCondicion(txtCondicion.getText());
                seleccionado.setEstado(txtEstado.getText());

                vehiculoService.actualizar(seleccionado);
                cargar(tabla, mensaje);

                mensaje.setText("Vehículo actualizado correctamente.");

            } catch (NumberFormatException e) {
                mensaje.setText("La capacidad debe ser numérica.");
            } catch (IllegalArgumentException e) {
                mensaje.setText(e.getMessage());
            } catch (SQLException e) {
                mensaje.setText("Error al actualizar vehículo.");
            }
        });

        btnEliminar.setOnAction(event -> {
            try {
                Vehiculo seleccionado = tabla.getSelectionModel().getSelectedItem();

                if (seleccionado == null) {
                    mensaje.setText("Seleccione un vehículo.");
                    return;
                }

                vehiculoService.eliminar(seleccionado.getIdVehiculo());
                cargar(tabla, mensaje);

                limpiar(txtPlaca, txtTipo, txtCapacidad,txtCondicion, txtEstado);

                mensaje.setText("Vehículo eliminado correctamente.");

            } catch (SQLException e) {
                mensaje.setText("Error al eliminar vehículo.");
            }
        });

        btnVolver.setOnAction(event -> {
            if (dashboardLayout != null) {
                dashboardLayout.mostrarContenido("Panel principal", null);
            } else {
                new DashboardView(usuario).mostrar(stage);
            }
        });

        // Formulario distribuido en dos columnas
        GridPane formulario = new GridPane();
        formulario.setHgap(12);
        formulario.setVgap(10);
        formulario.setMaxWidth(750);

        formulario.add(new Label("Placa:"), 0, 0);
        formulario.add(txtPlaca, 1, 0);

        formulario.add(new Label("Tipo:"), 2, 0);
        formulario.add(txtTipo, 3, 0);

        formulario.add(new Label("Capacidad de carga:"), 0, 1);
        formulario.add(txtCapacidad, 1, 1);

        formulario.add(new Label("Condición:"), 2, 1);
        formulario.add(txtCondicion, 3, 1);

        formulario.add(new Label("Estado:"), 0, 2);
        formulario.add(txtEstado, 1, 2);

        GridPane.setHgrow(txtPlaca, Priority.ALWAYS);
        GridPane.setHgrow(txtTipo, Priority.ALWAYS);
        GridPane.setHgrow(txtCapacidad, Priority.ALWAYS);
        GridPane.setHgrow(txtCondicion, Priority.ALWAYS);
        GridPane.setHgrow(txtEstado, Priority.ALWAYS);

        VBox panelFormulario = crearPanel("DATOS DEL VEHÍCULO", formulario);

        HBox botones = new HBox(8, btnRegistrar, btnActualizar, btnEliminar);

        VBox panelTabla = crearPanel("VEHÍCULOS REGISTRADOS", tabla);

        VBox contenido = new VBox(
                12, subtitulo,
                panelFormulario, botones,
                panelTabla, mensaje, btnVolver
        );

        contenido.getStyleClass().add("vehiculo-contenedor");
        contenido.setPadding(new Insets(16));

        ScrollPane scroll = new ScrollPane(contenido);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.getStyleClass().add("vehiculo-scroll");

        cargar(tabla, mensaje);

        if (dashboardLayout != null) {
            dashboardLayout.mostrarContenido("Vehículos", scroll);

            Node actual = dashboardLayout.getContenido();

            if (actual != null && actual.getScene() != null) {
                String css = getClass().getResource("/style/vehiculo.css").toExternalForm();

                if (!actual.getScene().getStylesheets().contains(css)) {
                    actual.getScene().getStylesheets().add(css);
                }
            }
        } else {
            Scene scene = new Scene(scroll, 950, 700);
            scene.getStylesheets().add(getClass().getResource("/style/vehiculo.css").toExternalForm());
            stage.setTitle("Logistic S.A.C. - Vehículos");
            stage.setScene(scene);
            stage.show();
            stage.centerOnScreen();
        }
    }

    private VBox crearPanel(String titulo, Node... elementos) {
        Label encabezado = new Label(titulo);

        VBox panel = new VBox(10, encabezado);
        panel.getStyleClass().add("vehiculo-panel");
        panel.getChildren().addAll(elementos);

        return panel;
    }

    private void cargar(TableView<Vehiculo> tabla, Label mensaje) {
        try {
            tabla.setItems(FXCollections.observableArrayList(vehiculoService.listar()));
        } catch (SQLException e) {
            mensaje.setText("Error al cargar vehículos.");
        }
    }

    private void limpiar(TextField... campos) {
        for (TextField campo : campos) {
            campo.clear();
        }
    }
}
