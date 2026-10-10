package grupocho.logisticsac.interfaz;

import grupocho.logisticsac.dao.DocumentoDAO;
import grupocho.logisticsac.dao.PrecintoDAO;
import grupocho.logisticsac.dao.RecepcionDAO;
import grupocho.logisticsac.dao.TrasladoDAO;
import grupocho.logisticsac.enums.EstadoTraslado;
import grupocho.logisticsac.modelo.DetalleTraslado;
import grupocho.logisticsac.modelo.Recepcion;
import grupocho.logisticsac.modelo.Traslado;
import grupocho.logisticsac.modelo.Usuario;
import grupocho.logisticsac.repository.RecepcionRepository;
import grupocho.logisticsac.repository.TrasladoRepository;
import grupocho.logisticsac.service.RecepcionService;
import grupocho.logisticsac.service.TrasladoService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.List;

public class RecepcionView {
    private final Usuario usuario;
    private final RecepcionService recepcionService;
    private final TrasladoService trasladoService;
    private final DashboardLayout dashboardLayout;

    public RecepcionView(Usuario usuario) {
        this(usuario, null);
    }

    public RecepcionView(Usuario usuario, DashboardLayout dashboardLayout) {
        this.usuario = usuario;
        this.dashboardLayout = dashboardLayout;
        RecepcionRepository recepcionRepository = new RecepcionDAO();
        TrasladoRepository trasladoRepository = new TrasladoDAO();
        this.recepcionService = new RecepcionService(recepcionRepository, trasladoRepository, new PrecintoDAO());
        this.trasladoService = new TrasladoService(trasladoRepository, new DocumentoDAO());
    }

