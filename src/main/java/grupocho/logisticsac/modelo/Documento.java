package grupocho.logisticsac.modelo;

import java.time.LocalDate;

public class Documento {
    private int idDocumento;
    private String numero;
    private String fechaEmision;
    private String fechaVencimiento;
    private String estado;
    private String observacion;
    private TipoDocumento tipoDocumento;

    public Documento() {
    }

    public Documento(String numero, String fechaEmision, String fechaVencimiento, String estado, TipoDocumento tipoDocumento) {
        this.numero = numero;
        this.fechaEmision = fechaEmision;
        this.fechaVencimiento = fechaVencimiento;
        this.estado = estado;
        this.tipoDocumento = tipoDocumento;
    }

    public Documento(int idDocumento, String numero, String fechaEmision, String fechaVencimiento, String estado, String observacion, TipoDocumento tipoDocumento) {
        this.idDocumento = idDocumento;
        this.numero = numero;
        this.fechaEmision = fechaEmision;
        this.fechaVencimiento = fechaVencimiento;
        this.estado = estado;
        this.observacion = observacion;
        this.tipoDocumento = tipoDocumento;
    }

    public boolean estaVigente() {
        return "VIGENTE".equalsIgnoreCase(estado);
    }

    public boolean estaVencido() {
        return "VENCIDO".equalsIgnoreCase(estado);
    }

    public boolean estaObservado() {
        return "OBSERVADO".equalsIgnoreCase(estado);
    }

    // marca el documento como VIGENTE o VENCIDO segun la fecha indicada
    public void actualizarEstado(LocalDate fecha) {
        if (fechaVencimiento != null && LocalDate.parse(fechaVencimiento).isBefore(fecha)) {
            this.estado = "VENCIDO";
        } else {
            this.estado = "VIGENTE";
        }
    }

    public int getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(int idDocumento) {
        this.idDocumento = idDocumento;
    }
    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getFechaEmision() {
        return fechaEmision;
    }
    public void setFechaEmision(String fechaEmision) {
        this.fechaEmision = fechaEmision;
    }
    public String getFechaVencimiento() {
        return fechaVencimiento;
    }
    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }
}