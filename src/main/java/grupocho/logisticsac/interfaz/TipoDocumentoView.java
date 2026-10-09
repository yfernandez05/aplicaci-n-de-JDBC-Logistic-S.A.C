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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;

public class TipoDocumentoView {
    private final Usuario usuario;
    private final TipoDocumentoService tipoDocumentoService;

    public TipoDocumentoView(Usuario usuario) {
        this.usuario = usuario;
        TipoDocumentoRepository repository = new TipoDocumentoDAO();
        this.tipoDocumentoService = new TipoDocumentoService(repository);
    }

    public void mostrar(Stage stage) {
        Label titulo = new Label("TIPOS DE DOCUMENTO REQUERIDOS");

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre (ejemplo: SOAT)");

        ComboBox<AmbitoDocumento> cmbAmbito = new ComboBox<>();
        cmbAmbito.getItems().addAll(AmbitoDocumento.values());
        cmbAmbito.setPromptText("Pertenece a");

        CheckBox chkObligatorio = new CheckBox("Obligatorio para la salida");

        Button btnRegistrar = new Button("Registrar");
        Button btnVolver = new Button("Volver");

        TableView<TipoDocumento> tabla = new TableView<>();

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
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Label mensaje = new Label();
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

        btnVolver.setOnAction(event -> new DashboardView(usuario).mostrar(stage));

        VBox layout = new VBox(
                10,
                titulo,
                txtNombre,
                cmbAmbito,
                chkObligatorio,
                btnRegistrar,
                tabla,
                mensaje,
                btnVolver
        );

        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 700, 500);
        stage.setTitle("Logistic S.A.C. - Tipos de documento");
        stage.setScene(scene);
        stage.show();
    }

    private void cargar(TableView<TipoDocumento> tabla, Label mensaje) {
        try {
            tabla.setItems(FXCollections.observableArrayList(tipoDocumentoService.listar()));
        } catch (SQLException e) {
            mensaje.setText("Error al cargar los tipos de documento.");
        }
    }
}
