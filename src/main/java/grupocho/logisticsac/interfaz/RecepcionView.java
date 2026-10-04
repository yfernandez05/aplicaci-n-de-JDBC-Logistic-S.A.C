package grupocho.logisticsac.interfaz;

import grupocho.logisticsac.dao.RecepcionDAO;
import grupocho.logisticsac.dao.TrasladoDAO;
import grupocho.logisticsac.enums.EstadoTraslado;
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
import java.time.LocalDateTime;
import java.util.List;

public class RecepcionView {
    private final Usuario usuario;
    private final RecepcionService recepcionService;
    private final TrasladoService trasladoService;

    public RecepcionView(Usuario usuario) {
        this.usuario = usuario;
        RecepcionRepository recepcionRepository = new RecepcionDAO();
        TrasladoRepository trasladoRepository = new TrasladoDAO();
        this.recepcionService = new RecepcionService(recepcionRepository, trasladoRepository);
        this.trasladoService = new TrasladoService(trasladoRepository);
    }

    public void mostrar(Stage stage) {
        Label titulo = new Label("RECEPCIÓN DE TRASLADO");

        ComboBox<Traslado> cmbTraslado = new ComboBox<>();
        cmbTraslado.setPromptText("Seleccione traslado");

        CheckBox chkPrecinto = new CheckBox("Precinto conforme");
        CheckBox chkCarga = new CheckBox("Carga conforme");

        TextArea txtObservacion = new TextArea();
        txtObservacion.setPromptText("Observación");
        txtObservacion.setPrefRowCount(3);

        Button btnRegistrar = new Button("Registrar recepción");
        Button btnVolver = new Button("Volver");
        Label mensaje = new Label();

        try {
            List<Traslado> traslados = trasladoService.listar().stream()
                    .filter(t -> t.getEstado() == EstadoTraslado.EN_TRANSITO)
                    .toList();

            cmbTraslado.setItems(FXCollections.observableArrayList(traslados));
        } catch (SQLException e) {
            mensaje.setText("No se pudieron cargar los traslados.");
        }

        btnRegistrar.setOnAction(event -> {
            try {
                Traslado traslado = cmbTraslado.getValue();

                if (traslado == null) {
                    mensaje.setText("Seleccione un traslado.");
                    return;
                }

                if (!chkPrecinto.isSelected() || !chkCarga.isSelected()) {
                    if (txtObservacion.getText().isBlank()) {
                        mensaje.setText("Ingrese una observación cuando exista una diferencia.");
                        return;
                    }
                }

                Recepcion recepcion = new Recepcion();
                recepcion.setFechaHoraRecepcion(LocalDateTime.now());
                recepcion.setPrecintoConforme(chkPrecinto.isSelected());
                recepcion.setCargaConforme(chkCarga.isSelected());
                recepcion.setObservacion(txtObservacion.getText());
                recepcion.setTraslado(traslado);
                recepcion.setDespachador(usuario);

                recepcionService.registrar(recepcion);

                mensaje.setText(
                        recepcion.tieneObservaciones()
                                ? "Recepción registrada con observaciones."
                                : "Recepción registrada correctamente."
                );

                cmbTraslado.getItems().remove(traslado);
                cmbTraslado.setValue(null);
                chkPrecinto.setSelected(false);
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
        formulario.setPadding(new Insets(20));

        formulario.add(new Label("Traslado:"), 0, 0);
        formulario.add(cmbTraslado, 1, 0);
        formulario.add(chkPrecinto, 0, 1, 2, 1);
        formulario.add(chkCarga, 0, 2, 2, 1);
        formulario.add(new Label("Observación:"), 0, 3);
        formulario.add(txtObservacion, 1, 3);

        VBox layout = new VBox(15, titulo, formulario, btnRegistrar, mensaje, btnVolver);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 650, 500);
        stage.setTitle("Logistic S.A.C. - Recepción");
        stage.setScene(scene);
        stage.show();
    }
}