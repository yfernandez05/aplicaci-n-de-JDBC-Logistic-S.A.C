package grupocho.logisticsac.modelo;

import java.time.LocalDateTime;

public class Evidencia {
    private int idEvidencia;
    private String rutaArchivo;
    private String descripcion;
    private LocalDateTime fechaHoraRegistro;
    private Inspeccion inspeccion;

    public Evidencia() {
    }

    public Evidencia(String rutaArchivo, String descripcion, Inspeccion inspeccion) {
        this.rutaArchivo = rutaArchivo;
        this.descripcion = descripcion;
        this.inspeccion = inspeccion;
        this.fechaHoraRegistro = LocalDateTime.now();
    }

    public Evidencia(int idEvidencia, String rutaArchivo, String descripcion, LocalDateTime fechaHoraRegistro, Inspeccion inspeccion) {
        this.idEvidencia = idEvidencia;
        this.rutaArchivo = rutaArchivo;
        this.descripcion = descripcion;
        this.fechaHoraRegistro = fechaHoraRegistro;
        this.inspeccion = inspeccion;
    }

    public int getIdEvidencia() {
        return idEvidencia;
    }
    public void setIdEvidencia(int idEvidencia) {
        this.idEvidencia = idEvidencia;
    }
    public String getRutaArchivo() {
        return rutaArchivo;
    }
    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaHoraRegistro() {
        return fechaHoraRegistro;
    }

    public void setFechaHoraRegistro(LocalDateTime fechaHoraRegistro) {
        this.fechaHoraRegistro = fechaHoraRegistro;
    }

    public Inspeccion getInspeccion() {
        return inspeccion;
    }

    public void setInspeccion(Inspeccion inspeccion) {
        this.inspeccion = inspeccion;
    }
}