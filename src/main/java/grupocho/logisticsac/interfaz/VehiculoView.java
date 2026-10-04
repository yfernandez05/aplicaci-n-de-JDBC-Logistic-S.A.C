package grupocho.logisticsac.interfaz;

import grupocho.logisticsac.dao.VehiculoDAO;
import grupocho.logisticsac.modelo.Usuario;
import grupocho.logisticsac.modelo.Vehiculo;
import grupocho.logisticsac.repository.VehiculoRepository;
import grupocho.logisticsac.service.VehiculoService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;

public class VehiculoView {
    private final Usuario usuario;
    private final VehiculoService vehiculoService;

    public VehiculoView(Usuario usuario) {
        this.usuario = usuario;
        VehiculoRepository repository = new VehiculoDAO();
        this.vehiculoService = new VehiculoService(repository);
    }

    public void mostrar(Stage stage) {
        Label titulo = new Label("VEHÍCULOS");

        TextField txtPlaca = new TextField();
        txtPlaca.setPromptText("Placa");

        TextField txtTipo = new TextField();
        txtTipo.setPromptText("Tipo");

        TextField txtCapacidad = new TextField();
        txtCapacidad.setPromptText("Capacidad de carga");

        TextField txtCondicion = new TextField();
        txtCondicion.setPromptText("Condición");

        TextField txtEstado = new TextField();
        txtEstado.setPromptText("Estado");

        Button btnRegistrar = new Button("Registrar");
        Button btnActualizar = new Button("Actualizar");
        Button btnEliminar = new Button("Eliminar");
        Button btnVolver = new Button("Volver");

        TableView<Vehiculo> tabla = new TableView<>();

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
        cargar(tabla, mensaje);

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

                txtPlaca.clear();
                txtTipo.clear();
                txtCapacidad.clear();
                txtCondicion.clear();
                txtEstado.clear();

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

                txtPlaca.clear();
                txtTipo.clear();
                txtCapacidad.clear();
                txtCondicion.clear();
                txtEstado.clear();

                mensaje.setText("Vehículo eliminado correctamente.");

            } catch (SQLException e) {
                mensaje.setText("Error al eliminar vehículo.");
            }
        });

        btnVolver.setOnAction(event -> new DashboardView(usuario).mostrar(stage));

        VBox layout = new VBox(
                10,
                titulo,
                txtPlaca,
                txtTipo,
                txtCapacidad,
                txtCondicion,
                txtEstado,
                btnRegistrar,
                btnActualizar,
                btnEliminar,
                tabla,
                mensaje,
                btnVolver
        );

        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 800, 550);
        stage.setTitle("Logistic S.A.C. - Vehículos");
        stage.setScene(scene);
        stage.show();
    }

    private void cargar(TableView<Vehiculo> tabla, Label mensaje) {
        try {
            tabla.setItems(FXCollections.observableArrayList(vehiculoService.listar()));
        } catch (SQLException e) {
            mensaje.setText("Error al cargar vehículos.");
        }
    }
}