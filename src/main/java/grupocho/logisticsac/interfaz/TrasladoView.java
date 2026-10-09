package grupocho.logisticsac.interfaz;

import grupocho.logisticsac.dao.AlmacenDAO;
import grupocho.logisticsac.dao.ConductorDAO;
import grupocho.logisticsac.dao.DocumentoDAO;
import grupocho.logisticsac.dao.ProductoDAO;
import grupocho.logisticsac.dao.TipoDocumentoDAO;
import grupocho.logisticsac.dao.TrasladoDAO;
import grupocho.logisticsac.dao.VehiculoDAO;
import grupocho.logisticsac.enums.AmbitoDocumento;
import grupocho.logisticsac.modelo.Almacen;
import grupocho.logisticsac.modelo.Conductor;
import grupocho.logisticsac.modelo.DetalleTraslado;
import grupocho.logisticsac.modelo.Documento;
import grupocho.logisticsac.modelo.Producto;
import grupocho.logisticsac.modelo.TipoDocumento;
import grupocho.logisticsac.modelo.Traslado;
import grupocho.logisticsac.modelo.Usuario;
import grupocho.logisticsac.modelo.Vehiculo;
import grupocho.logisticsac.repository.AlmacenRepository;
import grupocho.logisticsac.repository.ConductorRepository;
import grupocho.logisticsac.repository.DocumentoRepository;
import grupocho.logisticsac.repository.ProductoRepository;
import grupocho.logisticsac.repository.TipoDocumentoRepository;
import grupocho.logisticsac.repository.TrasladoRepository;
import grupocho.logisticsac.repository.VehiculoRepository;
import grupocho.logisticsac.service.AlmacenService;
import grupocho.logisticsac.service.ConductorService;
import grupocho.logisticsac.service.ProductoService;
import grupocho.logisticsac.service.TipoDocumentoService;
import grupocho.logisticsac.service.TrasladoService;
import grupocho.logisticsac.service.VehiculoService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;

public class TrasladoView {
    private final Usuario usuario;
    private final TrasladoService trasladoService;
    private final AlmacenService almacenService;
    private final ProductoService productoService;
    private final VehiculoService vehiculoService;
    private final ConductorService conductorService;
    private final TipoDocumentoService tipoDocumentoService;

    public TrasladoView(Usuario usuario) {
        this.usuario = usuario;

        TrasladoRepository trasladoRepository = new TrasladoDAO();
        DocumentoRepository documentoRepository = new DocumentoDAO();
        AlmacenRepository almacenRepository = new AlmacenDAO();
        ProductoRepository productoRepository = new ProductoDAO();
        VehiculoRepository vehiculoRepository = new VehiculoDAO();
        ConductorRepository conductorRepository = new ConductorDAO();
        TipoDocumentoRepository tipoDocumentoRepository = new TipoDocumentoDAO();

        this.trasladoService = new TrasladoService(trasladoRepository, documentoRepository);
        this.almacenService = new AlmacenService(almacenRepository);
        this.productoService = new ProductoService(productoRepository);
        this.vehiculoService = new VehiculoService(vehiculoRepository);
        this.conductorService = new ConductorService(conductorRepository);
        this.tipoDocumentoService = new TipoDocumentoService(tipoDocumentoRepository);
    }

