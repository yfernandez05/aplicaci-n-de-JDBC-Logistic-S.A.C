package grupocho.logisticsac.interfaz;

import grupocho.logisticsac.dao.AlmacenDAO;
import grupocho.logisticsac.dao.ConductorDAO;
import grupocho.logisticsac.dao.DocumentoDAO;
import grupocho.logisticsac.dao.EvidenciaDAO;
import grupocho.logisticsac.dao.InspeccionDAO;
import grupocho.logisticsac.dao.PrecintoDAO;
import grupocho.logisticsac.dao.RecepcionDAO;
import grupocho.logisticsac.dao.TipoDocumentoDAO;
import grupocho.logisticsac.dao.TrasladoDAO;
import grupocho.logisticsac.dao.UsuarioDAO;
import grupocho.logisticsac.dao.VehiculoDAO;
import grupocho.logisticsac.enums.EstadoTraslado;
import grupocho.logisticsac.enums.Rol;
import grupocho.logisticsac.modelo.Almacen;
import grupocho.logisticsac.modelo.Conductor;
import grupocho.logisticsac.modelo.DetalleTraslado;
import grupocho.logisticsac.modelo.Documento;
import grupocho.logisticsac.modelo.Evidencia;
import grupocho.logisticsac.modelo.FiltroTraslado;
import grupocho.logisticsac.modelo.Inspeccion;
import grupocho.logisticsac.modelo.Precinto;
import grupocho.logisticsac.modelo.Recepcion;
import grupocho.logisticsac.modelo.Traslado;
import grupocho.logisticsac.modelo.Usuario;
import grupocho.logisticsac.modelo.Vehiculo;
import grupocho.logisticsac.repository.DocumentoRepository;
import grupocho.logisticsac.repository.PrecintoRepository;
import grupocho.logisticsac.repository.TrasladoRepository;
import grupocho.logisticsac.service.AlmacenService;
import grupocho.logisticsac.service.ConductorService;
import grupocho.logisticsac.service.DocumentoService;
import grupocho.logisticsac.service.InspeccionService;
import grupocho.logisticsac.service.PrecintoService;
import grupocho.logisticsac.service.RecepcionService;
import grupocho.logisticsac.service.TrasladoService;
import grupocho.logisticsac.service.UsuarioService;
import grupocho.logisticsac.service.VehiculoService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class HistorialTrasladoView {

    private final Usuario usuario;
    private final TrasladoService trasladoService;
    private final VehiculoService vehiculoService;
    private final ConductorService conductorService;
    private final AlmacenService almacenService;
    private final UsuarioService usuarioService;
    private final DocumentoService documentoService;
    private final InspeccionService inspeccionService;
    private final PrecintoService precintoService;
    private final RecepcionService recepcionService;
    private final DashboardLayout dashboardLayout;

    public HistorialTrasladoView(Usuario usuario) {
        this(usuario, null);
    }

    public HistorialTrasladoView(Usuario usuario, DashboardLayout dashboardLayout) {
        this.usuario = usuario;
        this.dashboardLayout = dashboardLayout;

        TrasladoRepository trasladoRepository = new TrasladoDAO();
        DocumentoRepository documentoRepository = new DocumentoDAO();
        PrecintoRepository precintoRepository = new PrecintoDAO();

        this.trasladoService = new TrasladoService(trasladoRepository, documentoRepository);
        this.vehiculoService = new VehiculoService(new VehiculoDAO());
        this.conductorService = new ConductorService(new ConductorDAO());
        this.almacenService = new AlmacenService(new AlmacenDAO());
        this.usuarioService = new UsuarioService(new UsuarioDAO());
        this.documentoService = new DocumentoService(documentoRepository, new TipoDocumentoDAO());
        this.inspeccionService = new InspeccionService(new InspeccionDAO(), new EvidenciaDAO(), precintoRepository);
        this.precintoService = new PrecintoService(precintoRepository);
        this.recepcionService = new RecepcionService(new RecepcionDAO(), trasladoRepository, precintoRepository);
    }

    public void mostrar(Stage stage) {
        Label titulo = new Label("HISTORIAL DE TRASLADOS");
        titulo.getStyleClass().add("historial-titulo");

        TextField txtCodigo = new TextField();
        txtCodigo.setPromptText("Código");
        txtCodigo.getStyleClass().add("historial-campo");

        DatePicker fechaDesde = new DatePicker();
        fechaDesde.setPromptText("Desde");
        fechaDesde.getStyleClass().add("historial-campo");

        DatePicker fechaHasta = new DatePicker();
        fechaHasta.setPromptText("Hasta");
        fechaHasta.getStyleClass().add("historial-campo");

        ComboBox<EstadoTraslado> cmbEstado = new ComboBox<>();
        cmbEstado.getItems().addAll(EstadoTraslado.values());
        cmbEstado.setPromptText("Estado");
        cmbEstado.getStyleClass().add("historial-campo");

        ComboBox<Vehiculo> cmbVehiculo = new ComboBox<>();
        cmbVehiculo.setPromptText("Vehículo");
        cmbVehiculo.getStyleClass().add("historial-campo");

        ComboBox<Conductor> cmbConductor = new ComboBox<>();
        cmbConductor.setPromptText("Conductor");
        cmbConductor.getStyleClass().add("historial-campo");

        ComboBox<Usuario> cmbVigilante = new ComboBox<>();
        cmbVigilante.setPromptText("Vigilante");
        cmbVigilante.getStyleClass().add("historial-campo");

        ComboBox<Almacen> cmbAlmacen = new ComboBox<>();
        cmbAlmacen.setPromptText("Almacén");
        cmbAlmacen.getStyleClass().add("historial-campo");

        Button btnBuscar = new Button("Buscar");
        btnBuscar.getStyleClass().add("historial-boton");

        Button btnLimpiar = new Button("Limpiar");
        btnLimpiar.getStyleClass().add("historial-boton-secundario");

        Button btnDetalle = new Button("Ver detalle");
        btnDetalle.getStyleClass().add("historial-boton");

        HBox filtros = new HBox(10, txtCodigo, fechaDesde, fechaHasta, cmbEstado);
        filtros.getStyleClass().add("historial-filtros");

        HBox filtros2 = new HBox(10, cmbVehiculo, cmbConductor, cmbVigilante, cmbAlmacen, btnBuscar, btnLimpiar);
        filtros2.getStyleClass().add("historial-filtros");

        TableView<Traslado> tabla = new TableView<>();

        TableColumn<Traslado, Integer> id = new TableColumn<>("ID");
        id.setCellValueFactory(new PropertyValueFactory<>("idTraslado"));

        TableColumn<Traslado, String> codigo = new TableColumn<>("Código");
        codigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));

        TableColumn<Traslado, String> fechaColumna = new TableColumn<>("Fecha");
        fechaColumna.setCellValueFactory(cellData -> {
            LocalDate valor = cellData.getValue().getFechaProgramada();
            return new SimpleStringProperty(valor != null ? valor.toString() : "");
        });

        TableColumn<Traslado, String> origen = new TableColumn<>("Origen");
        origen.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getAlmacenOrigen() != null
                                ? cellData.getValue().getAlmacenOrigen().getNombre()
                                : ""
                ));

        TableColumn<Traslado, String> destino = new TableColumn<>("Destino");
        destino.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getAlmacenDestino() != null
                                ? cellData.getValue().getAlmacenDestino().getNombre()
                                : ""
                ));

        TableColumn<Traslado, String> vehiculo = new TableColumn<>("Vehículo");
        vehiculo.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getVehiculo() != null
                                ? cellData.getValue().getVehiculo().getPlaca()
                                : ""
                ));

        TableColumn<Traslado, String> conductor = new TableColumn<>("Conductor");
        conductor.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getConductor() != null
                                ? cellData.getValue().getConductor().getNombres()
                                : ""
                ));

        TableColumn<Traslado, String> vigilante = new TableColumn<>("Vigilante");
        vigilante.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getVigilante() != null
                                ? cellData.getValue().getVigilante().getNombreCompleto()
                                : ""
                ));

        TableColumn<Traslado, String> estado = new TableColumn<>("Estado");
        estado.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getEstado() != null
                                ? cellData.getValue().getEstado().name()
                                : ""
                ));

        TableColumn<Traslado, String> motivoRechazo = new TableColumn<>("Motivo rechazo");
        motivoRechazo.setCellValueFactory(new PropertyValueFactory<>("motivoRechazo"));

        tabla.getColumns().addAll(
                id, codigo, fechaColumna, origen, destino,
                vehiculo, conductor, vigilante, estado, motivoRechazo
        );

        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabla.setPrefHeight(230);
        tabla.getStyleClass().add("historial-tabla");

        TextArea txtDetalle = new TextArea();
        txtDetalle.setEditable(false);
        txtDetalle.setPromptText("Seleccione un traslado y presione \"Ver detalle\".");
        txtDetalle.setPrefRowCount(12);
        txtDetalle.setWrapText(true);
        txtDetalle.getStyleClass().add("historial-detalle");

        Label mensaje = new Label();
        mensaje.setWrapText(true);
        mensaje.getStyleClass().add("historial-mensaje");

        try {
            cmbVehiculo.getItems().addAll(vehiculoService.listar());
            cmbConductor.getItems().addAll(conductorService.listar());
            cmbAlmacen.getItems().addAll(almacenService.listar());

            for (Usuario u : usuarioService.listar()) {
                if (u.tieneRol(Rol.VIGILANTE)) {
                    cmbVigilante.getItems().add(u);
                }
            }

            tabla.setItems(
                    FXCollections.observableArrayList(trasladoService.listar())
            );
        } catch (SQLException e) {
            mensaje.setText("Error al cargar el historial.");
        }

        btnBuscar.setOnAction(event -> {
            try {
                FiltroTraslado filtro = new FiltroTraslado();
                filtro.setCodigo(txtCodigo.getText());
                filtro.setFechaDesde(fechaDesde.getValue());
                filtro.setFechaHasta(fechaHasta.getValue());
                filtro.setEstado(cmbEstado.getValue());
                filtro.setVehiculo(cmbVehiculo.getValue());
                filtro.setConductor(cmbConductor.getValue());
                filtro.setVigilante(cmbVigilante.getValue());
                filtro.setAlmacen(cmbAlmacen.getValue());

                List<Traslado> resultados = trasladoService.buscar(filtro);
                tabla.setItems(FXCollections.observableArrayList(resultados));
                txtDetalle.clear();

                if (resultados.isEmpty()) {
                    mensaje.setText("No se encontraron resultados.");
                } else {
                    mensaje.setText("Resultados encontrados: " + resultados.size());
                }
            } catch (IllegalArgumentException e) {
                mensaje.setText(e.getMessage());
            } catch (SQLException e) {
                mensaje.setText("Error al realizar la búsqueda.");
            }
        });

        btnLimpiar.setOnAction(event -> {
            txtCodigo.clear();
            fechaDesde.setValue(null);
            fechaHasta.setValue(null);
            cmbEstado.setValue(null);
            cmbVehiculo.setValue(null);
            cmbConductor.setValue(null);
            cmbVigilante.setValue(null);
            cmbAlmacen.setValue(null);
            txtDetalle.clear();

            try {
                tabla.setItems(
                        FXCollections.observableArrayList(trasladoService.listar())
                );
                mensaje.setText("");
            } catch (SQLException e) {
                mensaje.setText("Error al cargar el historial.");
            }
        });

        btnDetalle.setOnAction(event -> {
            Traslado seleccionado = tabla.getSelectionModel().getSelectedItem();

            if (seleccionado == null) {
                mensaje.setText("Seleccione un traslado de la tabla.");
                return;
            }

            try {
                txtDetalle.setText(armarDetalle(seleccionado));
                mensaje.setText("");
            } catch (SQLException e) {
                mensaje.setText("Error al cargar el detalle del traslado.");
            }
        });

        Button btnVolver = new Button("Volver");
        btnVolver.getStyleClass().add("historial-boton-secundario");

        btnVolver.setOnAction(event -> {
            if (dashboardLayout != null) {
                dashboardLayout.mostrarContenido("Panel principal", null);
            } else {
                new DashboardView(usuario).mostrar(stage);
            }
        });

        VBox contenido = new VBox(
                10,
                titulo,
                filtros,
                filtros2,
                tabla,
                btnDetalle,
                txtDetalle,
                mensaje,
                btnVolver
        );

        contenido.setPadding(new Insets(20));
        contenido.getStyleClass().add("historial-contenedor");

        if (dashboardLayout != null) {
            Scene scene = stage.getScene();
            var css = getClass().getResource("/style/historial-traslado.css");

            if (css != null && !scene.getStylesheets().contains(css.toExternalForm())) {
                scene.getStylesheets().add(css.toExternalForm());
            }

            ScrollPane scroll = new ScrollPane(contenido);
            scroll.setFitToWidth(true);
            scroll.setPannable(true);
            scroll.getStyleClass().add("historial-scroll");

            dashboardLayout.mostrarContenido("Historial de traslados", scroll);
        } else {
            ScrollPane scroll = new ScrollPane(contenido);
            scroll.setFitToWidth(true);
            scroll.setPannable(true);

            Scene scene = new Scene(scroll, 1200, 700);
            var css = getClass().getResource("/style/historial-traslado.css");

            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }

            stage.setTitle("Logistic S.A.C. - Historial de traslados");
            stage.setScene(scene);
            stage.show();
            stage.centerOnScreen();
        }
    }

    // Arma el texto con los productos, documentos, inspección, evidencias y recepción.
    private String armarDetalle(Traslado traslado) throws SQLException {
        StringBuilder texto = new StringBuilder();

        texto.append("TRASLADO ").append(traslado.getCodigo())
                .append(" | Estado: ").append(traslado.getEstado()).append("\n");
        texto.append("Ruta: ").append(traslado.getAlmacenOrigen().getNombre())
                .append(" -> ").append(traslado.getAlmacenDestino().getNombre()).append("\n");
        texto.append("Vehículo: ").append(traslado.getVehiculo())
                .append(" | Conductor: ").append(traslado.getConductor()).append("\n");

        if (traslado.getResponsableSalida() != null) {
            texto.append("Decisión de salida: ").append(traslado.getFechaHoraSalida())
                    .append(" por ").append(traslado.getResponsableSalida().getNombreCompleto()).append("\n");
        }

        if (traslado.getMotivoRechazo() != null) {
            texto.append("Motivo de rechazo: ").append(traslado.getMotivoRechazo()).append("\n");
        }

        texto.append("\nPRODUCTOS\n");
        for (DetalleTraslado detalle : trasladoService.listarDetalles(traslado)) {
            texto.append("  - ").append(detalle.getProducto().getDescripcion())
                    .append(": ").append(detalle.getCantidad())
                    .append(" ").append(detalle.getProducto().getUnidadMedida()).append("\n");
        }

        texto.append("\nDOCUMENTOS\n");
        agregarDocumentos(texto, trasladoService.listarDocumentos(traslado));
        agregarDocumentos(texto, documentoService.listarPorVehiculo(traslado.getVehiculo()));
        agregarDocumentos(texto, documentoService.listarPorConductor(traslado.getConductor()));

        texto.append("\nINSPECCIÓN EN GARITA\n");
        Inspeccion inspeccion = inspeccionService.buscarPorTraslado(traslado.getIdTraslado());

        if (inspeccion == null) {
            texto.append("  Sin inspección registrada.\n");
        } else {
            texto.append("  ").append(inspeccion.getFechaHora())
                    .append(" | Vigilante: ").append(inspeccion.getVigilante().getNombreCompleto()).append("\n");
            texto.append("  Vehículo: ").append(inspeccion.getResultado())
                    .append(" | Carga: ").append(inspeccion.isCargaConforme() ? "CONFORME" : "NO CONFORME").append("\n");

            if (inspeccion.getObservacion() != null) {
                texto.append("  Observación: ").append(inspeccion.getObservacion()).append("\n");
            }

            Precinto precinto = precintoService.buscarPorTraslado(traslado.getIdTraslado());
            if (precinto != null) {
                texto.append("  Precinto: ").append(precinto.getNumero()).append("\n");
            }

            texto.append("  Evidencias:\n");
            for (Evidencia evidencia : inspeccion.getEvidencias()) {
                texto.append("    - ").append(evidencia.getRutaArchivo())
                        .append(" (").append(evidencia.getDescripcion()).append(")\n");
            }
        }

        texto.append("\nRECEPCIÓN EN DESTINO\n");
        Recepcion recepcion = recepcionService.buscarPorTraslado(traslado.getIdTraslado());

        if (recepcion == null) {
            texto.append("  Sin recepción registrada.\n");
        } else {
            texto.append("  ").append(recepcion.getFechaHoraRecepcion())
                    .append(" | Despachador: ").append(recepcion.getDespachador().getNombreCompleto()).append("\n");
            texto.append("  Precinto: ").append(recepcion.isPrecintoConforme() ? "CONFORME" : "NO CONFORME")
                    .append(" | Carga: ").append(recepcion.isCargaConforme() ? "CONFORME" : "NO CONFORME").append("\n");

            if (recepcion.getObservacion() != null) {
                texto.append("  Observación: ").append(recepcion.getObservacion()).append("\n");
            }
        }

        return texto.toString();
    }

    private void agregarDocumentos(StringBuilder texto, List<Documento> documentos) {
        for (Documento documento : documentos) {
            texto.append("  - ").append(documento.getTipoDocumento().getNombre())
                    .append(" N° ").append(documento.getNumero())
                    .append(" (vence ").append(documento.getFechaVencimiento()).append(")\n");
        }
    }
}