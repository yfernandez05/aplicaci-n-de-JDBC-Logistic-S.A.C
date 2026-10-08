package grupocho.logisticsac.modelo;

public class Almacen {
    private int idAlmacen;
    private String codigo;
    private String nombre;
    private String direccion;
    private boolean activo;

    public Almacen() {
    }

    public Almacen(String codigo, String nombre, String direccion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.direccion = direccion;
        this.activo = true;
    }

    public Almacen(int idAlmacen, String codigo, String nombre, String direccion, boolean activo) {
        this.idAlmacen = idAlmacen;
        this.codigo = codigo;
        this.nombre = nombre;
        this.direccion = direccion;
        this.activo = activo;
    }


    public int getIdAlmacen() {
        return idAlmacen;
    }

    public void setIdAlmacen(int idAlmacen) {
        this.idAlmacen = idAlmacen;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre;
    }
}
