package grupocho.logisticsac.modelo;

import grupocho.logisticsac.enums.Rol;

public class Usuario {
    private int idUsuario;
    private String username;
    private String passwordHash;
    private String nombreCompleto;
    private Rol rol;
    private boolean activo;

    public Usuario() {
    }

    public Usuario(String username, String passwordHash, String nombreCompleto, Rol rol) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
        this.activo = true;
    }

    public Usuario(int idUsuario, String username, String passwordHash, String nombreCompleto, Rol rol, boolean activo) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.passwordHash = passwordHash;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
        this.activo = activo;
    }

    // GET y SET

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean tieneRol(Rol rol) {
        return this.rol == rol;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
