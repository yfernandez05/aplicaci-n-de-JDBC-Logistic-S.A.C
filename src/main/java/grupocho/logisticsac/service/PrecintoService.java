package grupocho.logisticsac.service;

import grupocho.logisticsac.config.ConexionDB;
import grupocho.logisticsac.dao.PrecintoDAO;
import grupocho.logisticsac.modelo.Precinto;
import grupocho.logisticsac.repository.PrecintoRepository;
import grupocho.logisticsac.validation.PrecintoValidator;

import java.sql.Connection;
import java.sql.SQLException;

public class PrecintoService {

    private final PrecintoRepository precintoRepository;
    private final PrecintoValidator precintoValidator;

    public PrecintoService(PrecintoRepository precintoRepository) {
        this.precintoRepository = precintoRepository;
        this.precintoValidator = new PrecintoValidator();
    }

    public void registrar(Precinto precinto) throws SQLException {

        precintoValidator.validar(precinto);
        Connection conexion = null;

        try {
            conexion = ConexionDB.obtenerConexion();
            conexion.setAutoCommit(false);
            precintoRepository.insertar(conexion, precinto);
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
}