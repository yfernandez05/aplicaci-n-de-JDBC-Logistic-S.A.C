package grupocho.logisticsac.service;

import grupocho.logisticsac.config.ConexionDB;
import grupocho.logisticsac.enums.AmbitoDocumento;
import grupocho.logisticsac.modelo.Conductor;
import grupocho.logisticsac.modelo.Documento;
import grupocho.logisticsac.modelo.TipoDocumento;
import grupocho.logisticsac.modelo.Traslado;
import grupocho.logisticsac.modelo.Vehiculo;
import grupocho.logisticsac.repository.DocumentoRepository;
import grupocho.logisticsac.repository.TipoDocumentoRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DocumentoService {
    private final DocumentoRepository documentoRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;

    public DocumentoService(DocumentoRepository documentoRepository, TipoDocumentoRepository tipoDocumentoRepository) {
        this.documentoRepository = documentoRepository;
        this.tipoDocumentoRepository = tipoDocumentoRepository;
    }

    public void registrarParaVehiculo(Documento documento, Vehiculo vehiculo) throws SQLException {
        if (vehiculo == null) {
            throw new IllegalArgumentException("Seleccione un vehículo.");
        }
        registrar(documento, AmbitoDocumento.VEHICULO, vehiculo.getIdVehiculo());
    }

    public void registrarParaConductor(Documento documento, Conductor conductor) throws SQLException {
        if (conductor == null) {
            throw new IllegalArgumentException("Seleccione un conductor.");
        }
        registrar(documento, AmbitoDocumento.CONDUCTOR, conductor.getIdConductor());
    }

    private void registrar(Documento documento, AmbitoDocumento ambito, int idPropietario) throws SQLException {

        if (documento == null || !documento.validar()) {
            throw new IllegalArgumentException("Los datos del documento no son válidos.");
        }

        if (documento.getTipoDocumento().getAmbito() != ambito) {
            throw new IllegalArgumentException("El tipo de documento no corresponde a " + ambito + ".");
        }

        documento.actualizarEstado(LocalDate.now());

        Connection conexion = null;

        try {
            conexion = ConexionDB.obtenerConexion();
            conexion.setAutoCommit(false);
            documentoRepository.insertar(conexion, documento);

            if (ambito == AmbitoDocumento.VEHICULO) {
                documentoRepository.vincularVehiculo(conexion, idPropietario, documento.getIdDocumento());
            } else {
                documentoRepository.vincularConductor(conexion, idPropietario, documento.getIdDocumento());
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

    public Documento buscarPorNumero(String numero) throws SQLException {

        if (numero == null || numero.isBlank()) {
            throw new IllegalArgumentException( "El número del documento es obligatorio." );
        }

        try (Connection conexion = ConexionDB.obtenerConexion()) {
            return documentoRepository.buscarPorNumero(conexion,numero);
        }

    }

    public List<Documento> listarPorVehiculo(Vehiculo vehiculo) throws SQLException {
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            List<Documento> documentos = documentoRepository.listarPorVehiculo(conexion, vehiculo.getIdVehiculo());
            actualizarEstados(documentos);
            return documentos;
        }
    }

    public List<Documento> listarPorConductor(Conductor conductor) throws SQLException {
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            List<Documento> documentos = documentoRepository.listarPorConductor(conexion, conductor.getIdConductor());
            actualizarEstados(documentos);
            return documentos;
        }
    }

    // Lista de verificacion de garita: un registro por cada tipo de documento obligatorio.
    // Los que faltan quedan como OBSERVADO y los que ya vencieron como VENCIDO.
    public List<Documento> verificarDocumentos(Traslado traslado) throws SQLException {
        if (traslado == null) {
            throw new IllegalArgumentException("El traslado no puede ser nulo.");
        }

        List<Documento> verificados = new ArrayList<>();
        LocalDate hoy = LocalDate.now();

        try (Connection conexion = ConexionDB.obtenerConexion()) {
            List<Documento> delVehiculo = documentoRepository.listarPorVehiculo(conexion, traslado.getVehiculo().getIdVehiculo());
            List<Documento> delConductor = documentoRepository.listarPorConductor(conexion, traslado.getConductor().getIdConductor());
            List<Documento> delTraslado = documentoRepository.listarPorTraslado(conexion, traslado.getIdTraslado());

            for (TipoDocumento tipo : tipoDocumentoRepository.listar(conexion)) {
                if (!tipo.isObligatorio()) {
                    continue;
                }

                List<Documento> registrados;
                if (tipo.getAmbito() == AmbitoDocumento.VEHICULO) {
                    registrados = delVehiculo;
                } else if (tipo.getAmbito() == AmbitoDocumento.CONDUCTOR) {
                    registrados = delConductor;
                } else {
                    registrados = delTraslado;
                }

                Documento documento = buscarMasReciente(registrados, tipo);

                if (documento == null) {
                    documento = new Documento("-", null, null, "OBSERVADO", tipo);
                    documento.setObservacion("Documento faltante");
                } else {
                    documento.actualizarEstado(hoy);
                }

                verificados.add(documento);
            }
        }

        return verificados;
    }

    // si hay varios documentos del mismo tipo se toma el de vencimiento mas lejano
    private Documento buscarMasReciente(List<Documento> documentos, TipoDocumento tipo) {
        Documento masReciente = null;

        for (Documento documento : documentos) {
            if (documento.getTipoDocumento().getIdTipoDocumento() != tipo.getIdTipoDocumento()) {
                continue;
            }

            if (masReciente == null
                    || documento.getFechaVencimiento().compareTo(masReciente.getFechaVencimiento()) > 0) {
                masReciente = documento;
            }
        }

        return masReciente;
    }

    private void actualizarEstados(List<Documento> documentos) {
        LocalDate hoy = LocalDate.now();
        for (Documento documento : documentos) {
            documento.actualizarEstado(hoy);
        }
    }
}
