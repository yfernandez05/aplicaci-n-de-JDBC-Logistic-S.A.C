package grupocho.logisticsac.modelo;

import java.time.LocalDateTime;

public class Precinto {
    private int idPrecinto;
    private String numero;
    private LocalDateTime fechaRegistro;
    private String estado;
    private Traslado traslado;

    public Precinto() {
    }

    public Precinto(String numero, Traslado traslado) {
        this.numero = numero;
        this.traslado = traslado;
        this.fechaRegistro = LocalDateTime.now();
        this.estado = "COLOCADO";
    }

    public Precinto(int idPrecinto, String numero, LocalDateTime fechaRegistro, String estado, Traslado traslado) {
        this.idPrecinto = idPrecinto;
        this.numero = numero;
        this.fechaRegistro = fechaRegistro;
        this.estado = estado;
        this.traslado = traslado;
    }

    public boolean validar() {
        return numero != null && !numero.isBlank() && fechaRegistro != null && traslado != null;
    }

    // compara el numero registrado en garita con el que llega al almacen destino
    public boolean coincideCon(String numeroRecibido) {
        return numero != null && numeroRecibido != null
                && numero.trim().equalsIgnoreCase(numeroRecibido.trim());
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

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
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
