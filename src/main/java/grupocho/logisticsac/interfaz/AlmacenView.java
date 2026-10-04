package grupocho.logisticsac.interfaz;

import grupocho.logisticsac.dao.AlmacenDAO;
import grupocho.logisticsac.modelo.Almacen;
import grupocho.logisticsac.modelo.Usuario;
import grupocho.logisticsac.repository.AlmacenRepository;
import grupocho.logisticsac.service.AlmacenService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;

public class AlmacenView {
    private final Usuario usuario;
    private final AlmacenService almacenService;

    public AlmacenView(Usuario usuario) {
        this.usuario = usuario;
        AlmacenRepository repository = new AlmacenDAO();
        this.almacenService = new AlmacenService(repository);
    }

    public void mostrar(Stage stage) {
        Label titulo = new Label("ALMACENES");

        TextField txtCodigo = new TextField();
        txtCodigo.setPromptText("Código");

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre");

        TextField txtDireccion = new TextField();
        txtDireccion.setPromptText("Dirección");

        Button btnRegistrar = new Button("Registrar");
        Button btnActualizar = new Button("Actualizar");
        Button btnEliminar = new Button("Eliminar");
        Button btnVolver = new Button("Volver");

        TableView<Almacen> tabla = new TableView<>();

        TableColumn<Almacen, String> codigo = new TableColumn<>("Código");
        codigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));

        TableColumn<Almacen, String> nombre = new TableColumn<>("Nombre");
        nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Almacen, String> direccion = new TableColumn<>("Dirección");
        direccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));

        tabla.getColumns().addAll(codigo, nombre, direccion);

        Label mensaje = new Label();

        tabla.setOnMouseClicked(event -> {
            Almacen seleccionado = tabla.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                txtCodigo.setText(seleccionado.getCodigo());
                txtNombre.setText(seleccionado.getNombre());
                txtDireccion.setText(seleccionado.getDireccion());
            }
        });

        cargar(tabla, mensaje);

        btnRegistrar.setOnAction(event -> {
            try {
                Almacen almacen = new Almacen(
                        txtCodigo.getText(),
                        txtNombre.getText(),
                        txtDireccion.getText()
                );

                almacenService.registrar(almacen);
                cargar(tabla, mensaje);

                txtCodigo.clear();
                txtNombre.clear();
                txtDireccion.clear();

                mensaje.setText("Almacén registrado correctamente.");
            } catch (IllegalArgumentException e) {
                mensaje.setText(e.getMessage());
            } catch (SQLException e) {
                mensaje.setText("Error al registrar almacén.");
            }
        });

        btnActualizar.setOnAction(event -> {
            try {
                Almacen seleccionado = tabla.getSelectionModel().getSelectedItem();
                if (seleccionado == null) {
                    mensaje.setText("Seleccione un almacén.");
                    return;
                }

                seleccionado.setCodigo(txtCodigo.getText());
                seleccionado.setNombre(txtNombre.getText());
                seleccionado.setDireccion(txtDireccion.getText());

                almacenService.actualizar(seleccionado);
                cargar(tabla, mensaje);
                mensaje.setText("Almacén actualizado correctamente.");
            } catch (IllegalArgumentException e) {
                mensaje.setText(e.getMessage());
            } catch (SQLException e) {
                mensaje.setText("Error al actualizar almacén.");
            }
        });

        btnEliminar.setOnAction(event -> {
            try {
                Almacen seleccionado = tabla.getSelectionModel().getSelectedItem();
                if (seleccionado == null) {
                    mensaje.setText("Seleccione un almacén.");
                    return;
                }

                almacenService.eliminar(seleccionado.getIdAlmacen());
                cargar(tabla, mensaje);
                txtCodigo.clear();
                txtNombre.clear();
                txtDireccion.clear();
                mensaje.setText("Almacén eliminado correctamente.");
            } catch (SQLException e) {
                mensaje.setText("Error al eliminar almacén.");
            }
        });

        btnVolver.setOnAction(event -> new DashboardView(usuario).mostrar(stage));

        VBox layout = new VBox(
                10,
                titulo,
                txtCodigo,
                txtNombre,
                txtDireccion,
                btnRegistrar,
                btnActualizar,
                btnEliminar,
                tabla,
                mensaje,
                btnVolver
        );

        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 700, 500);
        stage.setTitle("Logistic S.A.C. - Almacenes");
        stage.setScene(scene);
        stage.show();
    }

    private void cargar(TableView<Almacen> tabla, Label mensaje) {
        try {
            tabla.setItems(FXCollections.observableArrayList(almacenService.listar()));
        } catch (SQLException e) {
            mensaje.setText("Error al cargar almacenes.");
        }
    }


}