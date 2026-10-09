package grupocho.logisticsac.service;

import grupocho.logisticsac.config.ConexionDB;
import grupocho.logisticsac.enums.AmbitoDocumento;
import grupocho.logisticsac.modelo.TipoDocumento;
import grupocho.logisticsac.repository.TipoDocumentoRepository;
import grupocho.logisticsac.validation.TipoDocumentoValidator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

            if (tipoDocumentoRepository.buscarPorNombre(conexion, tipoDocumento.getNombre()) != null) {
                throw new IllegalArgumentException("El tipo de documento ya existe.");
            }

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

    public List<TipoDocumento> listar() throws SQLException {
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            return tipoDocumentoRepository.listar(conexion);
        }
    }

    public List<TipoDocumento> listarPorAmbito(AmbitoDocumento ambito) throws SQLException {
        List<TipoDocumento> lista = new ArrayList<>();

        for (TipoDocumento tipo : listar()) {
            if (tipo.getAmbito() == ambito) {
                lista.add(tipo);
            }
        }

        return lista;
    }
}
