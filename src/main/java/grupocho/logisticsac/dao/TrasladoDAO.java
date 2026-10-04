package grupocho.logisticsac.dao;

import grupocho.logisticsac.modelo.Traslado;
import grupocho.logisticsac.modelo.DetalleTraslado;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Timestamp;

public class TrasladoDAO {

    public void insertar(Connection conexion, Traslado traslado) throws SQLException {

        String sqlTraslado = """
                INSERT INTO traslado (codigo, fecha_programada, estado, observacion, motivo_rechazo,fecha_hora_salida, 
                id_almacen_origen, id_almacen_destino, id_vehiculo, id_conductor, id_responsable_salida)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        String sqlDetalle = """
                INSERT INTO detalle_traslado (id_traslado, id_producto, cantidad) VALUES (?, ?, ?)
                """;

        int idTrasladoGenerado;

        try (PreparedStatement psTraslado = conexion.prepareStatement(sqlTraslado,PreparedStatement.RETURN_GENERATED_KEYS)) {

            psTraslado.setString(1,traslado.getCodigo());
            psTraslado.setDate(2,Date.valueOf(traslado.getFechaProgramada()));
            psTraslado.setString(3,traslado.getEstado().name());
            psTraslado.setString(4,traslado.getObservacion());
            psTraslado.setString(5,traslado.getMotivoRechazo());

            if (traslado.getFechaHoraSalida() != null) {
                psTraslado.setTimestamp(6,Timestamp.valueOf(traslado.getFechaHoraSalida()));
            } else {
                psTraslado.setTimestamp(6, null);
            }

            psTraslado.setInt(7,traslado.getAlmacenOrigen().getIdAlmacen());
            psTraslado.setInt(8,traslado.getAlmacenDestino().getIdAlmacen());
            psTraslado.setInt(9,traslado.getVehiculo().getIdVehiculo());
            psTraslado.setInt(10,traslado.getConductor().getIdConductor());

            if (traslado.getResponsableSalida() != null) {
                psTraslado.setInt(11,traslado.getResponsableSalida().getIdUsuario());
            } else {
                psTraslado.setNull(11,java.sql.Types.INTEGER);
            }

            psTraslado.executeUpdate();

            try (ResultSet rs = psTraslado.getGeneratedKeys()) {
                if (!rs.next()) {
                    throw new SQLException("No se pudo obtener el ID del traslado.");
                }
                idTrasladoGenerado = rs.getInt(1);
            }
        }

        try (PreparedStatement psDetalle = conexion.prepareStatement(sqlDetalle)) {
            for (DetalleTraslado detalle : traslado.getDetalles()) {
                psDetalle.setInt(1,idTrasladoGenerado);
                psDetalle.setInt(2,detalle.getProducto().getIdProducto());
                psDetalle.setDouble(3,detalle.getCantidad());
                psDetalle.addBatch();
            }
            psDetalle.executeBatch();
        }

        traslado.setIdTraslado(idTrasladoGenerado);
    }

    public void actualizarEstado(Connection conexion, Traslado traslado) throws SQLException {
        String sql = """
            UPDATE traslado
            SET estado = ?, fecha_hora_salida = ?, motivo_rechazo = ?, id_responsable_salida = ?
            WHERE id_traslado = ?
            """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, traslado.getEstado().name());

            if (traslado.getFechaHoraSalida() != null) {
                ps.setTimestamp(2, Timestamp.valueOf(traslado.getFechaHoraSalida()));
            } else {
                ps.setTimestamp(2, null);
            }

            ps.setString(3, traslado.getMotivoRechazo());

            if (traslado.getResponsableSalida() != null) {
                ps.setInt(4, traslado.getResponsableSalida().getIdUsuario());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }

            ps.setInt(5, traslado.getIdTraslado());

            ps.executeUpdate();
        }
    }


}