package grupocho.logisticsac.service;

import grupocho.logisticsac.config.ConexionDB;
import grupocho.logisticsac.dao.TrasladoDAO;
import grupocho.logisticsac.enums.EstadoTraslado;
import grupocho.logisticsac.modelo.Inspeccion;
import grupocho.logisticsac.modelo.Traslado;
import grupocho.logisticsac.modelo.Usuario;

import java.sql.Connection;
import java.sql.SQLException;

public class TrasladoService {

    private final TrasladoDAO trasladoDAO;

    public TrasladoService() {
        this.trasladoDAO = new TrasladoDAO();
    }

    public void registrar(Traslado traslado) throws SQLException {

        if (traslado == null || !traslado.validar()) {
            throw new IllegalArgumentException("Los datos del traslado no son válidos.");
        }

        Connection conexion = null;

        try {
            conexion = ConexionDB.obtenerConexion();
            conexion.setAutoCommit(false);
            trasladoDAO.insertar(conexion,traslado);
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

    public void autorizarSalida(Traslado traslado, Usuario responsable, Inspeccion inspeccion) throws SQLException {
        if (traslado == null) {
            throw new IllegalArgumentException("El traslado no puede ser nulo.");
        }

        if (responsable == null) {
            throw new IllegalArgumentException("El responsable de salida es obligatorio.");
        }

        if (inspeccion == null) {
            throw new IllegalArgumentException("La inspección es obligatoria para autorizar la salida.");
        }

        if (traslado.getEstado() != EstadoTraslado.PROGRAMADO) {
            throw new IllegalStateException("Solo se puede autorizar un traslado PROGRAMADO.");
        }

        if (!inspeccion.puedeAutorizar()) {
            throw new IllegalStateException(
                    "El traslado no puede ser autorizado porque la inspección no es conforme."
            );
        }

        traslado.marcarEnTransito(responsable);

        Connection conexion = null;

        try {
            conexion = ConexionDB.obtenerConexion();
            conexion.setAutoCommit(false);

            trasladoDAO.actualizarEstado(conexion, traslado);

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

    public void rechazar(Traslado traslado, String motivo) throws SQLException {
        if (traslado == null) {
            throw new IllegalArgumentException("El traslado no puede ser nulo.");
        }

        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("El motivo de rechazo es obligatorio.");
        }

        if (traslado.getEstado() != EstadoTraslado.PROGRAMADO) {
            throw new IllegalStateException("Solo se puede rechazar un traslado PROGRAMADO.");
        }

        traslado.rechazar(motivo);

        Connection conexion = null;

        try {
            conexion = ConexionDB.obtenerConexion();
            conexion.setAutoCommit(false);

            trasladoDAO.actualizarEstado(conexion, traslado);

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