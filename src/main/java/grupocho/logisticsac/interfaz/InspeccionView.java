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
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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

    public InspeccionView(Usuario usuario) {
        this.usuario = usuario;
        TrasladoRepository trasladoRepository = new TrasladoDAO();
        DocumentoRepository documentoRepository = new DocumentoDAO();
        this.trasladoService = new TrasladoService(trasladoRepository, documentoRepository);
        this.inspeccionService = new InspeccionService(new InspeccionDAO(), new EvidenciaDAO(), new PrecintoDAO());
        this.documentoService = new DocumentoService(documentoRepository, new TipoDocumentoDAO());
    }

    public void mostrar(Stage stage) {
        Label titulo = new Label("CONTROL DE SALIDA EN GARITA - INSPECCIÓN");

        ComboBox<Traslado> cmbTraslado = new ComboBox<>();
        cmbTraslado.setPromptText("Seleccione traslado");

        Label lblTraslado = new Label("Traslado: -");

        // verificacion documentaria
        TableView<Documento> tablaDocumentos = TablaDocumentos.crear();
        Label lblDocumentos = new Label("Documentos: -");

        // detalle de la carga que debe salir
        ListView<String> listaCarga = new ListView<>();
        listaCarga.setPrefHeight(90);

        RadioButton rbConforme = new RadioButton("Vehículo conforme");
        RadioButton rbNoConforme = new RadioButton("Vehículo no conforme");

        ToggleGroup grupoVehiculo = new ToggleGroup();
        rbConforme.setToggleGroup(grupoVehiculo);
        rbNoConforme.setToggleGroup(grupoVehiculo);

        CheckBox chkCarga = new CheckBox("La carga coincide con el detalle del traslado");

        TextField txtPrecinto = new TextField();
        txtPrecinto.setPromptText("Número de precinto");

        TextField txtObservacion = new TextField();
        txtObservacion.setPromptText("Observación");

        TextField txtDescripcion = new TextField();
        txtDescripcion.setPromptText("Descripción de la foto");

        Button btnAdjuntar = new Button("Adjuntar foto");

        List<Evidencia> evidencias = new ArrayList<>();
        ListView<String> listaEvidencias = new ListView<>();
        listaEvidencias.setPrefHeight(70);

        Button btnRegistrar = new Button("Registrar inspección");
        Button btnVolver = new Button("Volver");

        Label mensaje = new Label();

        cargarTraslados(cmbTraslado, mensaje);

        cmbTraslado.setOnAction(event -> {
            Traslado traslado = cmbTraslado.getValue();

            tablaDocumentos.getItems().clear();
            listaCarga.getItems().clear();

            if (traslado == null) {
                lblTraslado.setText("Traslado: -");
                lblDocumentos.setText("Documentos: -");
                return;
            }

            lblTraslado.setText("Vehículo: " + traslado.getVehiculo()
                    + " | Conductor: " + traslado.getConductor()
                    + " | " + traslado.getAlmacenOrigen().getNombre()
                    + " -> " + traslado.getAlmacenDestino().getNombre());

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
                    lblDocumentos.setText("Documentos: todos los obligatorios están VIGENTES.");
                } else {
                    lblDocumentos.setText("Documentos: TRASLADO CON OBSERVACIONES (" + observados
                            + " faltante(s) o vencido(s)). No podrá autorizarse la salida.");
                }

                for (DetalleTraslado detalle : trasladoService.listarDetalles(traslado)) {
                    listaCarga.getItems().add(detalle.getProducto().getDescripcion()
                            + " - " + detalle.getCantidad()
                            + " " + detalle.getProducto().getUnidadMedida());
                }
            } catch (SQLException e) {
                mensaje.setText("No se pudo cargar la información del traslado.");
            }
        });

        btnAdjuntar.setOnAction(event -> {
            FileChooser selector = new FileChooser();
            selector.setTitle("Seleccione la foto de evidencia");
            selector.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Imágenes", "*.jpg", "*.jpeg", "*.png"));

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
            listaEvidencias.getItems().add(archivo.getName() + " - " + descripcion);
            txtDescripcion.clear();
        });

        btnRegistrar.setOnAction(event -> {
            try {
                Traslado traslado = cmbTraslado.getValue();

                if (traslado == null) {
                    mensaje.setText("Seleccione un traslado.");
                    return;
                }

                if (!rbConforme.isSelected() && !rbNoConforme.isSelected()) {
                    mensaje.setText("Seleccione el resultado de la inspección del vehículo.");
                    return;
                }

                boolean conforme = rbConforme.isSelected() && chkCarga.isSelected();

                if (!chkCarga.isSelected() && txtObservacion.getText().isBlank()) {
                    mensaje.setText("La carga es NO CONFORME: describa la diferencia en la observación.");
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
                inspeccion.setResultado(rbConforme.isSelected()
                        ? ResultadoInspeccion.CONFORME
                        : ResultadoInspeccion.NO_CONFORME);
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

                mensaje.setText("Inspección registrada (" + inspeccion.getFechaHora().toLocalDate()
                        + " " + inspeccion.getFechaHora().toLocalTime().withNano(0)
                        + ", " + usuario.getUsername()
                        + "). Continúe en \"Autorización / rechazo\".");

                cmbTraslado.getItems().remove(traslado);
                cmbTraslado.setValue(null);
                grupoVehiculo.selectToggle(null);
                chkCarga.setSelected(false);
                txtPrecinto.clear();
                txtObservacion.clear();
                evidencias.clear();
                listaEvidencias.getItems().clear();

            } catch (IllegalArgumentException | IllegalStateException e) {
                mensaje.setText(e.getMessage());
            } catch (IOException e) {
                mensaje.setText("No se pudo guardar la foto de evidencia.");
            } catch (SQLException e) {
                mensaje.setText("Error al registrar la inspección: " + e.getMessage());
            }
        });

        btnVolver.setOnAction(event -> {
            DashboardView dashboardView = new DashboardView(usuario);
            dashboardView.mostrar(stage);
        });

        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);

        formulario.add(new Label("Inspección física:"), 0, 0);
        formulario.add(new HBox(15, rbConforme, rbNoConforme), 1, 0);

        formulario.add(new Label("Carga:"), 0, 1);
        formulario.add(chkCarga, 1, 1);

        formulario.add(new Label("Precinto:"), 0, 2);
        formulario.add(txtPrecinto, 1, 2);

        formulario.add(new Label("Observación:"), 0, 3);
        formulario.add(txtObservacion, 1, 3);

        formulario.add(new Label("Evidencia:"), 0, 4);
        formulario.add(new HBox(10, txtDescripcion, btnAdjuntar), 1, 4);

        VBox layout = new VBox(
                8,
                titulo,
                new HBox(10, new Label("Traslado:"), cmbTraslado),
                lblTraslado,
                new Label("Documentos obligatorios:"),
                tablaDocumentos,
                lblDocumentos,
                new Label("Carga según el detalle del traslado:"),
                listaCarga,
                formulario,
                listaEvidencias,
                btnRegistrar,
                mensaje,
                btnVolver
        );

        layout.setPadding(new Insets(15));

        Scene scene = new Scene(layout, 850, 700);

        stage.setTitle("Logistic S.A.C. - Inspección");
        stage.setScene(scene);
        stage.show();
        stage.centerOnScreen();
    }

    // solo se muestran los traslados programados que todavia no tienen inspeccion
    private void cargarTraslados(ComboBox<Traslado> cmbTraslado, Label mensaje) {
        try {
            List<Traslado> pendientes = new ArrayList<>();

            for (Traslado traslado : trasladoService.listar()) {
                if (traslado.getEstado() == EstadoTraslado.PROGRAMADO && traslado.getVigilante() == null) {
                    pendientes.add(traslado);
                }
            }

            cmbTraslado.setItems(FXCollections.observableArrayList(pendientes));
        } catch (SQLException e) {
            mensaje.setText("No se pudieron cargar los traslados.");
        }
    }

    // copia la foto a la carpeta de evidencias y devuelve la ruta que se guarda en la base de datos
    private String guardarArchivo(String rutaOrigen, Traslado traslado) throws IOException {
        Path origen = Paths.get(rutaOrigen);
        Path carpeta = Paths.get(CARPETA_EVIDENCIAS);
        Files.createDirectories(carpeta);

        String nombre = traslado.getCodigo() + "_" + System.currentTimeMillis() + "_" + origen.getFileName();
        Path destino = carpeta.resolve(nombre);

        Files.copy(origen, destino, StandardCopyOption.REPLACE_EXISTING);

        return CARPETA_EVIDENCIAS + "/" + nombre;
    }
}
