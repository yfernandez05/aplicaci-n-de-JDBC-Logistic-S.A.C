package grupocho.logisticsac.dao;

import grupocho.logisticsac.modelo.Traslado;
import grupocho.logisticsac.modelo.DetalleTraslado;
import grupocho.logisticsac.repository.TrasladoRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TrasladoDAO implements TrasladoRepository {

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

    @Override
    public List<Traslado> listar(Connection conexion) throws SQLException {
        List<Traslado> lista = new ArrayList<>();

        String sql = """
        SELECT t.id_traslado,
               t.codigo,
               t.fecha_programada,
               t.estado,
               t.observacion,
               t.motivo_rechazo,
               ao.id_almacen AS id_origen,
               ao.codigo AS codigo_origen,
               ao.nombre AS nombre_origen,
               ad.id_almacen AS id_destino,
               ad.codigo AS codigo_destino,
               ad.nombre AS nombre_destino,
               v.id_vehiculo,
               v.placa,
               v.tipo,
               c.id_conductor,
               c.dni,
               c.nombres
        FROM traslado t
        INNER JOIN almacen ao ON t.id_almacen_origen = ao.id_almacen
        INNER JOIN almacen ad ON t.id_almacen_destino = ad.id_almacen
        INNER JOIN vehiculo v ON t.id_vehiculo = v.id_vehiculo
        INNER JOIN conductor c ON t.id_conductor = c.id_conductor
        ORDER BY t.id_traslado DESC
        """;

        try (PreparedStatement statement = conexion.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                Traslado traslado = new Traslado();

                traslado.setIdTraslado(rs.getInt("id_traslado"));
                traslado.setCodigo(rs.getString("codigo"));
                traslado.setFechaProgramada(rs.getDate("fecha_programada").toLocalDate());
                traslado.setEstado(grupocho.logisticsac.enums.EstadoTraslado.valueOf(rs.getString("estado")));
                traslado.setObservacion(rs.getString("observacion"));
                traslado.setMotivoRechazo(rs.getString("motivo_rechazo"));

                grupocho.logisticsac.modelo.Almacen origen = new grupocho.logisticsac.modelo.Almacen();
                origen.setIdAlmacen(rs.getInt("id_origen"));
                origen.setCodigo(rs.getString("codigo_origen"));
                origen.setNombre(rs.getString("nombre_origen"));

                grupocho.logisticsac.modelo.Almacen destino = new grupocho.logisticsac.modelo.Almacen();
                destino.setIdAlmacen(rs.getInt("id_destino"));
                destino.setCodigo(rs.getString("codigo_destino"));
                destino.setNombre(rs.getString("nombre_destino"));

                grupocho.logisticsac.modelo.Vehiculo vehiculo = new grupocho.logisticsac.modelo.Vehiculo();
                vehiculo.setIdVehiculo(rs.getInt("id_vehiculo"));
                vehiculo.setPlaca(rs.getString("placa"));
                vehiculo.setTipo(rs.getString("tipo"));

                grupocho.logisticsac.modelo.Conductor conductor = new grupocho.logisticsac.modelo.Conductor();
                conductor.setIdConductor(rs.getInt("id_conductor"));
                conductor.setDni(rs.getString("dni"));
                conductor.setNombres(rs.getString("nombres"));

                traslado.setAlmacenOrigen(origen);
                traslado.setAlmacenDestino(destino);
                traslado.setVehiculo(vehiculo);
                traslado.setConductor(conductor);

                lista.add(traslado);
            }
        }

        return lista;
    }


    @Override
    public List<Traslado> buscar(
            Connection conexion,
            String codigo,
            java.time.LocalDate fecha,
            grupocho.logisticsac.enums.EstadoTraslado estado,
            Integer idVehiculo,
            Integer idConductor,
            Integer idAlmacen
    ) throws SQLException {

        List<Traslado> lista = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
        SELECT t.id_traslado,
               t.codigo,
               t.fecha_programada,
               t.estado,
               t.observacion,
               t.motivo_rechazo,
               ao.id_almacen AS id_origen,
               ao.codigo AS codigo_origen,
               ao.nombre AS nombre_origen,
               ad.id_almacen AS id_destino,
               ad.codigo AS codigo_destino,
               ad.nombre AS nombre_destino,
               v.id_vehiculo,
               v.placa,
               v.tipo,
               c.id_conductor,
               c.dni,
               c.nombres
        FROM traslado t
        INNER JOIN almacen ao ON t.id_almacen_origen = ao.id_almacen
        INNER JOIN almacen ad ON t.id_almacen_destino = ad.id_almacen
        INNER JOIN vehiculo v ON t.id_vehiculo = v.id_vehiculo
        INNER JOIN conductor c ON t.id_conductor = c.id_conductor
        WHERE 1 = 1
        """);

        List<Object> parametros = new ArrayList<>();

        if (codigo != null && !codigo.isBlank()) {
            sql.append(" AND t.codigo LIKE ?");
            parametros.add("%" + codigo.trim() + "%");
        }

        if (fecha != null) {
            sql.append(" AND t.fecha_programada = ?");
            parametros.add(Date.valueOf(fecha));
        }

        if (estado != null) {
            sql.append(" AND t.estado = ?");
            parametros.add(estado.name());
        }

        if (idVehiculo != null) {
            sql.append(" AND t.id_vehiculo = ?");
            parametros.add(idVehiculo);
        }

        if (idConductor != null) {
            sql.append(" AND t.id_conductor = ?");
            parametros.add(idConductor);
        }

        if (idAlmacen != null) {
            sql.append(" AND (t.id_almacen_origen = ? OR t.id_almacen_destino = ?)");
            parametros.add(idAlmacen);
            parametros.add(idAlmacen);
        }

        sql.append(" ORDER BY t.id_traslado DESC");

        try (PreparedStatement statement = conexion.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametros.size(); i++) {
                statement.setObject(i + 1, parametros.get(i));
            }

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Traslado traslado = new Traslado();

                    traslado.setIdTraslado(rs.getInt("id_traslado"));
                    traslado.setCodigo(rs.getString("codigo"));
                    traslado.setFechaProgramada(rs.getDate("fecha_programada").toLocalDate());
                    traslado.setEstado( grupocho.logisticsac.enums.EstadoTraslado.valueOf(rs.getString("estado")));
                    traslado.setObservacion(rs.getString("observacion"));
                    traslado.setMotivoRechazo(rs.getString("motivo_rechazo"));

                    grupocho.logisticsac.modelo.Almacen origen = new grupocho.logisticsac.modelo.Almacen();

                    origen.setIdAlmacen(rs.getInt("id_origen"));
                    origen.setCodigo(rs.getString("codigo_origen"));
                    origen.setNombre(rs.getString("nombre_origen"));

                    grupocho.logisticsac.modelo.Almacen destino = new grupocho.logisticsac.modelo.Almacen();

                    destino.setIdAlmacen(rs.getInt("id_destino"));
                    destino.setCodigo(rs.getString("codigo_destino"));
                    destino.setNombre(rs.getString("nombre_destino"));

                    grupocho.logisticsac.modelo.Vehiculo vehiculo = new grupocho.logisticsac.modelo.Vehiculo();

                    vehiculo.setIdVehiculo(rs.getInt("id_vehiculo"));
                    vehiculo.setPlaca(rs.getString("placa"));
                    vehiculo.setTipo(rs.getString("tipo"));

                    grupocho.logisticsac.modelo.Conductor conductor = new grupocho.logisticsac.modelo.Conductor();

                    conductor.setIdConductor(rs.getInt("id_conductor"));
                    conductor.setDni(rs.getString("dni"));
                    conductor.setNombres(rs.getString("nombres"));

                    traslado.setAlmacenOrigen(origen);
                    traslado.setAlmacenDestino(destino);
                    traslado.setVehiculo(vehiculo);
                    traslado.setConductor(conductor);

                    lista.add(traslado);
                }
            }
        }

        return lista;
    }

}