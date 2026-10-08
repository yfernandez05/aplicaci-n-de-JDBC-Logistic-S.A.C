package grupocho.logisticsac.service;

import grupocho.logisticsac.config.ConexionDB;
import grupocho.logisticsac.dao.TrasladoDAO;
import grupocho.logisticsac.enums.EstadoTraslado;
import grupocho.logisticsac.modelo.Inspeccion;
import grupocho.logisticsac.modelo.Traslado;
import grupocho.logisticsac.modelo.Usuario;
import grupocho.logisticsac.repository.TrasladoRepository;
import grupocho.logisticsac.validation.TrasladoValidator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TrasladoService {

    private final TrasladoRepository trasladoRepository;
    private final TrasladoValidator trasladoValidator;

    public TrasladoService(TrasladoRepository trasladoRepository) {
        this.trasladoRepository = trasladoRepository;
        this.trasladoValidator = new TrasladoValidator();
    }

    public void registrar(Traslado traslado) throws SQLException {

        trasladoValidator.validar(traslado);
        Connection conexion = null;

        try {
            conexion = ConexionDB.obtenerConexion();
            conexion.setAutoCommit(false);
            trasladoRepository.insertar(conexion,traslado);
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

        trasladoValidator.validarAutorizacion(traslado, responsable, inspeccion);
        traslado.marcarEnTransito(responsable);
        Connection conexion = null;

        try {
            conexion = ConexionDB.obtenerConexion();
            conexion.setAutoCommit(false);
            trasladoRepository.actualizarEstado(conexion, traslado);
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

        trasladoValidator.validarRechazo(traslado, motivo);
        traslado.rechazar(motivo);
        Connection conexion = null;

        try {
            conexion = ConexionDB.obtenerConexion();
            conexion.setAutoCommit(false);
            trasladoRepository.actualizarEstado(conexion, traslado);
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

    public List<Traslado> listar() throws SQLException {
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            return trasladoRepository.listar(conexion);
        }
    }

    public List<Traslado> buscar(
            String codigo,
            java.time.LocalDate fecha,
            EstadoTraslado estado,
            Integer idVehiculo,
            Integer idConductor,
            Integer idAlmacen
    ) throws SQLException {
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            return trasladoRepository.buscar(
                    conexion,
                    codigo,
                    fecha,
                    estado,
                    idVehiculo,
                    idConductor,
                    idAlmacen
            );
        }
    }
}