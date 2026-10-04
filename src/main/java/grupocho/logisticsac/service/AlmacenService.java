package grupocho.logisticsac.service;

import grupocho.logisticsac.config.ConexionDB;
import grupocho.logisticsac.dao.AlmacenDAO;
import grupocho.logisticsac.modelo.Almacen;
import grupocho.logisticsac.repository.AlmacenRepository;
import grupocho.logisticsac.repository.UsuarioRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class AlmacenService {
    private final AlmacenRepository almacenRepository;

    public AlmacenService(AlmacenRepository almacenRepository) {
        this.almacenRepository = almacenRepository;
    }

    public void registrar(Almacen almacen) throws SQLException {

        if (almacen == null || !almacen.validar()) {
            throw new IllegalArgumentException("Los datos del almacén no son válidos.");
        }

        Connection conexion = null;

        try {
            conexion = ConexionDB.obtenerConexion();

            conexion.setAutoCommit(false);

            almacenRepository.insertar(conexion, almacen);

            conexion.commit();

        } catch (SQLException e) {

            if (conexion != null) {
                try {
                    conexion.rollback();
                } catch (SQLException rollbackException) {
                    e.addSuppressed(rollbackException);
                }
            }

            throw e;

        } finally {
            if (conexion != null) {
                conexion.setAutoCommit(true);
                conexion.close();
            }
        }
    }

    public Almacen buscarPorCodigo(String codigo) throws SQLException {

        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del almacén es obligatorio.");
        }

        try (Connection conexion = ConexionDB.obtenerConexion()) {
            return almacenRepository.buscarPorCodigo(conexion, codigo);
        }
    }

    public List<Almacen> listar() throws SQLException {
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            return almacenRepository.listar(conexion);
        }
    }

    public void actualizar(Almacen almacen) throws SQLException {
        if (almacen == null || !almacen.validar()) {
            throw new IllegalArgumentException("Los datos del almacén no son válidos.");
        }
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            almacenRepository.actualizar(conexion, almacen);
        }
    }

    public void eliminar(int idAlmacen) throws SQLException {
        if (idAlmacen <= 0) {
            throw new IllegalArgumentException("El almacén seleccionado no es válido.");
        }
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            almacenRepository.eliminar(conexion, idAlmacen);
        }
    }
}