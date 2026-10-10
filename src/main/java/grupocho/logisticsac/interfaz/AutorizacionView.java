
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
import javafx.scene.layout.Priority;
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
    private final DashboardLayout dashboardLayout;

    public AutorizacionView(Usuario usuario) {
        this(usuario, null);
    }

    public AutorizacionView(Usuario usuario,DashboardLayout dashboardLayout) {
        this.usuario = usuario;
        this.dashboardLayout = dashboardLayout;
        TrasladoRepository trasladoRepository = new TrasladoDAO();
        DocumentoRepository documentoRepository = new DocumentoDAO();
        this.trasladoService = new TrasladoService(trasladoRepository, documentoRepository);
        this.inspeccionService = new InspeccionService(new InspeccionDAO(), new EvidenciaDAO(), new PrecintoDAO());
        this.documentoService = new DocumentoService(documentoRepository, new TipoDocumentoDAO());
    }

    public void mostrar(Stage stage) {

        ComboBox<Traslado> cmbTraslado = new ComboBox<>();
        cmbTraslado.setPromptText("Seleccione un traslado");
        cmbTraslado.setMaxWidth(Double.MAX_VALUE);
        Label lblDocumentos = new Label("Documentos: pendiente de revisión");
        Label lblInspeccion = new Label("Inspección: pendiente de revisión");
        lblDocumentos.getStyleClass().add("autorizacion-estado");
        lblInspeccion.getStyleClass().add("autorizacion-estado");

        TableView<Documento> tablaDocumentos = TablaDocumentos.crear();
        tablaDocumentos.setPrefHeight(190);
        tablaDocumentos.setMinHeight(130);

        TextArea txtMotivo = new TextArea();
        txtMotivo.setPromptText("Escriba el motivo del rechazo");
        txtMotivo.setPrefRowCount(3);
        txtMotivo.setWrapText(true);

        Button btnAutorizar = new Button("Autorizar salida");
        btnAutorizar.getStyleClass().add("autorizacion-aprobar");
        btnAutorizar.setDisable(true);

        Button btnRechazar = new Button("Rechazar salida");
        btnRechazar.getStyleClass().add("autorizacion-rechazar");

        Button btnVolver = new Button("Volver");
        btnVolver.getStyleClass().add("autorizacion-volver");

        Label mensaje = new Label();
        mensaje.setWrapText(true);
        mensaje.getStyleClass().add("autorizacion-mensaje");

        List<Documento> documentos = new ArrayList<>();
        Inspeccion[] inspeccionActual = new Inspeccion[1];


        try {
            List<Traslado> traslados = trasladoService.listar()
                    .stream()
                    .filter(t -> t.getEstado() == EstadoTraslado.PROGRAMADO)
                    .toList();

            cmbTraslado.setItems(FXCollections.observableArrayList(traslados));

        } catch (SQLException e) {
            mensaje.setText("No se pudieron cargar los traslados.");
        }

        cmbTraslado.setOnAction(event -> {
            Traslado traslado = cmbTraslado.getValue();

            documentos.clear();
            inspeccionActual[0] = null;
            tablaDocumentos.getItems().clear();
            btnAutorizar.setDisable(true);
            mensaje.setText("");

            if (traslado == null) {
                lblDocumentos.setText("Documentos: pendiente de revisión");
                lblInspeccion.setText("Inspección: pendiente de revisión");
                return;
            }

            try {
                documentos.addAll(documentoService.verificarDocumentos(traslado));
                tablaDocumentos.setItems(FXCollections.observableArrayList(documentos));
                boolean documentosConformes = documentos.stream().allMatch(Documento::estaVigente);
                lblDocumentos.setText(documentosConformes ? "Documentos conformes" : "Documentos con observaciones");
                Inspeccion inspeccion = inspeccionService.buscarPorTraslado(traslado.getIdTraslado());
                inspeccionActual[0] = inspeccion;

                if (inspeccion == null) {
                    lblInspeccion.setText("Inspección sin registrar");
                } else {
                    String detalleInspeccion =
                            "Resultado: " + inspeccion.getResultado()
                                    + " | Carga: "
                                    + (inspeccion.isCargaConforme()
                                    ? "Conforme" : "No conforme")
                                    + " | Evidencias: "
                                    + inspeccion.getEvidencias().size();

                    if (inspeccion.getVigilante() != null) {
                        detalleInspeccion += " | Vigilante: " + inspeccion.getVigilante().getNombreCompleto();
                    }

                    if (inspeccion.getObservacion() != null
                            && !inspeccion.getObservacion().isBlank()) {
                        detalleInspeccion += " | Observación: " + inspeccion.getObservacion();
                    }

                    lblInspeccion.setText(detalleInspeccion);
                }

                boolean puedeAutorizar = documentosConformes && inspeccion != null && inspeccion.puedeAutorizar();

                btnAutorizar.setDisable(!puedeAutorizar);

                if (!puedeAutorizar) {
                    mensaje.setText( "No se puede autorizar. Revise los documentos " + "y la inspección, o indique el motivo del rechazo.");
                }

            } catch (SQLException e) {
                mensaje.setText("No se pudo cargar la información del traslado.");
            }
        });

        // Autorizar salida.
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

                trasladoService.autorizarSalida(
                        traslado,
                        usuario,
                        inspeccionActual[0],
                        documentos
                );

                cmbTraslado.getItems().remove(traslado);
                cmbTraslado.setValue(null);
                txtMotivo.clear();

                mensaje.setText(
                        "Salida autorizada para el traslado "
                                + traslado.getCodigo() + "."
                );

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
                    mensaje.setText("Ingrese el motivo del rechazo.");
                    return;
                }

                trasladoService.rechazar(traslado,txtMotivo.getText().trim(),usuario);

                cmbTraslado.getItems().remove(traslado);
                cmbTraslado.setValue(null);
                txtMotivo.clear();

                mensaje.setText("Traslado " + traslado.getCodigo() + " rechazado correctamente.");

            } catch (IllegalArgumentException | IllegalStateException e) {
                mensaje.setText(e.getMessage());

            } catch (SQLException e) {
                mensaje.setText("Error al rechazar el traslado.");
            }
        });

        btnVolver.setOnAction(event -> {
            if (dashboardLayout != null) {
                new DashboardView(usuario).mostrarContenidoInicial(dashboardLayout);
            } else {
                new DashboardView(usuario).mostrar(stage);
            }
        });

        VBox panelTraslado = new VBox(
                10,
                new Label("TRASLADO"),
                cmbTraslado
        );
        panelTraslado.getStyleClass().add("autorizacion-panel");

        VBox panelDocumentos = new VBox(
                10,
                new Label("DOCUMENTOS OBLIGATORIOS"),
                tablaDocumentos,
                lblDocumentos
        );
        panelDocumentos.getStyleClass().add("autorizacion-panel");

        VBox panelInspeccion = new VBox(
                10,
                new Label("RESULTADO DE INSPECCIÓN"),
                lblInspeccion
        );
        panelInspeccion.getStyleClass().add("autorizacion-panel");

        VBox panelRechazo = new VBox(
                10,
                new Label("MOTIVO DE RECHAZO"),
                txtMotivo
        );
        panelRechazo.getStyleClass().add("autorizacion-panel");

        VBox contenido = new VBox(
                16,
                panelTraslado,
                panelDocumentos,
                panelInspeccion,
                panelRechazo,
                new HBox(10, btnAutorizar, btnRechazar),
                mensaje,
                btnVolver
        );

        contenido.setPadding(new Insets(10));
        contenido.getStyleClass().add("autorizacion-contenedor");

        if (dashboardLayout != null) {
            Scene scene = stage.getScene();
            var css = getClass().getResource("/style/autorizacion.css");

            if (css != null && !scene.getStylesheets().contains( css.toExternalForm())) {
                scene.getStylesheets().add(css.toExternalForm());
            }

            ScrollPane scroll = new ScrollPane(contenido);
            scroll.setFitToWidth(true);
            scroll.setPannable(true);
            scroll.getStyleClass().add("autorizacion-scroll");

            dashboardLayout.mostrarContenido("Autorización / rechazo",scroll);

        } else {
            ScrollPane scroll = new ScrollPane(contenido);
            scroll.setFitToWidth(true);
            scroll.setPannable(true);

            Scene scene = new Scene(scroll, 1000, 700);

            var css = getClass().getResource("/style/autorizacion.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }

            stage.setTitle("Logistic S.A.C. - Autorización de salida");
            stage.setScene(scene);
            stage.show();
            stage.centerOnScreen();
        }
    }
}
