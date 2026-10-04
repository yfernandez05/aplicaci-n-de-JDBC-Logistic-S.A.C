package grupocho.logisticsac.modelo;

public class Producto {
    private int idProducto;
    private String codigo;
    private String descripcion;
    private String unidadMedida;
    private boolean activo;

    public Producto() {
    }
    public Producto(String codigo, String descripcion, String unidadMedida) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.unidadMedida = unidadMedida;
        this.activo = true;
    }

    public Producto(int idProducto, String codigo, String descripcion, String unidadMedida, boolean activo) {
        this.idProducto = idProducto;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.unidadMedida = unidadMedida;
        this.activo = activo;
    }

    public boolean validarCodigo() {
        return codigo != null && !codigo.isBlank();
    }

    public boolean validar() {
        return validarCodigo()
                && descripcion != null && !descripcion.isBlank()
                && unidadMedida != null && !unidadMedida.isBlank();
    }
    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return codigo + " - " + descripcion;
    }
}