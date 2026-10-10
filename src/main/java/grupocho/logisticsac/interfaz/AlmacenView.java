package grupocho.logisticsac.interfaz;

import grupocho.logisticsac.dao.AlmacenDAO;
import grupocho.logisticsac.modelo.Almacen;
import grupocho.logisticsac.modelo.Usuario;
import grupocho.logisticsac.repository.AlmacenRepository;
import grupocho.logisticsac.service.AlmacenService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;

public class AlmacenView {
    private final Usuario usuario;
    private final AlmacenService almacenService;
    private final DashboardLayout dashboardLayout;

    public AlmacenView(Usuario usuario) {
        this(usuario, null);
    }

    public AlmacenView(Usuario usuario, DashboardLayout dashboardLayout) {
        this.usuario = usuario;
        this.dashboardLayout = dashboardLayout;
        AlmacenRepository repository = new AlmacenDAO();
        this.almacenService = new AlmacenService(repository);
    }

    public void mostrar(Stage stage) {
        Label titulo = new Label("ALMACENES");
        titulo.getStyleClass().add("almacen-titulo");

        Label subtitulo = new Label("Gestión de almacenes y ubicaciones");
        subtitulo.getStyleClass().add("almacen-subtitulo");

        TextField txtCodigo = new TextField();
        txtCodigo.setPromptText("Código");

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre del almacén");

        TextField txtDireccion = new TextField();
        txtDireccion.setPromptText("Dirección");

        Button btnRegistrar = new Button("Registrar");
        btnRegistrar.getStyleClass().add("almacen-registrar");

        Button btnActualizar = new Button("Actualizar");
        btnActualizar.getStyleClass().add("almacen-actualizar");

        Button btnEliminar = new Button("Eliminar");
        btnEliminar.getStyleClass().add("almacen-eliminar");

        Button btnVolver = new Button("Volver");
        btnVolver.getStyleClass().add("almacen-volver");

        TableView<Almacen> tabla = new TableView<>();
        tabla.getStyleClass().add("almacen-tabla");
        tabla.setPrefHeight(280);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Almacen, String> codigo = new TableColumn<>("Código");
        codigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));

        TableColumn<Almacen, String> nombre = new TableColumn<>("Nombre");
        nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Almacen, String> direccion = new TableColumn<>("Dirección");
        direccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));

        tabla.getColumns().addAll(codigo, nombre, direccion);

        Label mensaje = new Label();
        mensaje.getStyleClass().add("almacen-mensaje");
        mensaje.setWrapText(true);

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
                limpiar(txtCodigo, txtNombre, txtDireccion);
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
                limpiar(txtCodigo, txtNombre, txtDireccion);
                mensaje.setText("Almacén eliminado correctamente.");
            } catch (SQLException e) {
                mensaje.setText("Error al eliminar almacén.");
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
        formulario.setHgap(12);
        formulario.setVgap(10);
        formulario.setMaxWidth(750);

        formulario.add(new Label("Código:"), 0, 0);
        formulario.add(txtCodigo, 1, 0);
        formulario.add(new Label("Nombre:"), 2, 0);
        formulario.add(txtNombre, 3, 0);
        formulario.add(new Label("Dirección:"), 0, 1);
        formulario.add(txtDireccion, 1, 1, 3, 1);

        GridPane.setHgrow(txtCodigo, Priority.ALWAYS);
        GridPane.setHgrow(txtNombre, Priority.ALWAYS);
        GridPane.setHgrow(txtDireccion, Priority.ALWAYS);

        HBox botones = new HBox(8, btnRegistrar, btnActualizar, btnEliminar);
        VBox panelFormulario = crearPanel("DATOS DEL ALMACÉN", formulario);
        VBox panelTabla = crearPanel("ALMACENES REGISTRADOS", tabla);

        VBox contenido = new VBox(12, titulo, subtitulo, panelFormulario, botones, panelTabla, mensaje, btnVolver);
        contenido.getStyleClass().add("almacen-contenedor");
        contenido.setPadding(new Insets(20));

        ScrollPane scroll = new ScrollPane(contenido);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.getStyleClass().add("almacen-scroll");

        cargar(tabla, mensaje);

        if (dashboardLayout != null) {
            dashboardLayout.mostrarContenido("Almacenes", scroll);
            Node actual = dashboardLayout.getContenido();
            if (actual != null && actual.getScene() != null) {
                String css = getClass().getResource("/style/almacen.css").toExternalForm();
                if (!actual.getScene().getStylesheets().contains(css)) {
                    actual.getScene().getStylesheets().add(css);
                }
            }
        } else {
            Scene scene = new Scene(scroll, 950, 650);
            scene.getStylesheets().add(getClass().getResource("/style/almacen.css").toExternalForm());
            stage.setTitle("Logistic S.A.C. - Almacenes");
            stage.setScene(scene);
            stage.show();
            stage.centerOnScreen();
        }
    }

    private VBox crearPanel(String titulo, Node... elementos) {
        Label encabezado = new Label(titulo);
        VBox panel = new VBox(10, encabezado);
        panel.getStyleClass().add("almacen-panel");
        panel.getChildren().addAll(elementos);
        return panel;
    }

    private void cargar(TableView<Almacen> tabla, Label mensaje) {
        try {
            tabla.setItems(FXCollections.observableArrayList(almacenService.listar()));
        } catch (SQLException e) {
            mensaje.setText("Error al cargar almacenes.");
        }
    }

    private void limpiar(TextField... campos) {
        for (TextField campo : campos) {
            campo.clear();
        }
    }
}