    public void mostrar(Stage stage) {
        Label titulo = new Label("RECEPCIÓN DE TRASLADO");
        titulo.getStyleClass().add("recepcion-titulo");

        ComboBox<Traslado> cmbTraslado = new ComboBox<>();
        cmbTraslado.setPromptText("Seleccione traslado");
        cmbTraslado.setMaxWidth(Double.MAX_VALUE);
        cmbTraslado.getStyleClass().add("recepcion-campo");

        Label lblTraslado = new Label("Traslado: -");
        lblTraslado.setWrapText(true);
        lblTraslado.getStyleClass().add("recepcion-informacion");

        ListView<String> listaCarga = new ListView<>();
        listaCarga.setPrefHeight(115);
        listaCarga.setMaxHeight(140);
        listaCarga.getStyleClass().add("recepcion-lista");

        TextField txtPrecinto = new TextField();
        txtPrecinto.setPromptText("Número de precinto");
        txtPrecinto.setMaxWidth(Double.MAX_VALUE);
        txtPrecinto.getStyleClass().add("recepcion-campo");

        CheckBox chkCarga = new CheckBox("Carga conforme");
        chkCarga.getStyleClass().add("recepcion-checkbox");

        TextArea txtObservacion = new TextArea();
        txtObservacion.setPromptText("Observación o incidencia");
        txtObservacion.setPrefRowCount(2);
        txtObservacion.setMaxHeight(75);
        txtObservacion.setWrapText(true);
        txtObservacion.getStyleClass().add("recepcion-campo");

        Button btnRegistrar = new Button("Confirmar recepción");
        btnRegistrar.getStyleClass().add("recepcion-registrar");

        Button btnVolver = new Button("Volver");
        btnVolver.getStyleClass().add("recepcion-volver");

        Label mensaje = new Label();
        mensaje.setWrapText(true);
        mensaje.getStyleClass().add("recepcion-mensaje");

        try {
            List<Traslado> traslados = trasladoService.listar()
                    .stream()
                    .filter(t -> t.getEstado() == EstadoTraslado.EN_TRANSITO)
                    .toList();
            cmbTraslado.setItems(FXCollections.observableArrayList(traslados));
        } catch (SQLException e) {
            mensaje.setText("No se pudieron cargar los traslados.");
        }

        cmbTraslado.setOnAction(event -> {
            Traslado traslado = cmbTraslado.getValue();
            listaCarga.getItems().clear();
            mensaje.setText("");

            if (traslado == null) {
                lblTraslado.setText("Traslado: -");
                return;
            }

            lblTraslado.setText("Vehículo: " + traslado.getVehiculo()
                    + " | Conductor: " + traslado.getConductor()
                    + " | " + traslado.getAlmacenOrigen().getNombre()
                    + " → " + traslado.getAlmacenDestino().getNombre());

            try {
                for (DetalleTraslado detalle : trasladoService.listarDetalles(traslado)) {
                    listaCarga.getItems().add(detalle.getProducto().getDescripcion()
                            + " - " + detalle.getCantidad()
                            + " " + detalle.getProducto().getUnidadMedida());
                }
            } catch (SQLException e) {
                mensaje.setText("No se pudo cargar el detalle del traslado.");
            }
        });

        btnRegistrar.setOnAction(event -> {
            try {
                Traslado traslado = cmbTraslado.getValue();

                if (traslado == null) {
                    mensaje.setText("Seleccione un traslado.");
                    return;
                }

                if (txtPrecinto.getText().isBlank()) {
                    mensaje.setText("Ingrese el número de precinto.");
                    return;
                }

                Recepcion recepcion = new Recepcion(traslado, usuario);
                recepcion.setCargaConforme(chkCarga.isSelected());
                recepcion.setObservacion(
                        txtObservacion.getText().isBlank()
                                ? null : txtObservacion.getText().trim()
                );

                recepcionService.registrar(recepcion, txtPrecinto.getText().trim());

                String fechaHora = recepcion.getFechaHoraRecepcion().toLocalDate()
                        + " " + recepcion.getFechaHoraRecepcion().toLocalTime().withNano(0);

                mensaje.setText(recepcion.tieneObservaciones()
                        ? "Recepción registrada con observaciones (" + fechaHora + ")."
                        : "Traslado " + traslado.getCodigo() + " recibido (" + fechaHora + ").");

                cmbTraslado.getItems().remove(traslado);
                cmbTraslado.setValue(null);
                txtPrecinto.clear();
                chkCarga.setSelected(false);
                txtObservacion.clear();
                listaCarga.getItems().clear();
                lblTraslado.setText("Traslado: -");

            } catch (IllegalArgumentException | IllegalStateException e) {
                mensaje.setText(e.getMessage());
            } catch (SQLException e) {
                mensaje.setText("Error al registrar la recepción.");
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
        formulario.getStyleClass().add("recepcion-formulario");
        formulario.add(new Label("Traslado"), 0, 0);
        formulario.add(cmbTraslado, 1, 0);
        formulario.add(new Label("Precinto"), 0, 1);
        formulario.add(txtPrecinto, 1, 1);
        formulario.add(chkCarga, 1, 2);
        formulario.add(new Label("Observación"), 0, 3);
        formulario.add(txtObservacion, 1, 3);

        VBox panelRecepcion = new VBox(12, titulo, formulario);
        panelRecepcion.getStyleClass().add("recepcion-panel");

        VBox panelCarga = new VBox(8,
                new Label("INFORMACIÓN DEL TRASLADO"),
                lblTraslado,
                new Label("CARGA ESPERADA"),
                listaCarga);
        panelCarga.getStyleClass().add("recepcion-panel");

        HBox acciones = new HBox(10, btnRegistrar, btnVolver);
        acciones.getStyleClass().add("recepcion-acciones");

        VBox panelConfirmacion = new VBox(10, acciones, mensaje);
        panelConfirmacion.getStyleClass().add("recepcion-panel");

        VBox contenido = new VBox(12, panelRecepcion, panelCarga, panelConfirmacion);
        contenido.setPadding(new Insets(16));
        contenido.getStyleClass().add("recepcion-contenedor");

        ScrollPane scroll = new ScrollPane(contenido);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("recepcion-scroll");

        var recurso = getClass().getResource("/style/recepcion.css");

        if (dashboardLayout != null) {
            Scene scene = stage.getScene();
            if (recurso != null && !scene.getStylesheets().contains(recurso.toExternalForm())) {
                scene.getStylesheets().add(recurso.toExternalForm());
            }
            dashboardLayout.mostrarContenido("Recepción de traslado", scroll);
        } else {
            Scene scene = new Scene(scroll, 800, 620);
            if (recurso != null) {
                scene.getStylesheets().add(recurso.toExternalForm());
            }
            stage.setTitle("Logistic S.A.C. - Recepción");
            stage.setScene(scene);
            stage.show();
            stage.centerOnScreen();
        }
    }
}