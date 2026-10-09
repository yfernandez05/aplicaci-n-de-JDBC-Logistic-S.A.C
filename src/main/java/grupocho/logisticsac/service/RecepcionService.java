package grupocho.logisticsac.service;

import grupocho.logisticsac.config.ConexionDB;
import grupocho.logisticsac.enums.EstadoTraslado;
import grupocho.logisticsac.modelo.Precinto;
import grupocho.logisticsac.modelo.Recepcion;
import grupocho.logisticsac.repository.PrecintoRepository;
import grupocho.logisticsac.repository.RecepcionRepository;
import grupocho.logisticsac.repository.TrasladoRepository;

import java.sql.Connection;
import java.sql.SQLException;

public class RecepcionService {

    private final RecepcionRepository recepcionRepository;
    private final TrasladoRepository trasladoRepository;
    private final PrecintoRepository precintoRepository;

    public RecepcionService(
            RecepcionRepository recepcionRepository,
            TrasladoRepository trasladoRepository,
            PrecintoRepository precintoRepository) {
        this.recepcionRepository = recepcionRepository;
        this.trasladoRepository = trasladoRepository;
        this.precintoRepository = precintoRepository;
    }

    // El numero de precinto recibido se compara con el registrado en garita.
    public void registrar(Recepcion recepcion, String numeroPrecinto) throws SQLException {

        if (recepcion == null || recepcion.getTraslado() == null || recepcion.getDespachador() == null) {
            throw new IllegalArgumentException("Los datos de la recepción no son válidos.");
        }

        if (numeroPrecinto == null || numeroPrecinto.isBlank()) {
            throw new IllegalArgumentException("Ingrese el número de precinto recibido.");
        }

        if (recepcion.getTraslado().getEstado() != EstadoTraslado.EN_TRANSITO) {
            throw new IllegalStateException("Solo se puede registrar la recepción de un traslado EN_TRANSITO.");
        }

        Connection conexion = null;

        try {
            conexion = ConexionDB.obtenerConexion();

            Precinto precinto = precintoRepository.buscarPorTraslado(conexion, recepcion.getTraslado().getIdTraslado());
            recepcion.setPrecintoConforme(precinto != null && precinto.coincideCon(numeroPrecinto));

            if (!recepcion.validar()) {
                if (!recepcion.isPrecintoConforme()) {
                    throw new IllegalArgumentException("El precinto no coincide con el registrado en garita. Describa la incidencia en la observación.");
                }
                throw new IllegalArgumentException("La carga no es conforme. Describa la incidencia en la observación.");
            }

            if (recepcion.tieneObservaciones()) {
                recepcion.getTraslado().setEstado(EstadoTraslado.RECIBIDO_CON_OBSERVACIONES);
            } else {
                recepcion.getTraslado().setEstado(EstadoTraslado.RECIBIDO);
            }

            conexion.setAutoCommit(false);
            recepcionRepository.insertar(conexion, recepcion);
            trasladoRepository.actualizarEstado(conexion, recepcion.getTraslado());
            conexion.commit();

        } catch (SQLException e) {

            recepcion.getTraslado().setEstado(EstadoTraslado.EN_TRANSITO);

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

    public Recepcion buscarPorTraslado(int idTraslado) throws SQLException {
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            return recepcionRepository.buscarPorTraslado(conexion, idTraslado);
        }
    }
}
