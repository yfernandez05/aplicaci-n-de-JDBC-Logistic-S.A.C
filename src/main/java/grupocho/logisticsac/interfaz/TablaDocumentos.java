package grupocho.logisticsac.interfaz;

import grupocho.logisticsac.modelo.Documento;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

// Tabla de documentos que se reutiliza en varias pantallas
public class TablaDocumentos {

    private TablaDocumentos() {
    }

    public static TableView<Documento> crear() {
        TableView<Documento> tabla = new TableView<>();

        TableColumn<Documento, String> tipo = new TableColumn<>("Documento");
        tipo.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTipoDocumento().getNombre()));

        TableColumn<Documento, String> ambito = new TableColumn<>("Pertenece a");
        ambito.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTipoDocumento().getAmbito().name()));

        TableColumn<Documento, String> numero = new TableColumn<>("Número");
        numero.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNumero()));

        TableColumn<Documento, String> vencimiento = new TableColumn<>("Vencimiento");
        vencimiento.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFechaVencimiento()));

        TableColumn<Documento, String> estado = new TableColumn<>("Estado");
        estado.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEstado()));

        TableColumn<Documento, String> observacion = new TableColumn<>("Observación");
        observacion.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getObservacion()));

        tabla.getColumns().addAll(tipo, ambito, numero, vencimiento, estado, observacion);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabla.setPrefHeight(160);

        return tabla;
    }
}
