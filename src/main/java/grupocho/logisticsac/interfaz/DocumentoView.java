
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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;

public class DocumentoView {
    private final Usuario usuario;
    private final DocumentoService documentoService;
    private final TipoDocumentoService tipoDocumentoService;
    private final VehiculoService vehiculoService;
    private final ConductorService conductorService;
    private final DashboardLayout dashboardLayout;

    public DocumentoView(Usuario usuario) {
        this(usuario, null);
    }

    public DocumentoView(Usuario usuario, DashboardLayout dashboardLayout) {
        this.usuario = usuario;
        this.dashboardLayout = dashboardLayout;
        TipoDocumentoRepository tipoDocumentoRepository = new TipoDocumentoDAO();
        this.documentoService = new DocumentoService(new DocumentoDAO(), tipoDocumentoRepository);
        this.tipoDocumentoService = new TipoDocumentoService(tipoDocumentoRepository);
        this.vehiculoService = new VehiculoService(new VehiculoDAO());
        this.conductorService = new ConductorService(new ConductorDAO());
    }

    public void mostrar(Stage stage) {
        Label titulo = new Label("DOCUMENTOS DE VEHÍCULOS Y CONDUCTORES");
        titulo.getStyleClass().add("documento-titulo");

        Label subtitulo = new Label("Registro y consulta de documentos");
        subtitulo.getStyleClass().add("documento-subtitulo");

        ComboBox<AmbitoDocumento> cmbAmbito = new ComboBox<>();
        cmbAmbito.getItems().addAll(AmbitoDocumento.VEHICULO, AmbitoDocumento.CONDUCTOR);
        cmbAmbito.setPromptText("Seleccione");
        cmbAmbito.setMaxWidth(Double.MAX_VALUE);

        ComboBox<Vehiculo> cmbVehiculo = new ComboBox<>();
        cmbVehiculo.setPromptText("Seleccione vehículo");
        cmbVehiculo.setDisable(true);
        cmbVehiculo.setMaxWidth(Double.MAX_VALUE);

        ComboBox<Conductor> cmbConductor = new ComboBox<>();
        cmbConductor.setPromptText("Seleccione conductor");
        cmbConductor.setDisable(true);
        cmbConductor.setMaxWidth(Double.MAX_VALUE);

        ComboBox<TipoDocumento> cmbTipo = new ComboBox<>();
        cmbTipo.setPromptText("Seleccione tipo");
        cmbTipo.setMaxWidth(Double.MAX_VALUE);

        TextField txtNumero = new TextField();
        txtNumero.setPromptText("Número del documento");

        DatePicker dpEmision = new DatePicker();
        dpEmision.setPromptText("Fecha de emisión");
        dpEmision.setMaxWidth(Double.MAX_VALUE);

        DatePicker dpVencimiento = new DatePicker();
        dpVencimiento.setPromptText("Fecha de vencimiento");
        dpVencimiento.setMaxWidth(Double.MAX_VALUE);

        Button btnRegistrar = new Button("Registrar documento");
        btnRegistrar.getStyleClass().add("documento-registrar");

        Button btnVolver = new Button("Volver");
        btnVolver.getStyleClass().add("documento-volver");

        TableView<Documento> tabla = TablaDocumentos.crear();
        tabla.getStyleClass().add("documento-tabla");
        tabla.setPrefHeight(230);

        Label mensaje = new Label();
        mensaje.getStyleClass().add("documento-mensaje");
        mensaje.setWrapText(true);

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
            cmbTipo.setValue(null);
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
                if (ambito == AmbitoDocumento.VEHICULO && cmbVehiculo.getValue() == null) {
                    mensaje.setText("Seleccione un vehículo.");
                    return;
                }
                if (ambito == AmbitoDocumento.CONDUCTOR && cmbConductor.getValue() == null) {
                    mensaje.setText("Seleccione un conductor.");
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

        btnVolver.setOnAction(event -> {
            if (dashboardLayout != null) {
                dashboardLayout.mostrarContenido("Panel principal", null);
            } else {
                new DashboardView(usuario).mostrar(stage);
            }
        });

        GridPane formulario = new GridPane();
        formulario.setHgap(12);
        formulario.setVgap(10);
        formulario.setMaxWidth(850);

        formulario.add(new Label("Pertenece a:"), 0, 0);
        formulario.add(cmbAmbito, 1, 0);
        formulario.add(new Label("Tipo de documento:"), 2, 0);
        formulario.add(cmbTipo, 3, 0);

        formulario.add(new Label("Vehículo:"), 0, 1);
        formulario.add(cmbVehiculo, 1, 1);
        formulario.add(new Label("Conductor:"), 2, 1);
        formulario.add(cmbConductor, 3, 1);

        formulario.add(new Label("Número:"), 0, 2);
        formulario.add(txtNumero, 1, 2);
        formulario.add(new Label("Fecha de emisión:"), 2, 2);
        formulario.add(dpEmision, 3, 2);

        formulario.add(new Label("Fecha de vencimiento:"), 0, 3);
        formulario.add(dpVencimiento, 1, 3);

        GridPane.setHgrow(cmbAmbito, Priority.ALWAYS);
        GridPane.setHgrow(cmbTipo, Priority.ALWAYS);
        GridPane.setHgrow(cmbVehiculo, Priority.ALWAYS);
        GridPane.setHgrow(cmbConductor, Priority.ALWAYS);
        GridPane.setHgrow(txtNumero, Priority.ALWAYS);
        GridPane.setHgrow(dpEmision, Priority.ALWAYS);
        GridPane.setHgrow(dpVencimiento, Priority.ALWAYS);

        VBox panelFormulario = crearPanel("DATOS DEL DOCUMENTO", formulario);
        VBox panelTabla = crearPanel("DOCUMENTOS REGISTRADOS", tabla);

        VBox contenido = new VBox(
                12, titulo, subtitulo, panelFormulario,
                btnRegistrar, panelTabla, mensaje, btnVolver
        );
        contenido.getStyleClass().add("documento-contenedor");
        contenido.setPadding(new Insets(20));

        ScrollPane scroll = new ScrollPane(contenido);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.getStyleClass().add("documento-scroll");

        if (dashboardLayout != null) {
            dashboardLayout.mostrarContenido("Documentos", scroll);
            Node actual = dashboardLayout.getContenido();
            if (actual != null && actual.getScene() != null) {
                String css = getClass().getResource("/style/documento.css").toExternalForm();
                if (!actual.getScene().getStylesheets().contains(css)) {
                    actual.getScene().getStylesheets().add(css);
                }
            }
        } else {
            Scene scene = new Scene(scroll, 1100, 700);
            scene.getStylesheets().add(getClass().getResource("/style/documento.css").toExternalForm());
            stage.setTitle("Logistic S.A.C. - Documentos");
            stage.setScene(scene);
            stage.show();
            stage.centerOnScreen();
        }
    }

    private VBox crearPanel(String titulo, Node... elementos) {
        Label encabezado = new Label(titulo);
        VBox panel = new VBox(10, encabezado);
        panel.getStyleClass().add("documento-panel");
        panel.getChildren().addAll(elementos);
        return panel;
    }

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
