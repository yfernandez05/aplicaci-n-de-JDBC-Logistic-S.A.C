package grupocho.logisticsac.modelo;

public class DetalleTraslado {
    private int idDetalle;
    private Producto producto;
    private double cantidad;

    public DetalleTraslado() {
    }

    public DetalleTraslado(Producto producto, double cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public DetalleTraslado(int idDetalle, Producto producto, double cantidad) {
        this.idDetalle = idDetalle;
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public boolean validarCantidad() {
        return cantidad > 0;
    }
    public boolean validar() {
        return producto != null && validarCantidad();
    }

    public int getIdDetalle() {
        return idDetalle;
    }
    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }
    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }
}