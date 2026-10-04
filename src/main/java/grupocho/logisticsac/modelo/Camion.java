package grupocho.logisticsac.modelo;

public class Camion extends Vehiculo {
    private int numeroEjes;
    public Camion() {
        super();
    }

    public Camion(String placa, String tipo, double capacidadCarga, String condicion, String estado, int numeroEjes) {
        super(placa, tipo, capacidadCarga, condicion, estado);
        this.numeroEjes = numeroEjes;
    }

    public Camion(int idVehiculo, String placa, String tipo, double capacidadCarga, String condicion, String estado, boolean activo, int numeroEjes) {
        super(idVehiculo, placa, tipo, capacidadCarga, condicion, estado, activo);
        this.numeroEjes = numeroEjes;
    }

    public boolean validar() {
        return super.validar() && numeroEjes > 0;
    }

    public int getNumeroEjes() {
        return numeroEjes;
    }

    public void setNumeroEjes(int numeroEjes) {
        this.numeroEjes = numeroEjes;
    }
}