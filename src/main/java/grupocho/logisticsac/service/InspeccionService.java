package grupocho.logisticsac.service;

import grupocho.logisticsac.config.ConexionDB;
import grupocho.logisticsac.dao.InspeccionDAO;
import grupocho.logisticsac.modelo.Inspeccion;
import grupocho.logisticsac.repository.InspeccionRepository;
import grupocho.logisticsac.validation.InspeccionValidator;

import java.sql.Connection;
import java.sql.SQLException;

public class InspeccionService {

    private final InspeccionRepository inspeccionRepository;
    private final InspeccionValidator inspeccionValidator;

    public InspeccionService(InspeccionRepository inspeccionRepository) {
        this.inspeccionRepository = inspeccionRepository;
        this.inspeccionValidator = new InspeccionValidator();
    }

    public void registrar(Inspeccion inspeccion) throws SQLException {
        inspeccionValidator.validar(inspeccion);
        Connection conexion = null;
        try {
            conexion = ConexionDB.obtenerConexion();
            conexion.setAutoCommit(false);
            inspeccionRepository.insertar(conexion,inspeccion);
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