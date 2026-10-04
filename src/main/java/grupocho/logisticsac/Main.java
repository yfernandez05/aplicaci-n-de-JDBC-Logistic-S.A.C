package grupocho.logisticsac;

import grupocho.logisticsac.config.ConexionDB;
import grupocho.logisticsac.enums.AmbitoDocumento;
import grupocho.logisticsac.enums.EstadoTraslado;
import grupocho.logisticsac.enums.ResultadoInspeccion;
import grupocho.logisticsac.enums.Rol;
import grupocho.logisticsac.modelo.*;
import grupocho.logisticsac.service.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws SQLException {

        Almacen almacenOrigen = new Almacen();
        almacenOrigen.setIdAlmacen(1);

        Almacen almacenDestino = new Almacen();
        almacenDestino.setIdAlmacen(4);

        Producto producto = new Producto();
        producto.setIdProducto(1);

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setIdVehiculo(1);

        Conductor conductor = new Conductor();
        conductor.setIdConductor(1);

        TrasladoService trasladoService = new TrasladoService();
        InspeccionService inspeccionService = new InspeccionService();
        RecepcionService recepcionService = new RecepcionService();

        System.out.println();
        System.out.println("==============================================");
        System.out.println("1. REGISTRO DE TRASLADO");
        System.out.println("==============================================");

        Traslado traslado3 = new Traslado(
                "TR-2026-018",
                LocalDate.now(),
                almacenOrigen,
                almacenDestino,
                vehiculo,
                conductor
        );

        traslado3.agregarDetalle(new DetalleTraslado(producto, 10));

        trasladoService.registrar(traslado3);

        System.out.println("Traslado registrado.");
        System.out.println("ID: " + traslado3.getIdTraslado());
        System.out.println("Código: " + traslado3.getCodigo());
        System.out.println("Estado: " + traslado3.getEstado());


        System.out.println();
        System.out.println("==============================================");
        System.out.println("2. INSPECCIÓN DEL VEHÍCULO");
        System.out.println("==============================================");

        Usuario vigilante = new Usuario();
        vigilante.setIdUsuario(1);

        Inspeccion inspeccion = new Inspeccion();

        inspeccion.setTraslado(traslado3);
        inspeccion.setVigilante(vigilante);
        inspeccion.setResultado(ResultadoInspeccion.CONFORME);
        inspeccion.setCargaConforme(true);
        inspeccion.setObservacion(null);

        Evidencia evidencia = new Evidencia();

        evidencia.setRutaArchivo("evidencias/TR-2026-015/inspeccion.jpg");

        evidencia.setDescripcion("Fotografía de la inspección del vehículo.");

        inspeccion.agregarEvidencia(evidencia);

        inspeccionService.registrar(inspeccion);

        System.out.println("Inspección registrada.");
        System.out.println("Resultado: " + inspeccion.getResultado());
        System.out.println("Carga conforme: " + inspeccion.isCargaConforme());
        System.out.println("Evidencias: " + inspeccion.getEvidencias().size());
        System.out.println("¿Puede autorizar?: " + inspeccion.puedeAutorizar());

        System.out.println();
        System.out.println("==============================================");
        System.out.println("3. AUTORIZACIÓN DE SALIDA");
        System.out.println("==============================================");

        Usuario responsable = new Usuario();
        responsable.setIdUsuario(1);

        trasladoService.autorizarSalida(traslado3,responsable,inspeccion);

        System.out.println("Salida autorizada.");
        System.out.println("Estado: " + traslado3.getEstado());
        System.out.println("Fecha/hora salida: " + traslado3.getFechaHoraSalida());


        System.out.println();
        System.out.println("==============================================");
        System.out.println("4. RECEPCIÓN CONFORME");
        System.out.println("==============================================");

        Recepcion recepcion = new Recepcion(traslado3,responsable);

        recepcion.setPrecintoConforme(true);
        recepcion.setCargaConforme(true);
        recepcion.setObservacion(null);

        recepcionService.registrar(recepcion);

        System.out.println("Recepción registrada.");
        System.out.println("Precinto conforme: " + recepcion.isPrecintoConforme());
        System.out.println("Carga conforme: " + recepcion.isCargaConforme());
        System.out.println("Estado final: " + traslado3.getEstado());


        System.out.println();
        System.out.println("==============================================");
        System.out.println("5. SEGUNDO TRASLADO PARA PRUEBA");
        System.out.println("==============================================");

        Traslado trasladoObservado = new Traslado(
                "TR-2026-019",
                LocalDate.now(),
                almacenOrigen,
                almacenDestino,
                vehiculo,
                conductor
        );

        trasladoObservado.agregarDetalle(new DetalleTraslado(producto, 15));

        trasladoService.registrar(trasladoObservado);

        System.out.println("Traslado registrado.");
        System.out.println("ID: " + trasladoObservado.getIdTraslado());
        System.out.println("Código: " + trasladoObservado.getCodigo());
        System.out.println("Estado: " + trasladoObservado.getEstado());


        System.out.println();
        System.out.println("==============================================");
        System.out.println("6. INSPECCIÓN DEL SEGUNDO TRASLADO");
        System.out.println("==============================================");

        Usuario vigilanteObservado = new Usuario();
        vigilanteObservado.setIdUsuario(1);

        Inspeccion inspeccionObservada = new Inspeccion();

        inspeccionObservada.setTraslado(trasladoObservado);
        inspeccionObservada.setVigilante(vigilanteObservado);
        inspeccionObservada.setResultado(ResultadoInspeccion.CONFORME);
        inspeccionObservada.setCargaConforme(true);
        inspeccionObservada.setObservacion(null);

        Evidencia evidenciaObservada = new Evidencia();

        evidenciaObservada.setRutaArchivo("evidencias/TR-2026-016/inspeccion.jpg");

        evidenciaObservada.setDescripcion("Fotografía de la inspección del vehículo.");

        inspeccionObservada.agregarEvidencia(evidenciaObservada);

        inspeccionService.registrar(inspeccionObservada);

        System.out.println("Inspección registrada.");
        System.out.println("Resultado: " + inspeccionObservada.getResultado());
        System.out.println("Carga conforme: " + inspeccionObservada.isCargaConforme());
        System.out.println("Evidencias: " + inspeccionObservada.getEvidencias().size());
        System.out.println("¿Puede autorizar?: " + inspeccionObservada.puedeAutorizar());


        System.out.println();
        System.out.println("==============================================");
        System.out.println("7. AUTORIZACIÓN DEL SEGUNDO TRASLADO");
        System.out.println("==============================================");

        Usuario responsableObservado = new Usuario();
        responsableObservado.setIdUsuario(1);

        trasladoService.autorizarSalida(trasladoObservado,responsableObservado,inspeccionObservada);

        System.out.println("Salida autorizada.");
        System.out.println("Estado: " + trasladoObservado.getEstado());
        System.out.println("Fecha/hora salida: " + trasladoObservado.getFechaHoraSalida());


        System.out.println();
        System.out.println("==============================================");
        System.out.println("8. RECEPCIÓN CON OBSERVACIONES");
        System.out.println("==============================================");

        Recepcion recepcionObservada = new Recepcion(trasladoObservado,responsableObservado
        );

        recepcionObservada.setPrecintoConforme(false);
        recepcionObservada.setCargaConforme(true);
        recepcionObservada.setObservacion("El número de precinto no coincide con el registrado.");

        recepcionService.registrar(recepcionObservada);

        System.out.println("Recepción con observaciones registrada.");
        System.out.println("Precinto conforme: " + recepcionObservada.isPrecintoConforme());
        System.out.println("Carga conforme: " + recepcionObservada.isCargaConforme());
        System.out.println("Observación: " + recepcionObservada.getObservacion());
        System.out.println("Estado final: " + trasladoObservado.getEstado());



    }
}