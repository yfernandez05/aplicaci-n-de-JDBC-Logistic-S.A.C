package grupocho.logisticsac.service;

import grupocho.logisticsac.config.ConexionDB;
import grupocho.logisticsac.dao.EvidenciaDAO;
import grupocho.logisticsac.modelo.Evidencia;
import grupocho.logisticsac.repository.EvidenciaRepository;

import java.sql.Connection;
import java.sql.SQLException;

public class EvidenciaService {

    private final EvidenciaRepository evidenciaRepository;

    public EvidenciaService(EvidenciaRepository evidenciaRepository) {
        this.evidenciaRepository = evidenciaRepository;
    }

    public void registrar(Evidencia evidencia) throws SQLException {
        if (evidencia == null || !evidencia.validar()) {
            throw new IllegalArgumentException("Los datos de la evidencia no son válidos.");
        }

        Connection conexion = null;

        try {
            conexion = ConexionDB.obtenerConexion();
            conexion.setAutoCommit(false);
            evidenciaRepository.insertar(conexion, evidencia);
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