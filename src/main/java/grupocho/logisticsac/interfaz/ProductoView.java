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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;

public class ProductoView {
    private final Usuario usuario;
    private final ProductoService productoService;

    public ProductoView(Usuario usuario) {
        this.usuario = usuario;
        ProductoRepository repository = new ProductoDAO();
        this.productoService = new ProductoService(repository);
    }

    public void mostrar(Stage stage) {
        Label titulo = new Label("PRODUCTOS");

        TextField txtCodigo = new TextField();
        txtCodigo.setPromptText("Código");

        TextField txtDescripcion = new TextField();
        txtDescripcion.setPromptText("Descripción");

        TextField txtUnidad = new TextField();
        txtUnidad.setPromptText("Unidad de medida");

        Button btnRegistrar = new Button("Registrar");
        Button btnActualizar = new Button("Actualizar");
        Button btnEliminar = new Button("Eliminar");
        Button btnVolver = new Button("Volver");

        TableView<Producto> tabla = new TableView<>();
        TableColumn<Producto, String> codigo = new TableColumn<>("Código");
        TableColumn<Producto, String> descripcion = new TableColumn<>("Descripción");
        TableColumn<Producto, String> unidad = new TableColumn<>("Unidad");

        codigo.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("codigo"));
        descripcion.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("descripcion"));
        unidad.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("unidadMedida"));

        tabla.getColumns().addAll(codigo, descripcion, unidad);

        Label mensaje = new Label();

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

                tabla.setItems(FXCollections.observableArrayList(productoService.listar()));
                txtCodigo.clear();
                txtDescripcion.clear();
                txtUnidad.clear();
                mensaje.setText("Producto registrado.");

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
            new DashboardView(usuario).mostrar(stage);
        });

        VBox layout = new VBox(
                10,
                titulo,
                txtCodigo,
                txtDescripcion,
                txtUnidad,
                btnRegistrar,
                btnActualizar,
                btnEliminar,
                tabla,
                mensaje,
                btnVolver
        );

        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 700, 500);
        stage.setTitle("Logistic S.A.C. - Productos");
        stage.setScene(scene);
        stage.show();
    }

    private void cargar(TableView<Producto> tabla, Label mensaje) {
        try {
            tabla.setItems(FXCollections.observableArrayList(productoService.listar()));
        } catch (SQLException e) {
            mensaje.setText("Error al cargar productos.");
        }
    }
}