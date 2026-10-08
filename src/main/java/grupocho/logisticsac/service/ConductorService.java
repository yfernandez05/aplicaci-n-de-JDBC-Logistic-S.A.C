package grupocho.logisticsac.service;

import grupocho.logisticsac.config.ConexionDB;
import grupocho.logisticsac.dao.ConductorDAO;
import grupocho.logisticsac.modelo.Conductor;
import grupocho.logisticsac.repository.ConductorRepository;
import grupocho.logisticsac.validation.ConductorValidator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ConductorService {
    private final ConductorRepository conductorRepository;
    private final ConductorValidator conductorValidator;
    public ConductorService(ConductorRepository conductorRepository) {
        this.conductorRepository = conductorRepository;
        this.conductorValidator = new ConductorValidator();
    }

    public void registrar(Conductor conductor) throws SQLException {

        conductorValidator.validar(conductor);
        Connection conexion = null;

        try {
            conexion = ConexionDB.obtenerConexion();
            conexion.setAutoCommit(false);
            conductorRepository.insertar(conexion, conductor);
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

    public Conductor buscarPorDni(String dni) throws SQLException {
        conductorValidator.validarDni(dni);
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            return conductorRepository.buscarPorDni(conexion, dni);
        }
    }

    public List<Conductor> listar() throws SQLException {
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            return conductorRepository.listar(conexion);
        }
    }

    public void actualizar(Conductor conductor) throws SQLException {
        conductorValidator.validar(conductor);
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            conductorRepository.actualizar(conexion, conductor);
        }
    }

    public void eliminar(int idConductor) throws SQLException {
        if (idConductor <= 0) {
            throw new IllegalArgumentException("El conductor seleccionado no es válido.");
        }
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            conductorRepository.eliminar(conexion, idConductor);
        }
    }
}