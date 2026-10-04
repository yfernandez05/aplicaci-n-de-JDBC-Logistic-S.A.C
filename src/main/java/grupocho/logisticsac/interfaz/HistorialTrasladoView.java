package grupocho.logisticsac.interfaz;

import grupocho.logisticsac.dao.TrasladoDAO;
import grupocho.logisticsac.enums.EstadoTraslado;
import grupocho.logisticsac.modelo.Traslado;
import grupocho.logisticsac.modelo.Usuario;
import grupocho.logisticsac.repository.TrasladoRepository;
import grupocho.logisticsac.service.TrasladoService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class HistorialTrasladoView {
    private final Usuario usuario;
    private final TrasladoService trasladoService;

    public HistorialTrasladoView(Usuario usuario) {
        this.usuario = usuario;
        TrasladoRepository repository = new TrasladoDAO();
        this.trasladoService = new TrasladoService(repository);
    }

    public void mostrar(Stage stage) {
        Label titulo = new Label("HISTORIAL DE TRASLADOS");

        TextField txtCodigo = new TextField();
        txtCodigo.setPromptText("Código");

        DatePicker fecha = new DatePicker();
        fecha.setPromptText("Fecha");

        ComboBox<EstadoTraslado> cmbEstado = new ComboBox<>();
        cmbEstado.getItems().addAll(EstadoTraslado.values());
        cmbEstado.setPromptText("Estado");

        Button btnBuscar = new Button("Buscar");
        Button btnLimpiar = new Button("Limpiar");

        HBox filtros = new HBox(10, txtCodigo, fecha, cmbEstado, btnBuscar, btnLimpiar);

        TableView<Traslado> tabla = new TableView<>();

        TableColumn<Traslado, Integer> id = new TableColumn<>("ID");
        id.setCellValueFactory(new PropertyValueFactory<>("idTraslado"));

        TableColumn<Traslado, String> codigo = new TableColumn<>("Código");
        codigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));

        TableColumn<Traslado, String> fechaColumna = new TableColumn<>("Fecha");
        fechaColumna.setCellValueFactory(cellData -> {
            LocalDate valor = cellData.getValue().getFechaProgramada();
            return new SimpleStringProperty(valor != null ? valor.toString() : "");
        });

        TableColumn<Traslado, String> origen = new TableColumn<>("Origen");
        origen.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getAlmacenOrigen() != null
                                ? cellData.getValue().getAlmacenOrigen().getNombre()
                                : ""
                )
        );

        TableColumn<Traslado, String> destino = new TableColumn<>("Destino");
        destino.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getAlmacenDestino() != null
                                ? cellData.getValue().getAlmacenDestino().getNombre()
                                : ""
                )
        );

        TableColumn<Traslado, String> vehiculo = new TableColumn<>("Vehículo");
        vehiculo.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getVehiculo() != null
                                ? cellData.getValue().getVehiculo().getPlaca()
                                : ""
                )
        );

        TableColumn<Traslado, String> conductor = new TableColumn<>("Conductor");
        conductor.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getConductor() != null
                                ? cellData.getValue().getConductor().getNombres()
                                : ""
                )
        );

        TableColumn<Traslado, String> estado = new TableColumn<>("Estado");
        estado.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getEstado() != null
                                ? cellData.getValue().getEstado().name()
                                : ""
                )
        );

        TableColumn<Traslado, String> observacion = new TableColumn<>("Observación");
        observacion.setCellValueFactory(new PropertyValueFactory<>("observacion"));

        TableColumn<Traslado, String> motivoRechazo = new TableColumn<>("Motivo rechazo");
        motivoRechazo.setCellValueFactory(new PropertyValueFactory<>("motivoRechazo"));

        tabla.getColumns().addAll(
                id,
                codigo,
                fechaColumna,
                origen,
                destino,
                vehiculo,
                conductor,
                estado,
                observacion,
                motivoRechazo
        );

        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Label mensaje = new Label();

        try {
            tabla.setItems(
                    FXCollections.observableArrayList(
                            trasladoService.listar()
                    )
            );
        } catch (SQLException e) {
            mensaje.setText("Error al cargar el historial.");
        }

        btnBuscar.setOnAction(event -> {
            try {
                String codigoFiltro = txtCodigo.getText();
                LocalDate fechaFiltro = fecha.getValue();
                EstadoTraslado estadoFiltro = cmbEstado.getValue();

                List<Traslado> resultados = trasladoService.buscar(
                        codigoFiltro,
                        fechaFiltro,
                        estadoFiltro,
                        null,
                        null,
                        null
                );

                tabla.setItems(FXCollections.observableArrayList(resultados));
                mensaje.setText("Resultados encontrados: " + resultados.size());
            } catch (SQLException e) {
                mensaje.setText("Error al realizar la búsqueda.");
            }
        });

        btnLimpiar.setOnAction(event -> {
            txtCodigo.clear();
            fecha.setValue(null);
            cmbEstado.setValue(null);

            try {
                tabla.setItems(
                        FXCollections.observableArrayList(
                                trasladoService.listar()
                        )
                );
                mensaje.setText("");
            } catch (SQLException e) {
                mensaje.setText("Error al cargar el historial.");
            }
        });

        Button btnVolver = new Button("Volver");

        btnVolver.setOnAction(event -> {
            DashboardView dashboardView = new DashboardView(usuario);
            dashboardView.mostrar(stage);
        });

        VBox layout = new VBox(
                15,
                titulo,
                filtros,
                tabla,
                mensaje,
                btnVolver
        );

        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 1200, 650);

        stage.setTitle("Logistic S.A.C. - Historial de traslados");
        stage.setScene(scene);
        stage.show();
        stage.centerOnScreen();
    }
}