package grupocho.logisticsac.modelo;

import java.time.LocalDateTime;

public class Recepcion {
    private int idRecepcion;
    private LocalDateTime fechaHoraRecepcion;
    private boolean precintoConforme;
    private boolean cargaConforme;
    private String observacion;

    private Traslado traslado;
    private Usuario despachador;

    public Recepcion() {
    }

    public Recepcion(Traslado traslado, Usuario despachador) {
        this.traslado = traslado;
        this.despachador = despachador;
        this.fechaHoraRecepcion = LocalDateTime.now();
    }

    public Recepcion(int idRecepcion,LocalDateTime fechaHoraRecepcion, boolean precintoConforme, boolean cargaConforme, String observacion, Traslado traslado, Usuario despachador) {
        this.idRecepcion = idRecepcion;
        this.fechaHoraRecepcion = fechaHoraRecepcion;
        this.precintoConforme = precintoConforme;
        this.cargaConforme = cargaConforme;
        this.observacion = observacion;
        this.traslado = traslado;
        this.despachador = despachador;
    }

    public boolean tieneObservaciones() {
        return !precintoConforme || !cargaConforme;
    }

    public int getIdRecepcion() {
        return idRecepcion;
    }

    public void setIdRecepcion(int idRecepcion) {
        this.idRecepcion = idRecepcion;
    }

    public LocalDateTime getFechaHoraRecepcion() {
        return fechaHoraRecepcion;
    }

    public void setFechaHoraRecepcion(LocalDateTime fechaHoraRecepcion) {
        this.fechaHoraRecepcion = fechaHoraRecepcion;
    }

    public boolean isPrecintoConforme() {
        return precintoConforme;
    }

    public void setPrecintoConforme(boolean precintoConforme) {
        this.precintoConforme = precintoConforme;
    }

    public boolean isCargaConforme() {
        return cargaConforme;
    }

    public void setCargaConforme(boolean cargaConforme) {
        this.cargaConforme = cargaConforme;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Traslado getTraslado() {
        return traslado;
    }

    public void setTraslado(Traslado traslado) {
        this.traslado = traslado;
    }

    public Usuario getDespachador() {
        return despachador;
    }

    public void setDespachador(Usuario despachador) {
        this.despachador = despachador;
    }
}