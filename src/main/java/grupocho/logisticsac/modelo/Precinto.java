package grupocho.logisticsac.modelo;

import java.time.LocalDate;

public class Precinto {
    private int idPrecinto;
    private String numero;
    private LocalDate fechaColocacion;
    private String estado;
    private Traslado traslado;

    public Precinto() {
    }

    public Precinto(String numero, LocalDate fechaColocacion, String estado, Traslado traslado) {
        this.numero = numero;
        this.fechaColocacion = fechaColocacion;
        this.estado = estado;
        this.traslado = traslado;
    }

    public Precinto(int idPrecinto, String numero, LocalDate fechaColocacion, String estado, Traslado traslado) {
        this.idPrecinto = idPrecinto;
        this.numero = numero;
        this.fechaColocacion = fechaColocacion;
        this.estado = estado;
        this.traslado = traslado;
    }
    public boolean validar() {
        return numero != null && !numero.isBlank() && fechaColocacion != null && traslado != null;
    }

    public int getIdPrecinto() {
        return idPrecinto;
    }

    public void setIdPrecinto(int idPrecinto) {
        this.idPrecinto = idPrecinto;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public LocalDate getFechaColocacion() {
        return fechaColocacion;
    }

    public void setFechaColocacion(LocalDate fechaColocacion) {
        this.fechaColocacion = fechaColocacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Traslado getTraslado() {
        return traslado;
    }

    public void setTraslado(Traslado traslado) {
        this.traslado = traslado;
    }
}