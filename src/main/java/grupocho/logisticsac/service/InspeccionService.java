package grupocho.logisticsac.service;

import grupocho.logisticsac.config.ConexionDB;
import grupocho.logisticsac.modelo.Evidencia;
import grupocho.logisticsac.modelo.Inspeccion;
import grupocho.logisticsac.modelo.Precinto;
import grupocho.logisticsac.repository.EvidenciaRepository;
import grupocho.logisticsac.repository.InspeccionRepository;
import grupocho.logisticsac.repository.PrecintoRepository;

import java.sql.Connection;
import java.sql.SQLException;

public class InspeccionService {

    private final InspeccionRepository inspeccionRepository;
    private final EvidenciaRepository evidenciaRepository;
    private final PrecintoRepository precintoRepository;

    public InspeccionService(InspeccionRepository inspeccionRepository,
                             EvidenciaRepository evidenciaRepository,
                             PrecintoRepository precintoRepository) {
        this.inspeccionRepository = inspeccionRepository;
        this.evidenciaRepository = evidenciaRepository;
        this.precintoRepository = precintoRepository;
    }

    // Guarda la inspeccion junto con sus evidencias y el precinto en una sola transaccion.
    // El precinto puede ser null cuando la inspeccion no es conforme.
    public void registrar(Inspeccion inspeccion, Precinto precinto) throws SQLException {

        if (inspeccion == null || !inspeccion.validar()) {
            throw new IllegalArgumentException("Los datos de la inspección no son válidos.");
        }

        if (inspeccion.puedeAutorizar() && (precinto == null || !precinto.validar())) {
            throw new IllegalArgumentException("El número de precinto es obligatorio cuando la inspección es conforme.");
        }

        Connection conexion = null;

        try {
            conexion = ConexionDB.obtenerConexion();

            int idTraslado = inspeccion.getTraslado().getIdTraslado();
            if (inspeccionRepository.buscarPorTraslado(conexion, idTraslado) != null) {
                throw new IllegalStateException("El traslado ya tiene una inspección registrada.");
            }

            conexion.setAutoCommit(false);
            inspeccionRepository.insertar(conexion,inspeccion);

            for (Evidencia evidencia : inspeccion.getEvidencias()) {
                evidenciaRepository.insertar(conexion, evidencia);
            }

            if (precinto != null) {
                precintoRepository.insertar(conexion, precinto);
            }

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

    public Inspeccion buscarPorTraslado(int idTraslado) throws SQLException {
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            Inspeccion inspeccion = inspeccionRepository.buscarPorTraslado(conexion, idTraslado);

            if (inspeccion != null) {
                inspeccion.setEvidencias(evidenciaRepository.listarPorInspeccion(conexion, inspeccion));
            }

            return inspeccion;
        }
    }
}
