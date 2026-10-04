package grupocho.logisticsac.interfaz;

import grupocho.logisticsac.dao.AlmacenDAO;
import grupocho.logisticsac.dao.ConductorDAO;
import grupocho.logisticsac.dao.ProductoDAO;
import grupocho.logisticsac.dao.TrasladoDAO;
import grupocho.logisticsac.dao.VehiculoDAO;
import grupocho.logisticsac.modelo.Almacen;
import grupocho.logisticsac.modelo.Conductor;
import grupocho.logisticsac.modelo.DetalleTraslado;
import grupocho.logisticsac.modelo.Producto;
import grupocho.logisticsac.modelo.Traslado;
import grupocho.logisticsac.modelo.Usuario;
import grupocho.logisticsac.modelo.Vehiculo;
import grupocho.logisticsac.repository.AlmacenRepository;
import grupocho.logisticsac.repository.ConductorRepository;
import grupocho.logisticsac.repository.ProductoRepository;
import grupocho.logisticsac.repository.TrasladoRepository;
import grupocho.logisticsac.repository.VehiculoRepository;
import grupocho.logisticsac.service.AlmacenService;
import grupocho.logisticsac.service.ConductorService;
import grupocho.logisticsac.service.ProductoService;
import grupocho.logisticsac.service.TrasladoService;
import grupocho.logisticsac.service.VehiculoService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.time.LocalDate;

public class TrasladoView {
    private final Usuario usuario;
    private final TrasladoService trasladoService;
    private final AlmacenService almacenService;
    private final ProductoService productoService;
    private final VehiculoService vehiculoService;
    private final ConductorService conductorService;

    public TrasladoView(Usuario usuario) {
        this.usuario = usuario;

        TrasladoRepository trasladoRepository = new TrasladoDAO();
        AlmacenRepository almacenRepository = new AlmacenDAO();
        ProductoRepository productoRepository = new ProductoDAO();
        VehiculoRepository vehiculoRepository = new VehiculoDAO();
        ConductorRepository conductorRepository = new ConductorDAO();

        this.trasladoService = new TrasladoService(trasladoRepository);
        this.almacenService = new AlmacenService(almacenRepository);
        this.productoService = new ProductoService(productoRepository);
        this.vehiculoService = new VehiculoService(vehiculoRepository);
        this.conductorService = new ConductorService(conductorRepository);
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

        ComboBox<Producto> cmbProducto = new ComboBox<>();
        cmbProducto.setPromptText("Seleccione producto");


        Spinner<Double> spCantidad = new Spinner<>(1.0, 10000.0, 1.0, 1.0);

        Button btnRegistrar = new Button("Registrar traslado");
        Button btnHistorial = new Button("Historial de traslados");
        Button btnVolver = new Button("Volver");

        btnHistorial.setOnAction(event -> {
            HistorialTrasladoView vista = new HistorialTrasladoView(usuario);
            vista.mostrar(stage);
        });

        Label mensaje = new Label();

        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);
        formulario.setAlignment(Pos.CENTER);

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
        formulario.add(new Label("Producto:"), 0, 6);
        formulario.add(cmbProducto, 1, 6);
        formulario.add(new Label("Cantidad:"), 0, 7);
        formulario.add(spCantidad, 1, 7);

        try {
            cmbOrigen.getItems().addAll(almacenService.listar());
            cmbDestino.getItems().addAll(almacenService.listar());
            cmbVehiculo.getItems().addAll(vehiculoService.listar());
            cmbConductor.getItems().addAll(conductorService.listar());
            cmbProducto.getItems().addAll(productoService.listar());
        } catch (SQLException e) {
            mensaje.setText("No se pudieron cargar los datos de la base de datos.");
        }

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

                if (cmbProducto.getValue() == null) {
                    mensaje.setText("Seleccione un producto.");
                    return;
                }

                if (spCantidad.getValue() <= 0) {
                    mensaje.setText("La cantidad debe ser mayor que cero.");
                    return;
                }

                Almacen origen = cmbOrigen.getValue();
                Almacen destino = cmbDestino.getValue();
                Vehiculo vehiculo = cmbVehiculo.getValue();
                Conductor conductor = cmbConductor.getValue();
                Producto producto = cmbProducto.getValue();

                Traslado traslado = new Traslado(
                        txtCodigo.getText(),
                        fechaProgramada.getValue(),
                        origen,
                        destino,
                        vehiculo,
                        conductor
                );

                traslado.agregarDetalle(
                        new DetalleTraslado(producto, spCantidad.getValue())
                );

                trasladoService.registrar(traslado);

                mensaje.setText("Traslado registrado correctamente.");

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

        VBox layout = new VBox(20, titulo, formulario, btnRegistrar, mensaje, btnVolver);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(25));

        Scene scene = new Scene(layout, 700, 650);
        stage.setTitle("Logistic S.A.C. - Registrar traslado");
        stage.setScene(scene);
        stage.show();
    }
}