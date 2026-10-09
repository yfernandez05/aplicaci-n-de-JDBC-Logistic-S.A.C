package grupocho.logisticsac.modelo;

import grupocho.logisticsac.enums.EstadoTraslado;

import java.time.LocalDate;

public class FiltroTraslado {
    private String codigo;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private Vehiculo vehiculo;
    private Conductor conductor;
    private Usuario vigilante;
    private Almacen almacen;
    private EstadoTraslado estado;

    public FiltroTraslado() {
    }

    public boolean validar() {
        if (fechaDesde != null && fechaHasta != null) {
            return !fechaDesde.isAfter(fechaHasta);
        }
        return true;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public LocalDate getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(LocalDate fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public LocalDate getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(LocalDate fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public Conductor getConductor() {
        return conductor;
    }

    public void setConductor(Conductor conductor) {
        this.conductor = conductor;
    }

    public Usuario getVigilante() {
        return vigilante;
    }

    public void setVigilante(Usuario vigilante) {
        this.vigilante = vigilante;
    }

    public Almacen getAlmacen() {
        return almacen;
    }

    public void setAlmacen(Almacen almacen) {
        this.almacen = almacen;
    }

    public EstadoTraslado getEstado() {
        return estado;
    }

    public void setEstado(EstadoTraslado estado) {
        this.estado = estado;
    }
}
