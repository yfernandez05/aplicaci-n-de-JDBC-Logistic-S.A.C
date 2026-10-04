package grupocho.logisticsac.modelo;

public class Conductor {

    private int idConductor;
    private String dni;
    private String nombres;
    private String numeroLicencia;
    private String categoriaLicencia;
    private boolean activo;

    public Conductor() {
    }

    public Conductor(String dni, String nombres, String numeroLicencia, String categoriaLicencia) {
        this.dni = dni;
        this.nombres = nombres;
        this.numeroLicencia = numeroLicencia;
        this.categoriaLicencia = categoriaLicencia;
        this.activo = true;
    }
    public Conductor(int idConductor, String dni, String nombres, String numeroLicencia, String categoriaLicencia, boolean activo) {
        this.idConductor = idConductor;
        this.dni = dni;
        this.nombres = nombres;
        this.numeroLicencia = numeroLicencia;
        this.categoriaLicencia = categoriaLicencia;
        this.activo = activo;
    }

    public boolean validarDni() {
        return dni != null && !dni.isBlank();
    }
    public boolean validar() {
        return validarDni() && nombres != null && !nombres.isBlank() && numeroLicencia != null && !numeroLicencia.isBlank()
                && categoriaLicencia != null && !categoriaLicencia.isBlank();
    }
    public int getIdConductor() {
        return idConductor;
    }

    public void setIdConductor(int idConductor) {
        this.idConductor = idConductor;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getNumeroLicencia() {
        return numeroLicencia;
    }

    public void setNumeroLicencia(String numeroLicencia) {
        this.numeroLicencia = numeroLicencia;
    }

    public String getCategoriaLicencia() {
        return categoriaLicencia;
    }

    public void setCategoriaLicencia(String categoriaLicencia) {
        this.categoriaLicencia = categoriaLicencia;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}