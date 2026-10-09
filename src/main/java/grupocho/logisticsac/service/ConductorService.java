package grupocho.logisticsac.service;

import grupocho.logisticsac.config.ConexionDB;
import grupocho.logisticsac.dao.ConductorDAO;
import grupocho.logisticsac.modelo.Conductor;
import grupocho.logisticsac.repository.ConductorRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ConductorService {
    private final ConductorRepository conductorRepository;

    public ConductorService(ConductorRepository conductorRepository) {
        this.conductorRepository = conductorRepository;
    }

    public void registrar(Conductor conductor) throws SQLException {

        if (conductor == null || !conductor.validar()) {
            throw new IllegalArgumentException("Los datos del conductor no son válidos.");
        }

        Connection conexion = null;

        try {
            conexion = ConexionDB.obtenerConexion();

            if (conductorRepository.buscarPorDni(conexion, conductor.getDni()) != null) {
                throw new IllegalArgumentException("El DNI ya existe.");
            }

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
        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("El DNI es obligatorio.");
        }

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
        if (conductor == null || !conductor.validar()) {
            throw new IllegalArgumentException("Los datos del conductor no son válidos.");
        }
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