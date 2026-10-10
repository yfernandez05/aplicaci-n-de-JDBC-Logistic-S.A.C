
package grupocho.logisticsac.interfaz;

import grupocho.logisticsac.dao.TipoDocumentoDAO;
import grupocho.logisticsac.enums.AmbitoDocumento;
import grupocho.logisticsac.modelo.TipoDocumento;
import grupocho.logisticsac.modelo.Usuario;
import grupocho.logisticsac.repository.TipoDocumentoRepository;
import grupocho.logisticsac.service.TipoDocumentoService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;

public class TipoDocumentoView {
    private final Usuario usuario;
    private final TipoDocumentoService tipoDocumentoService;
    private final DashboardLayout dashboardLayout;

    public TipoDocumentoView(Usuario usuario) {
        this(usuario, null);
    }

    public TipoDocumentoView(Usuario usuario, DashboardLayout dashboardLayout) {
        this.usuario = usuario;
        this.dashboardLayout = dashboardLayout;
        TipoDocumentoRepository repository = new TipoDocumentoDAO();
        this.tipoDocumentoService = new TipoDocumentoService(repository);
    }

    public void mostrar(Stage stage) {
        Label titulo = new Label("TIPOS DE DOCUMENTO REQUERIDOS");
        titulo.getStyleClass().add("tipo-documento-titulo");

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre (ejemplo: SOAT)");

        ComboBox<AmbitoDocumento> cmbAmbito = new ComboBox<>();
        cmbAmbito.getItems().addAll(AmbitoDocumento.values());
        cmbAmbito.setPromptText("Pertenece a");
        cmbAmbito.setMaxWidth(Double.MAX_VALUE);

        CheckBox chkObligatorio = new CheckBox("Obligatorio para la salida");
        chkObligatorio.getStyleClass().add("tipo-documento-checkbox");

        Button btnRegistrar = new Button("Registrar");
        btnRegistrar.getStyleClass().add("tipo-documento-registrar");

        Button btnVolver = new Button("Volver");
        btnVolver.getStyleClass().add("tipo-documento-volver");

        TableView<TipoDocumento> tabla = new TableView<>();
        tabla.getStyleClass().add("tipo-documento-tabla");
        tabla.setPrefHeight(280);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<TipoDocumento, String> nombre = new TableColumn<>("Nombre");
        nombre.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombre()));

        TableColumn<TipoDocumento, String> ambito = new TableColumn<>("Pertenece a");
        ambito.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getAmbito().name()));

        TableColumn<TipoDocumento, String> obligatorio = new TableColumn<>("Obligatorio");
        obligatorio.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().isObligatorio() ? "Sí" : "No"));

        tabla.getColumns().addAll(nombre, ambito, obligatorio);

        Label mensaje = new Label();
        mensaje.setWrapText(true);
        mensaje.getStyleClass().add("tipo-documento-mensaje");

        cargar(tabla, mensaje);

        btnRegistrar.setOnAction(event -> {
            try {
                TipoDocumento tipoDocumento = new TipoDocumento(
                        txtNombre.getText().trim(),
                        cmbAmbito.getValue(),
                        chkObligatorio.isSelected()
                );

                tipoDocumentoService.registrar(tipoDocumento);
                cargar(tabla, mensaje);

                txtNombre.clear();
                cmbAmbito.setValue(null);
                chkObligatorio.setSelected(false);

                mensaje.setText("Tipo de documento registrado correctamente.");
            } catch (IllegalArgumentException e) {
                mensaje.setText(e.getMessage());
            } catch (SQLException e) {
                mensaje.setText("Error al registrar el tipo de documento.");
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

        formulario.add(new Label("Nombre:"), 0, 0);
        formulario.add(txtNombre, 1, 0);
        formulario.add(new Label("Pertenece a:"), 2, 0);
        formulario.add(cmbAmbito, 3, 0);
        formulario.add(chkObligatorio, 1, 1, 3, 1);

        GridPane.setHgrow(txtNombre, Priority.ALWAYS);
        GridPane.setHgrow(cmbAmbito, Priority.ALWAYS);

        VBox panelFormulario = crearPanel("DATOS DEL TIPO DE DOCUMENTO", formulario);
        VBox panelTabla = crearPanel("TIPOS DE DOCUMENTO REGISTRADOS", tabla);

        VBox contenido = new VBox(
                12, titulo, panelFormulario, btnRegistrar,
                panelTabla, mensaje, btnVolver
        );
        contenido.setPadding(new Insets(20));
        contenido.getStyleClass().add("tipo-documento-contenedor");

        ScrollPane scroll = new ScrollPane(contenido);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.getStyleClass().add("tipo-documento-scroll");

        if (dashboardLayout != null) {
            dashboardLayout.mostrarContenido("Tipos de documento", scroll);
            Node actual = dashboardLayout.getContenido();

            if (actual != null && actual.getScene() != null) {
                String css = getClass().getResource("/style/tipo-documento.css").toExternalForm();
                if (!actual.getScene().getStylesheets().contains(css)) {
                    actual.getScene().getStylesheets().add(css);
                }
            }
        } else {
            Scene scene = new Scene(scroll, 950, 650);
            scene.getStylesheets().add(getClass().getResource("/style/tipo-documento.css").toExternalForm());
            stage.setTitle("Logistic S.A.C. - Tipos de documento");
            stage.setScene(scene);
            stage.show();
            stage.centerOnScreen();
        }
    }

    private VBox crearPanel(String titulo, Node... elementos) {
        Label encabezado = new Label(titulo);
        VBox panel = new VBox(10, encabezado);
        panel.getStyleClass().add("tipo-documento-panel");
        panel.getChildren().addAll(elementos);
        return panel;
    }

    private void cargar(TableView<TipoDocumento> tabla, Label mensaje) {
        try {
            tabla.setItems(FXCollections.observableArrayList(tipoDocumentoService.listar()));
        } catch (SQLException e) {
            mensaje.setText("Error al cargar los tipos de documento.");
        }
    }
}
