package grupocho.logisticsac.interfaz;

import grupocho.logisticsac.dao.ConductorDAO;
import grupocho.logisticsac.dao.DocumentoDAO;
import grupocho.logisticsac.dao.TipoDocumentoDAO;
import grupocho.logisticsac.dao.VehiculoDAO;
import grupocho.logisticsac.enums.AmbitoDocumento;
import grupocho.logisticsac.modelo.Conductor;
import grupocho.logisticsac.modelo.Documento;
import grupocho.logisticsac.modelo.TipoDocumento;
import grupocho.logisticsac.modelo.Usuario;
import grupocho.logisticsac.modelo.Vehiculo;
import grupocho.logisticsac.repository.TipoDocumentoRepository;
import grupocho.logisticsac.service.ConductorService;
import grupocho.logisticsac.service.DocumentoService;
import grupocho.logisticsac.service.TipoDocumentoService;
import grupocho.logisticsac.service.VehiculoService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;

public class DocumentoView {
    private final Usuario usuario;
    private final DocumentoService documentoService;
    private final TipoDocumentoService tipoDocumentoService;
    private final VehiculoService vehiculoService;
    private final ConductorService conductorService;

    public DocumentoView(Usuario usuario) {
        this.usuario = usuario;
        TipoDocumentoRepository tipoDocumentoRepository = new TipoDocumentoDAO();
        this.documentoService = new DocumentoService(new DocumentoDAO(), tipoDocumentoRepository);
        this.tipoDocumentoService = new TipoDocumentoService(tipoDocumentoRepository);
        this.vehiculoService = new VehiculoService(new VehiculoDAO());
        this.conductorService = new ConductorService(new ConductorDAO());
    }

    public void mostrar(Stage stage) {
        Label titulo = new Label("DOCUMENTOS DE VEHÍCULOS Y CONDUCTORES");

        ComboBox<AmbitoDocumento> cmbAmbito = new ComboBox<>();
        cmbAmbito.getItems().addAll(AmbitoDocumento.VEHICULO, AmbitoDocumento.CONDUCTOR);
        cmbAmbito.setPromptText("Seleccione");

        ComboBox<Vehiculo> cmbVehiculo = new ComboBox<>();
        cmbVehiculo.setPromptText("Seleccione vehículo");
        cmbVehiculo.setDisable(true);

        ComboBox<Conductor> cmbConductor = new ComboBox<>();
        cmbConductor.setPromptText("Seleccione conductor");
        cmbConductor.setDisable(true);

        ComboBox<TipoDocumento> cmbTipo = new ComboBox<>();
        cmbTipo.setPromptText("Seleccione tipo");

        TextField txtNumero = new TextField();
        txtNumero.setPromptText("Número del documento");

        DatePicker dpEmision = new DatePicker();
        dpEmision.setPromptText("Fecha de emisión");

        DatePicker dpVencimiento = new DatePicker();
        dpVencimiento.setPromptText("Fecha de vencimiento");

        Button btnRegistrar = new Button("Registrar documento");
        Button btnVolver = new Button("Volver");

        TableView<Documento> tabla = TablaDocumentos.crear();
        Label mensaje = new Label();

        try {
            cmbVehiculo.getItems().addAll(vehiculoService.listar());
            cmbConductor.getItems().addAll(conductorService.listar());
        } catch (SQLException e) {
            mensaje.setText("No se pudieron cargar los datos de la base de datos.");
        }

        cmbAmbito.setOnAction(event -> {
            AmbitoDocumento ambito = cmbAmbito.getValue();

            cmbVehiculo.setDisable(ambito != AmbitoDocumento.VEHICULO);
            cmbConductor.setDisable(ambito != AmbitoDocumento.CONDUCTOR);
            cmbVehiculo.setValue(null);
            cmbConductor.setValue(null);
            cmbTipo.getItems().clear();
            tabla.getItems().clear();

            if (ambito == null) {
                return;
            }

            try {
                cmbTipo.getItems().addAll(tipoDocumentoService.listarPorAmbito(ambito));
            } catch (SQLException e) {
                mensaje.setText("No se pudieron cargar los tipos de documento.");
            }
        });

        cmbVehiculo.setOnAction(event -> cargar(tabla, mensaje, cmbVehiculo.getValue(), null));
        cmbConductor.setOnAction(event -> cargar(tabla, mensaje, null, cmbConductor.getValue()));

        btnRegistrar.setOnAction(event -> {
            try {
                AmbitoDocumento ambito = cmbAmbito.getValue();

                if (ambito == null) {
                    mensaje.setText("Seleccione a quién pertenece el documento.");
                    return;
                }

                if (cmbTipo.getValue() == null) {
                    mensaje.setText("Seleccione el tipo de documento.");
                    return;
                }

                if (txtNumero.getText().isBlank()) {
                    mensaje.setText("Ingrese el número del documento.");
                    return;
                }

                if (dpEmision.getValue() == null || dpVencimiento.getValue() == null) {
                    mensaje.setText("Seleccione la fecha de emisión y la fecha de vencimiento.");
                    return;
                }

                if (dpVencimiento.getValue().isBefore(dpEmision.getValue())) {
                    mensaje.setText("La fecha de vencimiento no puede ser anterior a la de emisión.");
                    return;
                }

                Documento documento = new Documento(
                        txtNumero.getText().trim(),
                        dpEmision.getValue().toString(),
                        dpVencimiento.getValue().toString(),
                        "VIGENTE",
                        cmbTipo.getValue()
                );

                if (ambito == AmbitoDocumento.VEHICULO) {
                    documentoService.registrarParaVehiculo(documento, cmbVehiculo.getValue());
                } else {
                    documentoService.registrarParaConductor(documento, cmbConductor.getValue());
                }

                cargar(tabla, mensaje, cmbVehiculo.getValue(), cmbConductor.getValue());

                cmbTipo.setValue(null);
                txtNumero.clear();
                dpEmision.setValue(null);
                dpVencimiento.setValue(null);

                mensaje.setText("Documento registrado correctamente. Estado: " + documento.getEstado());
            } catch (IllegalArgumentException e) {
                mensaje.setText(e.getMessage());
            } catch (SQLException e) {
                mensaje.setText("Error al registrar el documento.");
            }
        });

        btnVolver.setOnAction(event -> new DashboardView(usuario).mostrar(stage));

        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);

