
package grupocho.logisticsac.interfaz;

import grupocho.logisticsac.dao.DocumentoDAO;
import grupocho.logisticsac.dao.EvidenciaDAO;
import grupocho.logisticsac.dao.InspeccionDAO;
import grupocho.logisticsac.dao.PrecintoDAO;
import grupocho.logisticsac.dao.TipoDocumentoDAO;
import grupocho.logisticsac.dao.TrasladoDAO;
import grupocho.logisticsac.enums.EstadoTraslado;
import grupocho.logisticsac.enums.ResultadoInspeccion;
import grupocho.logisticsac.modelo.DetalleTraslado;
import grupocho.logisticsac.modelo.Documento;
import grupocho.logisticsac.modelo.Evidencia;
import grupocho.logisticsac.modelo.Inspeccion;
import grupocho.logisticsac.modelo.Precinto;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InspeccionView {
    // carpeta del equipo donde se guardan las fotos de evidencia
    private static final String CARPETA_EVIDENCIAS = "evidencia";

    private final Usuario usuario;
    private final TrasladoService trasladoService;
    private final InspeccionService inspeccionService;
    private final DocumentoService documentoService;
    private final DashboardLayout dashboardLayout;

    public InspeccionView(Usuario usuario) {
        this(usuario, null);
    }

    public InspeccionView(Usuario usuario, DashboardLayout dashboardLayout) {
        this.usuario = usuario;
        this.dashboardLayout = dashboardLayout;

        TrasladoRepository trasladoRepository = new TrasladoDAO();
        DocumentoRepository documentoRepository = new DocumentoDAO();

        this.trasladoService = new TrasladoService(trasladoRepository, documentoRepository);
        this.inspeccionService = new InspeccionService(
                new InspeccionDAO(),
                new EvidenciaDAO(),
                new PrecintoDAO()
        );
        this.documentoService = new DocumentoService(
                documentoRepository,
                new TipoDocumentoDAO()
        );
    }

    public void mostrar(Stage stage) {
        ComboBox<Traslado> cmbTraslado = new ComboBox<>();
        cmbTraslado.setPromptText("Seleccione un traslado pendiente");
        cmbTraslado.setMaxWidth(Double.MAX_VALUE);

        Label lblTraslado = new Label("Seleccione un traslado para ver sus datos.");
        lblTraslado.setWrapText(true);
        lblTraslado.getStyleClass().add("inspeccion-informacion");

        // verificacion documentaria
        TableView<Documento> tablaDocumentos = TablaDocumentos.crear();
        tablaDocumentos.setPrefHeight(160);
        tablaDocumentos.setMinHeight(120);

        Label lblDocumentos = new Label("Documentos: pendientes de revisión.");
        lblDocumentos.setWrapText(true);

        // Detalle de la carga
        ListView<String> listaCarga = new ListView<>();
        listaCarga.setPrefHeight(120);
        listaCarga.setPlaceholder(new Label("Sin productos para mostrar"));

        RadioButton rbConforme = new RadioButton("Vehículo conforme");
        RadioButton rbNoConforme = new RadioButton("Vehículo no conforme");

        ToggleGroup grupoVehiculo = new ToggleGroup();
        rbConforme.setToggleGroup(grupoVehiculo);
        rbNoConforme.setToggleGroup(grupoVehiculo);

        CheckBox chkCarga = new CheckBox("La carga coincide con el detalle del traslado");
        chkCarga.setWrapText(true);

        TextField txtPrecinto = new TextField();
        txtPrecinto.setPromptText("Número de precinto");

        TextField txtObservacion = new TextField();
        txtObservacion.setPromptText("Describa las observaciones");

        TextField txtDescripcion = new TextField();
        txtDescripcion.setPromptText("Descripción de la foto");

        Button btnAdjuntar = new Button("Adjuntar foto");

        List<Evidencia> evidencias = new ArrayList<>();
        ListView<String> listaEvidencias = new ListView<>();
        listaEvidencias.setPrefHeight(100);
        listaEvidencias.setPlaceholder(new Label("Todavía no se adjuntaron evidencias"));

        // Acciones y mensajes
        Button btnRegistrar = new Button("Registrar inspección");
        btnRegistrar.getStyleClass().add("inspeccion-registrar");

        Button btnVolver = new Button("Volver");
        btnVolver.getStyleClass().add("inspeccion-volver");

        Label mensaje = new Label();
        mensaje.setWrapText(true);
        mensaje.getStyleClass().add("inspeccion-mensaje");
        cargarTraslados(cmbTraslado, mensaje);

        cmbTraslado.setOnAction(event -> {
            Traslado traslado = cmbTraslado.getValue();

            tablaDocumentos.getItems().clear();
            listaCarga.getItems().clear();
            lblDocumentos.setText("Documentos: pendientes de revisión.");

            if (traslado == null) {
                lblTraslado.setText("Seleccione un traslado para ver sus datos.");
                return;
            }

            lblTraslado.setText("Vehículo: " + traslado.getVehiculo()
                            + "\nConductor: " + traslado.getConductor()
                            + "\nOrigen: "
                            + traslado.getAlmacenOrigen().getNombre()
                            + "\nDestino: "
                            + traslado.getAlmacenDestino().getNombre()
            );

            try {
                List<Documento> documentos = documentoService.verificarDocumentos(traslado);

                tablaDocumentos.setItems(FXCollections.observableArrayList(documentos));

                int observados = 0;
                for (Documento documento : documentos) {
                    if (!documento.estaVigente()) {
                        observados++;
                    }
                }

                if (observados == 0) {
                    lblDocumentos.setText("Todos los documentos obligatorios están vigentes.");
                } else {
                    lblDocumentos.setText("Traslado con observaciones: " + observados + " documento(s) faltante(s) o vencido(s).");
                }

                for (DetalleTraslado detalle : trasladoService.listarDetalles(traslado)) {
                    listaCarga.getItems().add( detalle.getProducto().getDescripcion() + " - " + detalle.getCantidad() + " " + detalle.getProducto().getUnidadMedida());
                }

                mensaje.setText("");

            } catch (SQLException e) {
                mensaje.setText("No se pudo cargar la información del traslado."
                );
            }
        });

        // Adjuntar foto de evidencia
        btnAdjuntar.setOnAction(event -> {
            FileChooser selector = new FileChooser();
            selector.setTitle("Seleccione la foto de evidencia");

            selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.jpg", "*.jpeg", "*.png"));

            File archivo = selector.showOpenDialog(stage);

            if (archivo == null) {
                return;
            }

            String descripcion = txtDescripcion.getText().isBlank()
                    ? "Evidencia de inspección"
                    : txtDescripcion.getText().trim();

            Evidencia evidencia = new Evidencia();
            evidencia.setRutaArchivo(archivo.getAbsolutePath());
            evidencia.setDescripcion(descripcion);

            evidencias.add(evidencia);

            listaEvidencias.getItems().add(
                    archivo.getName() + " - " + descripcion
            );

            txtDescripcion.clear();
            mensaje.setText("");
        });

        // Registrar inspección
        btnRegistrar.setOnAction(event -> {
            try {
                Traslado traslado = cmbTraslado.getValue();

                if (traslado == null) {
                    mensaje.setText("Seleccione un traslado.");
                    return;
                }

                if (!rbConforme.isSelected()
                        && !rbNoConforme.isSelected()) {
                    mensaje.setText("Seleccione el resultado de la inspección del vehículo.");
                    return;
                }

                boolean conforme = rbConforme.isSelected() && chkCarga.isSelected();

                if (!chkCarga.isSelected() && txtObservacion.getText().isBlank()) {
                    mensaje.setText("La carga no es conforme: describa la diferencia.");
                    return;
                }

                if (!conforme && txtObservacion.getText().isBlank()) {
                    mensaje.setText("La observación es obligatoria cuando la inspección no es conforme.");
                    return;
                }

                if (conforme && txtPrecinto.getText().isBlank()) {
                    mensaje.setText("Ingrese el número de precinto.");
                    return;
                }

                if (conforme && evidencias.isEmpty()) {
                    mensaje.setText("Adjunte al menos una foto de evidencia.");
                    return;
                }

                Inspeccion inspeccion = new Inspeccion(traslado, usuario);

                inspeccion.setResultado(
                                rbConforme.isSelected()
                                ? ResultadoInspeccion.CONFORME
                                : ResultadoInspeccion.NO_CONFORME
                );

                inspeccion.setCargaConforme(chkCarga.isSelected());

                inspeccion.setObservacion(txtObservacion.getText().isBlank() ? null : txtObservacion.getText().trim());

                for (Evidencia evidencia : evidencias) {
                    evidencia.setRutaArchivo(guardarArchivo(evidencia.getRutaArchivo(), traslado));

                    evidencia.setFechaHoraRegistro(inspeccion.getFechaHora());

                    evidencia.setInspeccion(inspeccion);
                    inspeccion.agregarEvidencia(evidencia);
                }

                Precinto precinto = null;

                if (!txtPrecinto.getText().isBlank()) {
                    precinto = new Precinto(txtPrecinto.getText().trim(), traslado);
                }

                inspeccionService.registrar(inspeccion, precinto);

                mensaje.setText(
                        "Inspección registrada ("
                                + inspeccion.getFechaHora().toLocalDate()
                                + " "
                                + inspeccion.getFechaHora()
                                .toLocalTime().withNano(0)
                                + ", " + usuario.getUsername()
                                + "). Continúe en Autorización / rechazo."
                );

                cmbTraslado.getItems().remove(traslado);
                cmbTraslado.setValue(null);
                grupoVehiculo.selectToggle(null);
                chkCarga.setSelected(false);
                txtPrecinto.clear();
                txtObservacion.clear();
                txtDescripcion.clear();
                evidencias.clear();
                listaEvidencias.getItems().clear();
                tablaDocumentos.getItems().clear();
                listaCarga.getItems().clear();
                lblTraslado.setText("Seleccione un traslado para ver sus datos.");
                lblDocumentos.setText("Documentos: pendientes de revisión.");

            } catch (IllegalArgumentException | IllegalStateException e) {
                mensaje.setText(e.getMessage());

            } catch (IOException e) {
                mensaje.setText("No se pudo guardar la foto de evidencia.");

            } catch (SQLException e) {
                mensaje.setText("Error al registrar la inspección: " + e.getMessage());
            }
        });

        // Volver al panel principal sin cerrar el dashboard
        btnVolver.setOnAction(event -> {
            if (dashboardLayout != null) {
                dashboardLayout.mostrarContenido("Panel principal", null);
            } else {
                new DashboardView(usuario).mostrar(stage);
            }
        });

        // Panel superior: selección e información del traslado
        VBox panelTraslado = new VBox(
                10,
                new Label("TRASLADO PENDIENTE"),
                cmbTraslado,
                lblTraslado
        );
        panelTraslado.getStyleClass().add("inspeccion-panel");

        // Panel de revisión documental
        VBox panelDocumentos = new VBox(
                10,
                new Label("VERIFICACIÓN DOCUMENTARIA"),
                tablaDocumentos,
                lblDocumentos
        );
        panelDocumentos.getStyleClass().add("inspeccion-panel");

        HBox opcionesVehiculo = new HBox(
                18, rbConforme, rbNoConforme
        );

        VBox panelCarga = new VBox(
                10,
                new Label("DETALLE DE LA CARGA"),
                listaCarga
        );
        panelCarga.getStyleClass().add("inspeccion-panel");

        VBox panelInspeccion = new VBox(
                12,
                new Label("INSPECCIÓN FÍSICA"),
                new Label("Estado del vehículo"),
                opcionesVehiculo,
                new Label("Verificación de la carga"),
                chkCarga,
                new Label("Número de precinto"),
                txtPrecinto,
                new Label("Observaciones"),
                txtObservacion
        );
        panelInspeccion.getStyleClass().add("inspeccion-panel");

        // Panel de evidencias
        HBox filaEvidencia = new HBox(10, txtDescripcion, btnAdjuntar);
        HBox.setHgrow(txtDescripcion, Priority.ALWAYS);

        VBox panelEvidencias = new VBox(
                10,
                new Label("EVIDENCIAS FOTOGRÁFICAS"),
                filaEvidencia,
                listaEvidencias
        );
        panelEvidencias.getStyleClass().add("inspeccion-panel");

        VBox columnaIzquierda = new VBox(
                16,
                panelTraslado,
                panelDocumentos
        );
        columnaIzquierda.setPrefWidth(430);
        columnaIzquierda.setMinWidth(300);

        VBox columnaDerecha = new VBox(
                16,
                panelCarga,
                panelInspeccion,
                panelEvidencias
        );
        columnaDerecha.setMinWidth(0);

        HBox cuerpo = new HBox(
                16, columnaIzquierda, columnaDerecha
        );
        HBox.setHgrow(columnaDerecha, Priority.ALWAYS);

        HBox acciones = new HBox(10, btnRegistrar, btnVolver);
        acciones.setPadding(new Insets(4, 0, 4, 0));

        VBox layout = new VBox(
                16,
                cuerpo,
                mensaje,
                acciones
        );
        layout.setPadding(new Insets(16));
        layout.getStyleClass().add("inspeccion-contenedor");

        if (dashboardLayout != null) {

            Scene scene = stage.getScene();
            var css = getClass().getResource("/style/inspeccion.css");

            if (css != null && !scene.getStylesheets().contains( css.toExternalForm())) {
                scene.getStylesheets().add(css.toExternalForm());
            }

            ScrollPane scroll = new ScrollPane(layout);
            scroll.setFitToWidth(true);
            scroll.setPannable(true);
            scroll.getStyleClass().add("inspeccion-scroll");

            dashboardLayout.mostrarContenido("Inspección y salida",scroll);

        } else {

            ScrollPane scroll = new ScrollPane(layout);
            scroll.setFitToWidth(true);
            scroll.setPannable(true);

            Scene scene = new Scene(scroll, 1100, 750);

            var css = getClass().getResource("/style/inspeccion.css");

            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }

            stage.setTitle("Logistic S.A.C. - Inspección");
            stage.setScene(scene);
            stage.show();
            stage.centerOnScreen();
        }
    }

    // Carga los traslados que aún no tienen inspección.
    private void cargarTraslados(
            ComboBox<Traslado> cmbTraslado,
            Label mensaje
    ) {
        try {
            List<Traslado> pendientes = new ArrayList<>();

            for (Traslado traslado : trasladoService.listar()) {
                if (traslado.getEstado() == EstadoTraslado.PROGRAMADO
                        && traslado.getVigilante() == null) {
                    pendientes.add(traslado);
                }
            }

            cmbTraslado.setItems(
                    FXCollections.observableArrayList(pendientes)
            );

        } catch (SQLException e) {
            mensaje.setText("No se pudieron cargar los traslados.");
        }
    }

    // Copia la foto a la carpeta de evidencias y devuelve su ruta.
    private String guardarArchivo(
            String rutaOrigen,
            Traslado traslado
    ) throws IOException {

        Path origen = Paths.get(rutaOrigen);
        Path carpeta = Paths.get(CARPETA_EVIDENCIAS);

        Files.createDirectories(carpeta);

        String nombre = traslado.getCodigo()
                + "_" + System.currentTimeMillis()
                + "_" + origen.getFileName();

        Path destino = carpeta.resolve(nombre);

        Files.copy(
                origen,
                destino,
                StandardCopyOption.REPLACE_EXISTING
        );

        return CARPETA_EVIDENCIAS + "/" + nombre;
    }
}
