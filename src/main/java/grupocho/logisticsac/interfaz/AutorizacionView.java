package grupocho.logisticsac.interfaz;

import grupocho.logisticsac.dao.InspeccionDAO;
import grupocho.logisticsac.dao.TrasladoDAO;
import grupocho.logisticsac.enums.EstadoTraslado;
import grupocho.logisticsac.modelo.Inspeccion;
import grupocho.logisticsac.modelo.Traslado;
import grupocho.logisticsac.modelo.Usuario;
import grupocho.logisticsac.repository.InspeccionRepository;
import grupocho.logisticsac.repository.TrasladoRepository;
import grupocho.logisticsac.service.TrasladoService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.List;

public class AutorizacionView {
    private final Usuario usuario;
    private final TrasladoService trasladoService;
    private final InspeccionRepository inspeccionRepository;

    public AutorizacionView(Usuario usuario) {
        this.usuario = usuario;
        TrasladoRepository trasladoRepository = new TrasladoDAO();
        this.inspeccionRepository = new InspeccionDAO();
        this.trasladoService = new TrasladoService(trasladoRepository);
    }

    public void mostrar(Stage stage) {
        Label titulo = new Label("AUTORIZACIÓN DE SALIDA");

        ComboBox<Traslado> cmbTraslado = new ComboBox<>();
        cmbTraslado.setPromptText("Seleccione traslado");

        Label lblInspeccion = new Label("Inspección: -");
        Label lblResultado = new Label("Resultado: -");

        TextArea txtMotivo = new TextArea();
        txtMotivo.setPromptText("Motivo de rechazo");
        txtMotivo.setPrefRowCount(3);

        Button btnAutorizar = new Button("Autorizar salida");
        Button btnRechazar = new Button("Rechazar");
        Button btnVolver = new Button("Volver");
        Label mensaje = new Label();

        try {
            List<Traslado> traslados = trasladoService.listar().stream()
                    .filter(t -> t.getEstado() == EstadoTraslado.PROGRAMADO)
                    .toList();

            cmbTraslado.setItems(FXCollections.observableArrayList(traslados));
        } catch (SQLException e) {
            mensaje.setText("No se pudieron cargar los traslados.");
        }

        cmbTraslado.setOnAction(event -> {
            Traslado traslado = cmbTraslado.getValue();

            if (traslado == null) {
                return;
            }

            lblInspeccion.setText("Inspección: pendiente de consulta");
            lblResultado.setText("Traslado: " + traslado.getCodigo());
        });

        btnAutorizar.setOnAction(event -> {
            try {
                Traslado traslado = cmbTraslado.getValue();

                if (traslado == null) {
                    mensaje.setText("Seleccione un traslado.");
                    return;
                }

                if (traslado.getEstado() != EstadoTraslado.PROGRAMADO) {
                    mensaje.setText("El traslado no está PROGRAMADO.");
                    return;
                }

                Inspeccion inspeccion = buscarInspeccion(traslado);

                if (inspeccion == null) {
                    mensaje.setText("El traslado no tiene una inspección registrada.");
                    return;
                }

                trasladoService.autorizarSalida(traslado, usuario, inspeccion);

                mensaje.setText("Salida autorizada correctamente.");
                lblInspeccion.setText("Inspección: CONFORME");
                lblResultado.setText("Estado: EN_TRANSITO");
                cmbTraslado.getItems().remove(traslado);
                cmbTraslado.setValue(null);

            } catch (IllegalArgumentException | IllegalStateException e) {
                mensaje.setText(e.getMessage());
            } catch (SQLException e) {
                mensaje.setText("Error al autorizar la salida.");
            }
        });

        btnRechazar.setOnAction(event -> {
            try {
                Traslado traslado = cmbTraslado.getValue();

                if (traslado == null) {
                    mensaje.setText("Seleccione un traslado.");
                    return;
                }

                if (txtMotivo.getText().isBlank()) {
                    mensaje.setText("Ingrese el motivo de rechazo.");
                    return;
                }

                trasladoService.rechazar(traslado, txtMotivo.getText());

                mensaje.setText("Traslado rechazado correctamente.");
                cmbTraslado.getItems().remove(traslado);
                cmbTraslado.setValue(null);
                txtMotivo.clear();

            } catch (IllegalArgumentException | IllegalStateException e) {
                mensaje.setText(e.getMessage());
            } catch (SQLException e) {
                mensaje.setText("Error al rechazar el traslado.");
            }
        });

        btnVolver.setOnAction(event -> {
            DashboardView dashboardView = new DashboardView(usuario);
            dashboardView.mostrar(stage);
        });

        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);
        formulario.setPadding(new Insets(20));

        formulario.add(new Label("Traslado:"), 0, 0);
        formulario.add(cmbTraslado, 1, 0);
        formulario.add(lblInspeccion, 0, 1, 2, 1);
        formulario.add(lblResultado, 0, 2, 2, 1);
        formulario.add(new Label("Motivo:"), 0, 3);
        formulario.add(txtMotivo, 1, 3);

        VBox layout = new VBox(15, titulo, formulario, btnAutorizar, btnRechazar, mensaje, btnVolver);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 650, 500);
        stage.setTitle("Logistic S.A.C. - Autorización de salida");
        stage.setScene(scene);
        stage.show();
    }

    private Inspeccion buscarInspeccion(Traslado traslado) throws SQLException {
        return inspeccionRepository.buscarPorTraslado(ConexionHelper.obtenerConexion(), traslado.getIdTraslado());
    }

    private static class ConexionHelper {
        private static java.sql.Connection obtenerConexion() throws SQLException {
            return grupocho.logisticsac.config.ConexionDB.obtenerConexion();
        }
    }
}