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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.List;

public class RecepcionView {
    private final Usuario usuario;
    private final RecepcionService recepcionService;
    private final TrasladoService trasladoService;

    public RecepcionView(Usuario usuario) {
        this.usuario = usuario;
        RecepcionRepository recepcionRepository = new RecepcionDAO();
        TrasladoRepository trasladoRepository = new TrasladoDAO();
        this.recepcionService = new RecepcionService(recepcionRepository, trasladoRepository, new PrecintoDAO());
        this.trasladoService = new TrasladoService(trasladoRepository, new DocumentoDAO());
    }

    public void mostrar(Stage stage) {
        Label titulo = new Label("RECEPCIÓN DE TRASLADO");

        ComboBox<Traslado> cmbTraslado = new ComboBox<>();
        cmbTraslado.setPromptText("Seleccione traslado");

        Label lblTraslado = new Label("Traslado: -");

        // detalle de la carga que debe llegar
        ListView<String> listaCarga = new ListView<>();
        listaCarga.setPrefHeight(110);

        TextField txtPrecinto = new TextField();
        txtPrecinto.setPromptText("Número de precinto que llegó");

        CheckBox chkCarga = new CheckBox("Carga conforme");

        TextArea txtObservacion = new TextArea();
        txtObservacion.setPromptText("Observación / incidencia");
        txtObservacion.setPrefRowCount(3);

        Button btnRegistrar = new Button("Confirmar recepción");
        Button btnVolver = new Button("Volver");
        Label mensaje = new Label();
        mensaje.setWrapText(true);

        try {
            List<Traslado> traslados = trasladoService.listar().stream()
                    .filter(t -> t.getEstado() == EstadoTraslado.EN_TRANSITO)
                    .toList();

            cmbTraslado.setItems(FXCollections.observableArrayList(traslados));
        } catch (SQLException e) {
            mensaje.setText("No se pudieron cargar los traslados.");
        }

        cmbTraslado.setOnAction(event -> {
            Traslado traslado = cmbTraslado.getValue();
            listaCarga.getItems().clear();

            if (traslado == null) {
                lblTraslado.setText("Traslado: -");
                return;
            }

            lblTraslado.setText("Vehículo: " + traslado.getVehiculo()
                    + " | Conductor: " + traslado.getConductor()
                    + " | " + traslado.getAlmacenOrigen().getNombre()
                    + " -> " + traslado.getAlmacenDestino().getNombre());

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
                    mensaje.setText("Ingrese el número de precinto que llegó.");
                    return;
                }

                Recepcion recepcion = new Recepcion(traslado, usuario);
                recepcion.setCargaConforme(chkCarga.isSelected());
                recepcion.setObservacion(txtObservacion.getText().isBlank() ? null : txtObservacion.getText().trim());

                // el servicio compara el precinto con el registrado en garita
                recepcionService.registrar(recepcion, txtPrecinto.getText().trim());

                String fechaHora = recepcion.getFechaHoraRecepcion().toLocalDate()
                        + " " + recepcion.getFechaHoraRecepcion().toLocalTime().withNano(0);

                mensaje.setText(
                        recepcion.tieneObservaciones()
                                ? "Recepción registrada CON OBSERVACIONES (" + fechaHora + ")."
                                : "Traslado " + traslado.getCodigo() + " RECIBIDO (" + fechaHora + ")."
                );

                cmbTraslado.getItems().remove(traslado);
                cmbTraslado.setValue(null);
                txtPrecinto.clear();
                chkCarga.setSelected(false);
                txtObservacion.clear();

            } catch (IllegalArgumentException | IllegalStateException e) {
                mensaje.setText(e.getMessage());
            } catch (SQLException e) {
                mensaje.setText("Error al registrar la recepción.");
            }
        });

        btnVolver.setOnAction(event -> {
            DashboardView dashboardView = new DashboardView(usuario);
            dashboardView.mostrar(stage);
        });

        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);

        formulario.add(new Label("Traslado:"), 0, 0);
        formulario.add(cmbTraslado, 1, 0);
        formulario.add(new Label("Precinto:"), 0, 1);
        formulario.add(txtPrecinto, 1, 1);
        formulario.add(chkCarga, 1, 2);
        formulario.add(new Label("Observación:"), 0, 3);
        formulario.add(txtObservacion, 1, 3);

        VBox layout = new VBox(
                12,
                titulo,
                formulario,
                lblTraslado,
                new Label("Carga según el detalle del traslado:"),
                listaCarga,
                btnRegistrar,
                mensaje,
                btnVolver
        );
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 700, 580);
        stage.setTitle("Logistic S.A.C. - Recepción");
        stage.setScene(scene);
        stage.show();
        stage.centerOnScreen();
    }
}
