package grupocho.logisticsac.modelo;

public class Vehiculo {

    private int idVehiculo;
    private String placa;
    private String tipo;
    private double capacidadCarga;
    private String condicion;
    private String estado;
    private boolean activo;

    public Vehiculo() {
    }

    public Vehiculo(String placa, String tipo, double capacidadCarga, String condicion, String estado) {
        this.placa = placa;
        this.tipo = tipo;
        this.capacidadCarga = capacidadCarga;
        this.condicion = condicion;
        this.estado = estado;
        this.activo = true;
    }

    public Vehiculo(int idVehiculo, String placa, String tipo, double capacidadCarga, String condicion, String estado, boolean activo) {
        this.idVehiculo = idVehiculo;
        this.placa = placa;
        this.tipo = tipo;
        this.capacidadCarga = capacidadCarga;
        this.condicion = condicion;
        this.estado = estado;
        this.activo = activo;
    }
    public boolean validarPlaca() {
        return placa != null && !placa.isBlank();
    }

    public boolean validar() {
        return validarPlaca()
                && tipo != null && !tipo.isBlank()
                && capacidadCarga > 0
                && condicion != null && !condicion.isBlank()
                && estado != null && !estado.isBlank();
    }
    public int getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(int idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getCapacidadCarga() {
        return capacidadCarga;
    }

    public void setCapacidadCarga(double capacidadCarga) {
        this.capacidadCarga = capacidadCarga;
    }
    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }
    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return placa + " - " + tipo;
    }
}