    public void mostrar(Stage stage) {
        Label titulo = new Label("REGISTRAR TRASLADO");

        TextField txtCodigo = new TextField();
        txtCodigo.setPromptText("Código del traslado");

        DatePicker fechaProgramada = new DatePicker();
        fechaProgramada.setPromptText("Seleccione una fecha");

        ComboBox<Almacen> cmbOrigen = new ComboBox<>();
        cmbOrigen.setPromptText("Seleccione almacén");

        ComboBox<Almacen> cmbDestino = new ComboBox<>();
        cmbDestino.setPromptText("Seleccione almacén");

        ComboBox<Vehiculo> cmbVehiculo = new ComboBox<>();
        cmbVehiculo.setPromptText("Seleccione vehículo");

        ComboBox<Conductor> cmbConductor = new ComboBox<>();
        cmbConductor.setPromptText("Seleccione conductor");

        // productos del traslado
        ComboBox<Producto> cmbProducto = new ComboBox<>();
        cmbProducto.setPromptText("Seleccione producto");

        Spinner<Double> spCantidad = new Spinner<>(1.0, 10000.0, 1.0, 1.0);
        spCantidad.setPrefWidth(90);

        Button btnAgregarProducto = new Button("Agregar");
        Button btnQuitarProducto = new Button("Quitar");

        ObservableList<DetalleTraslado> detalles = FXCollections.observableArrayList();
        TableView<DetalleTraslado> tablaDetalles = new TableView<>(detalles);
        tablaDetalles.setPrefHeight(150);

        TableColumn<DetalleTraslado, String> colProducto = new TableColumn<>("Producto");
        colProducto.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getProducto().toString()));

        TableColumn<DetalleTraslado, String> colCantidad = new TableColumn<>("Cantidad");
        colCantidad.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getCantidad())));

        tablaDetalles.getColumns().addAll(colProducto, colCantidad);
        tablaDetalles.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // documentos del traslado (guia de remision, orden de traslado, etc.)
        ComboBox<TipoDocumento> cmbTipoDocumento = new ComboBox<>();
        cmbTipoDocumento.setPromptText("Tipo de documento");

        TextField txtNumeroDocumento = new TextField();
        txtNumeroDocumento.setPromptText("Número");

        DatePicker dpEmision = new DatePicker();
        dpEmision.setPromptText("Emisión");
        dpEmision.setPrefWidth(130);

        DatePicker dpVencimiento = new DatePicker();
        dpVencimiento.setPromptText("Vencimiento");
        dpVencimiento.setPrefWidth(130);

        Button btnAgregarDocumento = new Button("Agregar");
        Button btnQuitarDocumento = new Button("Quitar");

        ObservableList<Documento> documentos = FXCollections.observableArrayList();
        TableView<Documento> tablaDocumentos = TablaDocumentos.crear();
        tablaDocumentos.setItems(documentos);
        tablaDocumentos.setPrefHeight(150);

        Button btnRegistrar = new Button("Registrar traslado");
        Button btnVolver = new Button("Volver");

        Label mensaje = new Label();
        mensaje.setWrapText(true);

        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);

        formulario.add(new Label("Código:"), 0, 0);
        formulario.add(txtCodigo, 1, 0);
        formulario.add(new Label("Fecha programada:"), 0, 1);
        formulario.add(fechaProgramada, 1, 1);
        formulario.add(new Label("Origen:"), 0, 2);
        formulario.add(cmbOrigen, 1, 2);
        formulario.add(new Label("Destino:"), 0, 3);
        formulario.add(cmbDestino, 1, 3);
        formulario.add(new Label("Vehículo:"), 0, 4);
        formulario.add(cmbVehiculo, 1, 4);
        formulario.add(new Label("Conductor:"), 0, 5);
        formulario.add(cmbConductor, 1, 5);

        try {
            cmbOrigen.getItems().addAll(almacenService.listar());
            cmbDestino.getItems().addAll(almacenService.listar());
            cmbVehiculo.getItems().addAll(vehiculoService.listar());
            cmbConductor.getItems().addAll(conductorService.listar());
            cmbProducto.getItems().addAll(productoService.listar());
            cmbTipoDocumento.getItems().addAll(tipoDocumentoService.listarPorAmbito(AmbitoDocumento.TRASLADO));
        } catch (SQLException e) {
            mensaje.setText("No se pudieron cargar los datos de la base de datos.");
        }

        btnAgregarProducto.setOnAction(event -> {
            Producto producto = cmbProducto.getValue();

            if (producto == null) {
                mensaje.setText("Seleccione un producto.");
                return;
            }

            if (spCantidad.getValue() <= 0) {
                mensaje.setText("La cantidad debe ser mayor que cero.");
                return;
            }

            for (DetalleTraslado detalle : detalles) {
                if (detalle.getProducto().getIdProducto() == producto.getIdProducto()) {
                    mensaje.setText("El producto ya fue agregado al traslado.");
                    return;
                }
            }

            detalles.add(new DetalleTraslado(producto, spCantidad.getValue()));
            cmbProducto.setValue(null);
            mensaje.setText("");
        });

        btnQuitarProducto.setOnAction(event -> {
            DetalleTraslado seleccionado = tablaDetalles.getSelectionModel().getSelectedItem();

            if (seleccionado == null) {
                mensaje.setText("Seleccione un producto de la tabla.");
                return;
            }

            detalles.remove(seleccionado);
        });

        btnAgregarDocumento.setOnAction(event -> {
            if (cmbTipoDocumento.getValue() == null) {
                mensaje.setText("Seleccione el tipo de documento.");
                return;
            }

            if (txtNumeroDocumento.getText().isBlank()) {
                mensaje.setText("Ingrese el número del documento.");
                return;
            }

            if (dpEmision.getValue() == null || dpVencimiento.getValue() == null) {
                mensaje.setText("Seleccione la fecha de emisión y de vencimiento del documento.");
                return;
            }

            if (dpVencimiento.getValue().isBefore(dpEmision.getValue())) {
                mensaje.setText("La fecha de vencimiento no puede ser anterior a la de emisión.");
                return;
            }

            documentos.add(new Documento(
                    txtNumeroDocumento.getText().trim(),
                    dpEmision.getValue().toString(),
                    dpVencimiento.getValue().toString(),
                    "VIGENTE",
                    cmbTipoDocumento.getValue()
            ));

            cmbTipoDocumento.setValue(null);
            txtNumeroDocumento.clear();
            dpEmision.setValue(null);
            dpVencimiento.setValue(null);
            mensaje.setText("");
        });

        btnQuitarDocumento.setOnAction(event -> {
            Documento seleccionado = tablaDocumentos.getSelectionModel().getSelectedItem();

            if (seleccionado == null) {
                mensaje.setText("Seleccione un documento de la tabla.");
                return;
            }

            documentos.remove(seleccionado);
        });

        btnRegistrar.setOnAction(event -> {
            try {
                if (txtCodigo.getText() == null || txtCodigo.getText().isBlank()) {
                    mensaje.setText("Ingrese el código del traslado.");
                    return;
                }

                if (fechaProgramada.getValue() == null) {
                    mensaje.setText("Seleccione la fecha programada.");
                    return;
                }

                if (cmbOrigen.getValue() == null) {
                    mensaje.setText("Seleccione el almacén de origen.");
                    return;
                }

                if (cmbDestino.getValue() == null) {
                    mensaje.setText("Seleccione el almacén de destino.");
                    return;
                }

                if (cmbOrigen.getValue().getIdAlmacen() == cmbDestino.getValue().getIdAlmacen()) {
                    mensaje.setText("El origen y destino deben ser diferentes.");
                    return;
                }

                if (cmbVehiculo.getValue() == null) {
                    mensaje.setText("Seleccione un vehículo.");
                    return;
                }

                if (cmbConductor.getValue() == null) {
                    mensaje.setText("Seleccione un conductor.");
                    return;
                }

                if (detalles.isEmpty()) {
                    mensaje.setText("Agregue al menos un producto al traslado.");
                    return;
                }

                Traslado traslado = new Traslado(
                        txtCodigo.getText().trim(),
                        fechaProgramada.getValue(),
                        cmbOrigen.getValue(),
                        cmbDestino.getValue(),
                        cmbVehiculo.getValue(),
                        cmbConductor.getValue()
                );

                for (DetalleTraslado detalle : detalles) {
                    traslado.agregarDetalle(detalle);
                }

                for (Documento documento : documentos) {
                    traslado.agregarDocumento(documento);
                }

                trasladoService.registrar(traslado);

                mensaje.setText("Traslado " + traslado.getCodigo() + " registrado correctamente. Estado: " + traslado.getEstado());

                txtCodigo.clear();
                fechaProgramada.setValue(null);
                cmbOrigen.setValue(null);
                cmbDestino.setValue(null);
                cmbVehiculo.setValue(null);
                cmbConductor.setValue(null);
                detalles.clear();
                documentos.clear();

            } catch (IllegalArgumentException e) {
                mensaje.setText(e.getMessage());
            } catch (SQLException e) {
                mensaje.setText("Error al registrar: " + e.getMessage());
            }
        });

        btnVolver.setOnAction(event -> {
            DashboardView dashboardView = new DashboardView(usuario);
            dashboardView.mostrar(stage);
        });

        VBox datos = new VBox(15, titulo, formulario, btnRegistrar, mensaje, btnVolver);
        datos.setPrefWidth(330);

        VBox listas = new VBox(
                10,
                new Label("Productos del traslado:"),
                new HBox(10, cmbProducto, spCantidad, btnAgregarProducto, btnQuitarProducto),
                tablaDetalles,
                new Label("Documentos del traslado:"),
                new HBox(10, cmbTipoDocumento, txtNumeroDocumento),
                new HBox(10, dpEmision, dpVencimiento, btnAgregarDocumento, btnQuitarDocumento),
                tablaDocumentos
        );

        HBox layout = new HBox(25, datos, listas);
        layout.setPadding(new Insets(25));

        Scene scene = new Scene(layout, 1000, 620);
        stage.setTitle("Logistic S.A.C. - Registrar traslado");
        stage.setScene(scene);
        stage.show();
        stage.centerOnScreen();
    }
}
