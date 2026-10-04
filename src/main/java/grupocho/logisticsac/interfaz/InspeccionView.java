package grupocho.logisticsac.interfaz;

import grupocho.logisticsac.dao.EvidenciaDAO;
import grupocho.logisticsac.dao.InspeccionDAO;
import grupocho.logisticsac.dao.TrasladoDAO;
import grupocho.logisticsac.enums.EstadoTraslado;
import grupocho.logisticsac.enums.ResultadoInspeccion;
import grupocho.logisticsac.modelo.Evidencia;
import grupocho.logisticsac.modelo.Inspeccion;
import grupocho.logisticsac.modelo.Traslado;
import grupocho.logisticsac.modelo.Usuario;
import grupocho.logisticsac.repository.EvidenciaRepository;
import grupocho.logisticsac.repository.InspeccionRepository;
import grupocho.logisticsac.repository.TrasladoRepository;
import grupocho.logisticsac.service.EvidenciaService;
import grupocho.logisticsac.service.InspeccionService;
import grupocho.logisticsac.service.TrasladoService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class InspeccionView {
    private final Usuario usuario;
    private final TrasladoService trasladoService;
    private final InspeccionService inspeccionService;
    private final EvidenciaService evidenciaService;

    public InspeccionView(Usuario usuario) {
        this.usuario = usuario;
        TrasladoRepository trasladoRepository = new TrasladoDAO();
        InspeccionRepository inspeccionRepository = new InspeccionDAO();
        EvidenciaRepository evidenciaRepository = new EvidenciaDAO();
        this.trasladoService = new TrasladoService(trasladoRepository);
        this.inspeccionService = new InspeccionService(inspeccionRepository);
        this.evidenciaService = new EvidenciaService(evidenciaRepository);
    }

    public void mostrar(Stage stage) {
        Label titulo = new Label("INSPECCIÓN DE TRASLADO");

        ComboBox<Traslado> cmbTraslado = new ComboBox<>();
        cmbTraslado.setPromptText("Seleccione traslado");

        RadioButton rbConforme = new RadioButton("Carga conforme");
        RadioButton rbNoConforme = new RadioButton("Carga no conforme");

        ToggleGroup grupoCarga = new ToggleGroup();
        rbConforme.setToggleGroup(grupoCarga);
        rbNoConforme.setToggleGroup(grupoCarga);

        TextField txtObservacion = new TextField();
        txtObservacion.setPromptText("Observación");

        TextField txtEvidencia = new TextField();
        txtEvidencia.setPromptText("Ruta de evidencia");

        Button btnRegistrar = new Button("Registrar inspección");
        Button btnVolver = new Button("Volver");

        Label mensaje = new Label();

        try {
            List<Traslado> traslados = trasladoService.listar()
                    .stream()
                    .filter(t -> t.getEstado() == EstadoTraslado.PROGRAMADO)
                    .toList();

            cmbTraslado.setItems(FXCollections.observableArrayList(traslados));
        } catch (SQLException e) {
            mensaje.setText("No se pudieron cargar los traslados.");
        }

        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);
        formulario.setPadding(new Insets(20));

        formulario.add(new Label("Traslado:"), 0, 0);
        formulario.add(cmbTraslado, 1, 0);

        formulario.add(new Label("Resultado:"), 0, 1);
        formulario.add(rbConforme, 1, 1);
        formulario.add(rbNoConforme, 2, 1);

        formulario.add(new Label("Observación:"), 0, 2);
        formulario.add(txtObservacion, 1, 2);

        formulario.add(new Label("Evidencia:"), 0, 3);
        formulario.add(txtEvidencia, 1, 3);

        btnRegistrar.setOnAction(event -> {
            try {
                Traslado traslado = cmbTraslado.getValue();

                if (traslado == null) {
                    mensaje.setText("Seleccione un traslado.");
                    return;
                }

                if (!rbConforme.isSelected() && !rbNoConforme.isSelected()) {
                    mensaje.setText("Seleccione el resultado de la inspección.");
                    return;
                }

                boolean cargaConforme = rbConforme.isSelected();
                ResultadoInspeccion resultado = cargaConforme
                        ? ResultadoInspeccion.CONFORME
                        : ResultadoInspeccion.NO_CONFORME;

                if (!cargaConforme && txtObservacion.getText().isBlank()) {
                    mensaje.setText("La observación es obligatoria cuando la carga no es conforme.");
                    return;
                }

                if (cargaConforme && txtEvidencia.getText().isBlank()) {
                    mensaje.setText("La evidencia es obligatoria cuando la inspección es conforme.");
                    return;
                }

                Inspeccion inspeccion = new Inspeccion();
                inspeccion.setFechaHora(LocalDateTime.now());
                inspeccion.setResultado(resultado);
                inspeccion.setCargaConforme(cargaConforme);
                inspeccion.setObservacion(txtObservacion.getText());
                inspeccion.setTraslado(traslado);
                inspeccion.setVigilante(usuario);

                inspeccionService.registrar(inspeccion);

                if (cargaConforme) {
                    Evidencia evidencia = new Evidencia();
                    evidencia.setRutaArchivo(txtEvidencia.getText());
                    evidencia.setDescripcion("Evidencia de inspección");
                    evidencia.setFechaHoraRegistro(LocalDateTime.now());
                    evidencia.setInspeccion(inspeccion);
                    evidenciaService.registrar(evidencia);
                }

                mensaje.setText("Inspección registrada correctamente.");
                cmbTraslado.getItems().remove(traslado);
                cmbTraslado.setValue(null);
                rbConforme.setSelected(false);
                rbNoConforme.setSelected(false);
                txtObservacion.clear();
                txtEvidencia.clear();

            } catch (IllegalArgumentException | IllegalStateException e) {
                mensaje.setText(e.getMessage());
            } catch (SQLException e) {
                mensaje.setText("Error al registrar la inspección.");
            }
        });

        btnVolver.setOnAction(event -> {
            DashboardView dashboardView = new DashboardView(usuario);
            dashboardView.mostrar(stage);
        });

        VBox layout = new VBox(
                15,
                titulo,
                formulario,
                btnRegistrar,
                mensaje,
                btnVolver
        );

        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 700, 500);

        stage.setTitle("Logistic S.A.C. - Inspección");
        stage.setScene(scene);
        stage.show();
    }
}