        formulario.add(new Label("Pertenece a:"), 0, 0);
        formulario.add(cmbAmbito, 1, 0);
        formulario.add(new Label("Vehículo:"), 0, 1);
        formulario.add(cmbVehiculo, 1, 1);
        formulario.add(new Label("Conductor:"), 0, 2);
        formulario.add(cmbConductor, 1, 2);
        formulario.add(new Label("Tipo de documento:"), 0, 3);
        formulario.add(cmbTipo, 1, 3);
        formulario.add(new Label("Número:"), 0, 4);
        formulario.add(txtNumero, 1, 4);
        formulario.add(new Label("Fecha de emisión:"), 0, 5);
        formulario.add(dpEmision, 1, 5);
        formulario.add(new Label("Fecha de vencimiento:"), 0, 6);
        formulario.add(dpVencimiento, 1, 6);

        VBox layout = new VBox(
                10,
                titulo,
                formulario,
                btnRegistrar,
                new Label("Documentos registrados:"),
                tabla,
                mensaje,
                btnVolver
        );

        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 800, 650);
        stage.setTitle("Logistic S.A.C. - Documentos");
        stage.setScene(scene);
        stage.show();
        stage.centerOnScreen();
    }

    // muestra los documentos del vehiculo o del conductor seleccionado
    private void cargar(TableView<Documento> tabla, Label mensaje, Vehiculo vehiculo, Conductor conductor) {
        try {
            if (vehiculo != null) {
                tabla.setItems(FXCollections.observableArrayList(documentoService.listarPorVehiculo(vehiculo)));
            } else if (conductor != null) {
                tabla.setItems(FXCollections.observableArrayList(documentoService.listarPorConductor(conductor)));
            } else {
                tabla.getItems().clear();
            }
        } catch (SQLException e) {
            mensaje.setText("Error al cargar los documentos.");
        }
    }
}
