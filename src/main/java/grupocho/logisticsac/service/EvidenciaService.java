package grupocho.logisticsac.service;

import grupocho.logisticsac.config.ConexionDB;
import grupocho.logisticsac.dao.EvidenciaDAO;
import grupocho.logisticsac.modelo.Evidencia;
import grupocho.logisticsac.repository.EvidenciaRepository;
import grupocho.logisticsac.validation.EvidenciaValidator;

import java.sql.Connection;
import java.sql.SQLException;

public class EvidenciaService {

    private final EvidenciaRepository evidenciaRepository;
    private final EvidenciaValidator evidenciaValidator;

    public EvidenciaService(EvidenciaRepository evidenciaRepository) {
        this.evidenciaRepository = evidenciaRepository;
        this.evidenciaValidator = new EvidenciaValidator();
    }

    public void registrar(Evidencia evidencia) throws SQLException {
        evidenciaValidator.validar(evidencia);
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