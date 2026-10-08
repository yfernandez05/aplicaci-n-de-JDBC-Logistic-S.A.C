package grupocho.logisticsac.service;

import grupocho.logisticsac.config.ConexionDB;
import grupocho.logisticsac.dao.TipoDocumentoDAO;
import grupocho.logisticsac.modelo.TipoDocumento;
import grupocho.logisticsac.repository.TipoDocumentoRepository;
import grupocho.logisticsac.validation.TipoDocumentoValidator;

import java.sql.Connection;
import java.sql.SQLException;

public class TipoDocumentoService {
    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final TipoDocumentoValidator tipoDocumentoValidator;

    public TipoDocumentoService(TipoDocumentoRepository tipoDocumentoRepository) {
        this.tipoDocumentoRepository = tipoDocumentoRepository;
        this.tipoDocumentoValidator = new TipoDocumentoValidator();
    }

    public void registrar(TipoDocumento tipoDocumento) throws SQLException {

        tipoDocumentoValidator.validar(tipoDocumento);
        Connection conexion = null;

        try {
            conexion = ConexionDB.obtenerConexion();
            conexion.setAutoCommit(false);
            tipoDocumentoRepository.insertar(conexion, tipoDocumento);
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

    public TipoDocumento buscarPorNombre(String nombre) throws SQLException {

        tipoDocumentoValidator.validarNombre(nombre);

        try (Connection conexion = ConexionDB.obtenerConexion()) {
            return tipoDocumentoRepository.buscarPorNombre(
                    conexion,
                    nombre
            );
        }
    }
}