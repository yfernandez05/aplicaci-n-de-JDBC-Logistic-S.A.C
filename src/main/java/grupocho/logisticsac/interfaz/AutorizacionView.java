package grupocho.logisticsac.interfaz;

import grupocho.logisticsac.dao.DocumentoDAO;
import grupocho.logisticsac.dao.EvidenciaDAO;
import grupocho.logisticsac.dao.InspeccionDAO;
import grupocho.logisticsac.dao.PrecintoDAO;
import grupocho.logisticsac.dao.TipoDocumentoDAO;
import grupocho.logisticsac.dao.TrasladoDAO;
import grupocho.logisticsac.enums.EstadoTraslado;
import grupocho.logisticsac.modelo.Documento;
import grupocho.logisticsac.modelo.Inspeccion;
import grupocho.logisticsac.modelo.Traslado;
import grupocho.logisticsac.modelo.Usuario;
import grupocho.logisticsac.repository.DocumentoRepository;
import grupocho.logisticsac.repository.TrasladoRepository;
import grupocho.logisticsac.service.DocumentoService;
import grupocho.logisticsac.service.InspeccionService;
import grupocho.logisticsac.service.TrasladoService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AutorizacionView {
    private final Usuario usuario;
    private final TrasladoService trasladoService;
    private final InspeccionService inspeccionService;
    private final DocumentoService documentoService;

    public AutorizacionView(Usuario usuario) {
        this.usuario = usuario;
        TrasladoRepository trasladoRepository = new TrasladoDAO();
        DocumentoRepository documentoRepository = new DocumentoDAO();
        this.trasladoService = new TrasladoService(trasladoRepository, documentoRepository);
        this.inspeccionService = new InspeccionService(new InspeccionDAO(), new EvidenciaDAO(), new PrecintoDAO());
        this.documentoService = new DocumentoService(documentoRepository, new TipoDocumentoDAO());
    }

    public void mostrar(Stage stage) {
        Label titulo = new Label("AUTORIZACIÓN DE SALIDA");

        ComboBox<Traslado> cmbTraslado = new ComboBox<>();
        cmbTraslado.setPromptText("Seleccione traslado");

        TableView<Documento> tablaDocumentos = TablaDocumentos.crear();

        Label lblDocumentos = new Label("Documentos: -");
        Label lblInspeccion = new Label("Inspección: -");

        TextArea txtMotivo = new TextArea();
        txtMotivo.setPromptText("Motivo de rechazo");
        txtMotivo.setPrefRowCount(3);

        Button btnAutorizar = new Button("Autorizar salida");
        Button btnRechazar = new Button("Rechazar");
        Button btnVolver = new Button("Volver");
        Label mensaje = new Label();
        mensaje.setWrapText(true);

        btnAutorizar.setDisable(true);

        try {
            List<Traslado> traslados = trasladoService.listar().stream()
                    .filter(t -> t.getEstado() == EstadoTraslado.PROGRAMADO)
                    .toList();

            cmbTraslado.setItems(FXCollections.observableArrayList(traslados));
        } catch (SQLException e) {
            mensaje.setText("No se pudieron cargar los traslados.");
        }

        // lista de verificacion e inspeccion del traslado seleccionado
        List<Documento> documentos = new ArrayList<>();
        Inspeccion[] inspeccionActual = new Inspeccion[1];

        cmbTraslado.setOnAction(event -> {
            Traslado traslado = cmbTraslado.getValue();

            documentos.clear();
            inspeccionActual[0] = null;
            tablaDocumentos.getItems().clear();
            btnAutorizar.setDisable(true);
            mensaje.setText("");

            if (traslado == null) {
                lblDocumentos.setText("Documentos: -");
                lblInspeccion.setText("Inspección: -");
                return;
            }

            try {
                documentos.addAll(documentoService.verificarDocumentos(traslado));
                tablaDocumentos.setItems(FXCollections.observableArrayList(documentos));

                boolean documentosConformes = true;
                for (Documento documento : documentos) {
                    if (!documento.estaVigente()) {
                        documentosConformes = false;
                    }
                }

                lblDocumentos.setText(documentosConformes
                        ? "Documentos: CONFORMES"
                        : "Documentos: CON OBSERVACIONES (faltantes o vencidos)");

                Inspeccion inspeccion = inspeccionService.buscarPorTraslado(traslado.getIdTraslado());
                inspeccionActual[0] = inspeccion;

                if (inspeccion == null) {
                    lblInspeccion.setText("Inspección: SIN REGISTRAR");
                } else {
                    lblInspeccion.setText("Inspección: " + inspeccion.getResultado()
                            + " | Carga: " + (inspeccion.isCargaConforme() ? "CONFORME" : "NO CONFORME")
                            + " | Evidencias: " + inspeccion.getEvidencias().size()
                            + " | Vigilante: " + inspeccion.getVigilante().getNombreCompleto()
                            + (inspeccion.getObservacion() != null ? " | Obs.: " + inspeccion.getObservacion() : ""));
                }

                // solo se habilita autorizar cuando documentos e inspeccion estan conformes
                boolean puedeAutorizar = documentosConformes && inspeccion != null && inspeccion.puedeAutorizar();
                btnAutorizar.setDisable(!puedeAutorizar);

                if (!puedeAutorizar) {
                    mensaje.setText("La autorización está bloqueada. Solo puede rechazar la salida indicando el motivo.");
                }
            } catch (SQLException e) {
                mensaje.setText("No se pudo cargar la información del traslado.");
            }
        });

        btnAutorizar.setOnAction(event -> {
            try {
                Traslado traslado = cmbTraslado.getValue();

                if (traslado == null) {
                    mensaje.setText("Seleccione un traslado.");
                    return;
                }

                if (inspeccionActual[0] == null) {
                    mensaje.setText("El traslado no tiene una inspección registrada.");
                    return;
                }

                trasladoService.autorizarSalida(traslado, usuario, inspeccionActual[0], documentos);

                cmbTraslado.getItems().remove(traslado);
                cmbTraslado.setValue(null);

                mensaje.setText("Salida autorizada. Traslado " + traslado.getCodigo()
                        + " EN_TRANSITO desde " + traslado.getFechaHoraSalida().toLocalDate()
                        + " " + traslado.getFechaHoraSalida().toLocalTime().withNano(0)
                        + " por " + usuario.getUsername() + ".");

            } catch (IllegalArgumentException | IllegalStateException e) {
                mensaje.setText(e.getMessage());
            } catch (SQLException e) {
                mensaje.setText("Error al autorizar la salida.");
            }
        });

        btnRechazar.setOnAction(event -> {
            try {
                Traslado traslado = cmbTraslado.getValue();

                if (traslado == null) {
                    mensaje.setText("Seleccione un traslado.");
                    return;
                }

                if (txtMotivo.getText().isBlank()) {
                    mensaje.setText("Ingrese el motivo de rechazo.");
                    return;
                }

                trasladoService.rechazar(traslado, txtMotivo.getText().trim(), usuario);

                cmbTraslado.getItems().remove(traslado);
                cmbTraslado.setValue(null);
                txtMotivo.clear();

                mensaje.setText("Traslado " + traslado.getCodigo() + " RECHAZADO por " + usuario.getUsername() + ".");

            } catch (IllegalArgumentException | IllegalStateException e) {
                mensaje.setText(e.getMessage());
            } catch (SQLException e) {
                mensaje.setText("Error al rechazar el traslado.");
            }
        });

        btnVolver.setOnAction(event -> {
            DashboardView dashboardView = new DashboardView(usuario);
            dashboardView.mostrar(stage);
        });

        VBox layout = new VBox(
                10,
                titulo,
                new HBox(10, new Label("Traslado:"), cmbTraslado),
                new Label("Documentos obligatorios:"),
                tablaDocumentos,
                lblDocumentos,
                lblInspeccion,
                new Label("Motivo (solo para rechazar):"),
                txtMotivo,
                new HBox(10, btnAutorizar, btnRechazar),
                mensaje,
                btnVolver
        );
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 850, 600);
        stage.setTitle("Logistic S.A.C. - Autorización de salida");
        stage.setScene(scene);
        stage.show();
        stage.centerOnScreen();
    }
}
