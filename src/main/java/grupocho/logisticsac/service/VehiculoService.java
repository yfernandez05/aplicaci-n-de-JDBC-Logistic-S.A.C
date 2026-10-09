package grupocho.logisticsac.service;

import grupocho.logisticsac.config.ConexionDB;
import grupocho.logisticsac.modelo.Vehiculo;
import grupocho.logisticsac.repository.VehiculoRepository;
import grupocho.logisticsac.validation.VehiculoValidator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final VehiculoValidator vehiculoValidator;

    public VehiculoService(VehiculoRepository vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
        this.vehiculoValidator = new VehiculoValidator();
    }

    public void registrar(Vehiculo vehiculo) throws SQLException {
        vehiculoValidator.validar(vehiculo);
        Connection conexion = null;
        try {
            conexion = ConexionDB.obtenerConexion();

            if (vehiculoRepository.buscarPorPlaca(conexion, vehiculo.getPlaca()) != null) {
                throw new IllegalArgumentException("La placa ya existe.");
            }

            conexion.setAutoCommit(false);
            vehiculoRepository.insertar(conexion, vehiculo);
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

    public Vehiculo buscarPorPlaca(String placa) throws SQLException {

        if (placa == null || placa.isBlank()) {
            throw new IllegalArgumentException("La placa es obligatoria.");
        }

        try (Connection conexion = ConexionDB.obtenerConexion()) {
            return vehiculoRepository.buscarPorPlaca(conexion, placa);
        }
    }

    public List<Vehiculo> listar() throws SQLException {
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            return vehiculoRepository.listar(conexion);
        }
    }

    public void actualizar(Vehiculo vehiculo) throws SQLException {
        vehiculoValidator.validar(vehiculo);
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            vehiculoRepository.actualizar(conexion, vehiculo);
        }
    }

    public void eliminar(int idVehiculo) throws SQLException {
        if (idVehiculo <= 0) {
            throw new IllegalArgumentException("El vehículo seleccionado no es válido.");
        }
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            vehiculoRepository.eliminar(conexion, idVehiculo);
        }
    }
}