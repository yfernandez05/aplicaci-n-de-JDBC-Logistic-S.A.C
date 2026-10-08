package grupocho.logisticsac.service;

import grupocho.logisticsac.config.ConexionDB;
import grupocho.logisticsac.dao.RecepcionDAO;
import grupocho.logisticsac.dao.TrasladoDAO;
import grupocho.logisticsac.enums.EstadoTraslado;
import grupocho.logisticsac.modelo.Recepcion;
import grupocho.logisticsac.repository.RecepcionRepository;
import grupocho.logisticsac.repository.TrasladoRepository;
import grupocho.logisticsac.validation.RecepcionValidator;

import java.sql.Connection;
import java.sql.SQLException;

public class RecepcionService {

    private final RecepcionRepository recepcionRepository;
    private final TrasladoRepository trasladoRepository;
    private final RecepcionValidator recepcionValidator;

    public RecepcionService( RecepcionRepository recepcionRepository, TrasladoRepository trasladoRepository) {
        this.recepcionRepository = recepcionRepository;
        this.trasladoRepository = trasladoRepository;
        this.recepcionValidator = new RecepcionValidator();
    }

    public void registrar(Recepcion recepcion) throws SQLException {

        recepcionValidator.validar(recepcion);
        recepcionValidator.validarEstadoTraslado(recepcion);
        if (recepcion.tieneObservaciones()) {
            recepcion.getTraslado().setEstado(EstadoTraslado.RECIBIDO_CON_OBSERVACIONES);
        } else {
            recepcion.getTraslado().setEstado(EstadoTraslado.RECIBIDO);
        }
        Connection conexion = null;

        try {
            conexion = ConexionDB.obtenerConexion();
            conexion.setAutoCommit(false);
            recepcionRepository.insertar(conexion, recepcion);
            trasladoRepository.actualizarEstado(conexion, recepcion.getTraslado());
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