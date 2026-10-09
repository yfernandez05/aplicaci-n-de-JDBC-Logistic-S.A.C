package grupocho.logisticsac.modelo;

import grupocho.logisticsac.enums.AmbitoDocumento;

public class TipoDocumento {

    private int idTipoDocumento;
    private String nombre;
    private AmbitoDocumento ambito;
    private boolean obligatorio;
    private boolean activo;

    public TipoDocumento() {
    }

    public TipoDocumento(String nombre, AmbitoDocumento ambito, boolean obligatorio) {
        this.nombre = nombre;
        this.ambito = ambito;
        this.obligatorio = obligatorio;
        this.activo = true;
    }

    public TipoDocumento(int idTipoDocumento, String nombre, AmbitoDocumento ambito, boolean obligatorio, boolean activo) {
        this.idTipoDocumento = idTipoDocumento;
        this.nombre = nombre;
        this.ambito = ambito;
        this.obligatorio = obligatorio;
        this.activo = activo;
    }

    public int getIdTipoDocumento() {
        return idTipoDocumento;
    }
    public void setIdTipoDocumento(int idTipoDocumento) {
        this.idTipoDocumento = idTipoDocumento;
    }
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public AmbitoDocumento getAmbito() {
        return ambito;
    }

    public void setAmbito(AmbitoDocumento ambito) {
        this.ambito = ambito;
    }

    public boolean isObligatorio() {
        return obligatorio;
    }
    public void setObligatorio(boolean obligatorio) {
        this.obligatorio = obligatorio;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return nombre;
    }
}