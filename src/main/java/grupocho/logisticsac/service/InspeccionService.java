package grupocho.logisticsac.service;

import grupocho.logisticsac.config.ConexionDB;
import grupocho.logisticsac.dao.InspeccionDAO;
import grupocho.logisticsac.modelo.Inspeccion;
import grupocho.logisticsac.repository.InspeccionRepository;

import java.sql.Connection;
import java.sql.SQLException;

public class InspeccionService {

    private final InspeccionRepository inspeccionRepository;

    public InspeccionService(InspeccionRepository inspeccionRepository) {
        this.inspeccionRepository = inspeccionRepository;
    }

    public void registrar(Inspeccion inspeccion) throws SQLException {

        if (inspeccion == null || !inspeccion.validar()) {
            throw new IllegalArgumentException("Los datos de la inspección no son válidos.");
        }

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