package grupocho.logisticsac.modelo;

import grupocho.logisticsac.enums.ResultadoInspeccion;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Inspeccion {
    private int idInspeccion;
    private LocalDateTime fechaHora;
    private ResultadoInspeccion resultado;
    private boolean cargaConforme;
    private String observacion;
    private Traslado traslado;
    private Usuario vigilante;
    private List<Evidencia> evidencias;

    public Inspeccion() {
        this.fechaHora = LocalDateTime.now();
        this.evidencias = new ArrayList<>();
    }

    public Inspeccion(Traslado traslado, Usuario vigilante) {
        this.traslado = traslado;
        this.vigilante = vigilante;
        this.fechaHora = LocalDateTime.now();
        this.evidencias = new ArrayList<>();
    }

    public void agregarEvidencia(Evidencia evidencia) {
        if (evidencia != null) {
            evidencias.add(evidencia);
        }
    }

    public void marcarConforme() {
        this.resultado = ResultadoInspeccion.CONFORME;
        this.cargaConforme = true;
        this.observacion = null;
    }

    public void marcarNoConforme(String observacion) {
        this.resultado = ResultadoInspeccion.NO_CONFORME;
        this.cargaConforme = false;
        this.observacion = observacion;
    }

    public boolean puedeAutorizar() {
        return resultado == ResultadoInspeccion.CONFORME && cargaConforme && !evidencias.isEmpty();
    }

    public boolean validar() {
        if (traslado == null || vigilante == null || resultado == null) {
            return false;
        }

        boolean conforme = resultado == ResultadoInspeccion.CONFORME && cargaConforme;

        if (!conforme && (observacion == null || observacion.isBlank())) {
            return false;
        }

        if (conforme && evidencias.isEmpty()) {
            return false;
        }

        return true;
    }

    public int getIdInspeccion() {
        return idInspeccion;
    }

    public void setIdInspeccion(int idInspeccion) {
        this.idInspeccion = idInspeccion;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }
    public ResultadoInspeccion getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoInspeccion resultado) {
        this.resultado = resultado;
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
    public Usuario getVigilante() {
        return vigilante;
    }

    public void setVigilante(Usuario vigilante) {
        this.vigilante = vigilante;
    }
    public List<Evidencia> getEvidencias() {
        return evidencias;
    }

    public void setEvidencias(List<Evidencia> evidencias) {
        this.evidencias = evidencias;
    }
}