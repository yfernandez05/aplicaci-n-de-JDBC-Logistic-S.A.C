package grupocho.logisticsac.interfaz;

import grupocho.logisticsac.dao.ProductoDAO;
import grupocho.logisticsac.modelo.Producto;
import grupocho.logisticsac.modelo.Usuario;
import grupocho.logisticsac.repository.ProductoRepository;
import grupocho.logisticsac.service.ProductoService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;

public class ProductoView {
    private final Usuario usuario;
    private final ProductoService productoService;
    private final DashboardLayout dashboardLayout;

    public ProductoView(Usuario usuario) {
        this(usuario, null);
    }

    public ProductoView(Usuario usuario, DashboardLayout dashboardLayout) {
        this.usuario = usuario;
        this.dashboardLayout = dashboardLayout;
        ProductoRepository repository = new ProductoDAO();
        this.productoService = new ProductoService(repository);
    }

    public void mostrar(Stage stage) {

        TextField txtCodigo = new TextField();
        txtCodigo.setPromptText("Código del producto");

        TextField txtDescripcion = new TextField();
        txtDescripcion.setPromptText("Descripción");

        TextField txtUnidad = new TextField();
        txtUnidad.setPromptText("Unidad de medida");

        Button btnRegistrar = new Button("Registrar");
        btnRegistrar.getStyleClass().add("producto-boton");

        Button btnActualizar = new Button("Actualizar");
        btnActualizar.getStyleClass().add("producto-boton");

        Button btnEliminar = new Button("Eliminar");
        btnEliminar.getStyleClass().add("producto-boton-secundario");

        Button btnVolver = new Button("Volver");
        btnVolver.getStyleClass().add("producto-boton-secundario");

        TableView<Producto> tabla = new TableView<>();

        TableColumn<Producto, String> codigo = new TableColumn<>("Código");
        codigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));

        TableColumn<Producto, String> descripcion = new TableColumn<>("Descripción");
        descripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        TableColumn<Producto, String> unidad = new TableColumn<>("Unidad");
        unidad.setCellValueFactory(new PropertyValueFactory<>("unidadMedida"));

        tabla.getColumns().addAll(codigo, descripcion, unidad);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabla.getStyleClass().add("producto-tabla");
        tabla.setPrefHeight(350);

        Label mensaje = new Label();
        mensaje.getStyleClass().add("producto-mensaje");
        mensaje.setWrapText(true);

        tabla.setOnMouseClicked(event -> {
            Producto producto = tabla.getSelectionModel().getSelectedItem();
            if (producto != null) {
                txtCodigo.setText(producto.getCodigo());
                txtDescripcion.setText(producto.getDescripcion());
                txtUnidad.setText(producto.getUnidadMedida());
            }
        });

        try {
            tabla.setItems(FXCollections.observableArrayList(productoService.listar()));
        } catch (SQLException e) {
            mensaje.setText("Error al cargar productos.");
        }

        btnRegistrar.setOnAction(event -> {
            try {
                Producto producto = new Producto(
                        txtCodigo.getText(),
                        txtDescripcion.getText(),
                        txtUnidad.getText()
                );
                productoService.registrar(producto);
                cargar(tabla, mensaje);
                txtCodigo.clear();
                txtDescripcion.clear();
                txtUnidad.clear();
                mensaje.setText("Producto registrado correctamente.");
            } catch (IllegalArgumentException e) {
                mensaje.setText(e.getMessage());
            } catch (SQLException e) {
                mensaje.setText("Error al registrar producto.");
            }
        });

        btnActualizar.setOnAction(event -> {
            try {
                Producto seleccionado = tabla.getSelectionModel().getSelectedItem();
                if (seleccionado == null) {
                    mensaje.setText("Seleccione un producto.");
                    return;
                }
                seleccionado.setCodigo(txtCodigo.getText());
                seleccionado.setDescripcion(txtDescripcion.getText());
                seleccionado.setUnidadMedida(txtUnidad.getText());
                productoService.actualizar(seleccionado);
                cargar(tabla, mensaje);
                mensaje.setText("Producto actualizado correctamente.");
            } catch (IllegalArgumentException e) {
                mensaje.setText(e.getMessage());
            } catch (SQLException e) {
                mensaje.setText("Error al actualizar producto.");
            }
        });

        btnEliminar.setOnAction(event -> {
            try {
                Producto seleccionado = tabla.getSelectionModel().getSelectedItem();
                if (seleccionado == null) {
                    mensaje.setText("Seleccione un producto.");
                    return;
                }
                productoService.eliminar(seleccionado.getIdProducto());
                cargar(tabla, mensaje);
                txtCodigo.clear();
                txtDescripcion.clear();
                txtUnidad.clear();
                mensaje.setText("Producto eliminado correctamente.");
            } catch (IllegalArgumentException e) {
                mensaje.setText(e.getMessage());
            } catch (SQLException e) {
                mensaje.setText("Error al eliminar producto.");
            }
        });

        btnVolver.setOnAction(event -> {
            if (dashboardLayout != null) {
                dashboardLayout.mostrarContenido("Panel principal", null);
            } else {
                new DashboardView(usuario).mostrar(stage);
            }
        });

        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(12);
        formulario.add(new Label("Código:"), 0, 0);
        formulario.add(txtCodigo, 1, 0);
        formulario.add(new Label("Descripción:"), 0, 1);
        formulario.add(txtDescripcion, 1, 1);
        formulario.add(new Label("Unidad de medida:"), 0, 2);
        formulario.add(txtUnidad, 1, 2);
        formulario.getStyleClass().add("producto-formulario");

        HBox acciones = new HBox(10, btnRegistrar, btnActualizar, btnEliminar);
        acciones.getStyleClass().add("producto-acciones");

        VBox panelFormulario = new VBox(15,
                new Label("REGISTRAR PRODUCTO"),
                formulario,
                acciones
        );
        panelFormulario.getStyleClass().add("producto-panel");

        VBox panelTabla = new VBox(12,
                new Label("PRODUCTOS REGISTRADOS"),
                tabla,
                mensaje
        );
        panelTabla.getStyleClass().add("producto-panel");

        VBox contenido = new VBox(15, panelFormulario, panelTabla, btnVolver);
        contenido.setPadding(new Insets(20));
        contenido.getStyleClass().add("producto-contenedor");

        if (dashboardLayout != null) {
            Scene scene = stage.getScene();
            var css = getClass().getResource("/style/producto.css");
            if (css != null && !scene.getStylesheets().contains(css.toExternalForm())) {
                scene.getStylesheets().add(css.toExternalForm());
            }
            dashboardLayout.mostrarContenido("Productos", contenido);
        } else {
            Scene scene = new Scene(contenido, 850, 650);
            var css = getClass().getResource("/style/producto.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }
            stage.setTitle("Logistic S.A.C. - Productos");
            stage.setScene(scene);
            stage.show();
        }
    }

    private void cargar(TableView<Producto> tabla, Label mensaje) {
        try {
            tabla.setItems(FXCollections.observableArrayList(productoService.listar()));
        } catch (SQLException e) {
            mensaje.setText("Error al cargar productos.");
        }
    }
}