package grupocho.logisticsac.modelo;

import grupocho.logisticsac.enums.EstadoTraslado;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Traslado {
    private int idTraslado;
    private String codigo;
    private LocalDate fechaProgramada;

    private Almacen almacenOrigen;
    private Almacen almacenDestino;
    private Vehiculo vehiculo;
    private Conductor conductor;
    private EstadoTraslado estado;
    private String observacion;
    private String motivoRechazo;
    private LocalDateTime fechaHoraSalida;
    private Usuario responsableSalida;
    private List<DetalleTraslado> detalles;
    private List<Documento> documentos;
    // vigilante que inspecciono el traslado en garita
    private Usuario vigilante;

    public Traslado() {
        this.detalles = new ArrayList<>();
        this.documentos = new ArrayList<>();
        this.estado = EstadoTraslado.PROGRAMADO;
    }

    public Traslado(String codigo, LocalDate fechaProgramada, Almacen almacenOrigen, Almacen almacenDestino, Vehiculo vehiculo, Conductor conductor) {
        this.codigo = codigo;
        this.fechaProgramada = fechaProgramada;
        this.almacenOrigen = almacenOrigen;
        this.almacenDestino = almacenDestino;
        this.vehiculo = vehiculo;
        this.conductor = conductor;
        this.estado = EstadoTraslado.PROGRAMADO;
        this.detalles = new ArrayList<>();
        this.documentos = new ArrayList<>();
    }

    public void agregarDetalle(DetalleTraslado detalle) {
        if (detalle != null) {
            detalles.add(detalle);
        }
    }

    public void agregarDocumento(Documento documento) {
        if (documento != null) {
            documentos.add(documento);
        }
    }
    public boolean tieneDetalles() {
        return !detalles.isEmpty();
    }

    public void marcarEnTransito(Usuario responsable) {
        this.estado = EstadoTraslado.EN_TRANSITO;
        this.fechaHoraSalida = LocalDateTime.now();
        this.responsableSalida = responsable;
    }

    public void rechazar(String motivo, Usuario responsable) {
        this.estado = EstadoTraslado.RECHAZADO;
        this.motivoRechazo = motivo;
        this.fechaHoraSalida = LocalDateTime.now();
        this.responsableSalida = responsable;
    }

    public int getIdTraslado() {
        return idTraslado;
    }

    public void setIdTraslado(int idTraslado) {
        this.idTraslado = idTraslado;
    }
    public String getCodigo() {
        return codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public LocalDate getFechaProgramada() {
        return fechaProgramada;
    }
    public void setFechaProgramada(LocalDate fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    public Almacen getAlmacenOrigen() {
        return almacenOrigen;
    }

    public void setAlmacenOrigen(Almacen almacenOrigen) {
        this.almacenOrigen = almacenOrigen;
    }
    public Almacen getAlmacenDestino() {
        return almacenDestino;
    }

    public void setAlmacenDestino(Almacen almacenDestino) {
        this.almacenDestino = almacenDestino;
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

    public EstadoTraslado getEstado() {
        return estado;
    }

    public void setEstado(EstadoTraslado estado) {
        this.estado = estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }

    public LocalDateTime getFechaHoraSalida() {
        return fechaHoraSalida;
    }

    public void setFechaHoraSalida(LocalDateTime fechaHoraSalida) {
        this.fechaHoraSalida = fechaHoraSalida;
    }

    public Usuario getResponsableSalida() {
        return responsableSalida;
    }

    public void setResponsableSalida(Usuario responsableSalida) {
        this.responsableSalida = responsableSalida;
    }

    public List<DetalleTraslado> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleTraslado> detalles) {
        this.detalles = detalles;
    }

    public List<Documento> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<Documento> documentos) {
        this.documentos = documentos;
    }

    public Usuario getVigilante() {
        return vigilante;
    }

    public void setVigilante(Usuario vigilante) {
        this.vigilante = vigilante;
    }

    @Override
    public String toString() {
        return codigo;
    }
}