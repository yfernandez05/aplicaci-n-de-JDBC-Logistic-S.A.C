-- Proyecto: Sistema de Control de Salida de Vehículos

DROP DATABASE IF EXISTS logistic_sac;
CREATE DATABASE logistic_sac
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
    
USE logistic_sac;

CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nombre_completo VARCHAR(120) NOT NULL,
    rol VARCHAR(30) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT uq_usuario_username UNIQUE (username),
    CONSTRAINT chk_usuario_rol CHECK (
        rol IN (
            'ADMINISTRADOR',
            'DESPACHADOR',
            'VIGILANTE',
            'JEFE_SEGURIDAD'
        )
    )
) ENGINE=InnoDB;

CREATE TABLE almacen (
    id_almacen INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(30) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(200) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT uq_almacen_codigo UNIQUE (codigo)
) ENGINE=InnoDB;

CREATE TABLE producto (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL,
    descripcion VARCHAR(200) NOT NULL,
    unidad_medida VARCHAR(30) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT uq_producto_codigo UNIQUE (codigo)
) ENGINE=InnoDB;

CREATE TABLE tipo_documento (
    id_tipo_documento INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    ambito VARCHAR(20) NOT NULL,
    obligatorio BOOLEAN NOT NULL DEFAULT FALSE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT uq_tipo_documento_nombre UNIQUE (nombre),
    CONSTRAINT chk_tipo_documento_ambito CHECK (
        ambito IN ('VEHICULO', 'CONDUCTOR', 'TRASLADO')
    )
) ENGINE=InnoDB;

CREATE TABLE vehiculo (
    id_vehiculo INT AUTO_INCREMENT PRIMARY KEY,
    placa VARCHAR(20) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    capacidad_carga DECIMAL(12,2) NOT NULL,
    condicion VARCHAR(30) NOT NULL,
    estado VARCHAR(30) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT uq_vehiculo_placa UNIQUE (placa),
    CONSTRAINT chk_vehiculo_capacidad CHECK (capacidad_carga > 0)
) ENGINE=InnoDB;

CREATE TABLE camion (
    id_vehiculo INT PRIMARY KEY,
    numero_ejes INT NOT NULL,

    CONSTRAINT chk_camion_ejes CHECK (numero_ejes > 0),

    CONSTRAINT fk_camion_vehiculo
        FOREIGN KEY (id_vehiculo)
        REFERENCES vehiculo(id_vehiculo)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE conductor (
    id_conductor INT AUTO_INCREMENT PRIMARY KEY,
    dni VARCHAR(20) NOT NULL,
    nombres VARCHAR(120) NOT NULL,
    numero_licencia VARCHAR(30) NOT NULL,
    categoria_licencia VARCHAR(20) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT uq_conductor_dni UNIQUE (dni),
    CONSTRAINT uq_conductor_licencia UNIQUE (numero_licencia)
) ENGINE=InnoDB;

CREATE TABLE documento (
    id_documento INT AUTO_INCREMENT PRIMARY KEY,
    numero VARCHAR(80) NOT NULL,
    fecha_emision DATE NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'VIGENTE',
    observacion VARCHAR(500),
    id_tipo_documento INT NOT NULL,

    CONSTRAINT fk_documento_tipo
        FOREIGN KEY (id_tipo_documento)
        REFERENCES tipo_documento(id_tipo_documento)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT chk_documento_estado CHECK (
        estado IN ('VIGENTE', 'VENCIDO', 'OBSERVADO')
    ),

    CONSTRAINT chk_documento_fechas CHECK (
        fecha_vencimiento >= fecha_emision
    )
) ENGINE=InnoDB;

CREATE TABLE traslado (
    id_traslado INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(40) NOT NULL,
    fecha_programada DATETIME NOT NULL,
    estado VARCHAR(40) NOT NULL DEFAULT 'PROGRAMADO',
    observacion VARCHAR(500),
    fecha_hora_salida DATETIME NULL,
    motivo_rechazo VARCHAR(500),

    id_almacen_origen INT NOT NULL,
    id_almacen_destino INT NOT NULL,
    id_vehiculo INT NOT NULL,
    id_conductor INT NOT NULL,
    id_responsable_salida INT NULL,

    CONSTRAINT uq_traslado_codigo UNIQUE (codigo),

    CONSTRAINT fk_traslado_origen
        FOREIGN KEY (id_almacen_origen)
        REFERENCES almacen(id_almacen)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_traslado_destino
        FOREIGN KEY (id_almacen_destino)
        REFERENCES almacen(id_almacen)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_traslado_vehiculo
        FOREIGN KEY (id_vehiculo)
        REFERENCES vehiculo(id_vehiculo)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_traslado_conductor
        FOREIGN KEY (id_conductor)
        REFERENCES conductor(id_conductor)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_traslado_responsable
        FOREIGN KEY (id_responsable_salida)
        REFERENCES usuario(id_usuario)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT chk_traslado_estado CHECK (
        estado IN (
            'PROGRAMADO',
            'EN_TRANSITO',
            'RECHAZADO',
            'RECIBIDO',
            'RECIBIDO_CON_OBSERVACIONES'
        )
    )

) ENGINE=InnoDB;

CREATE TABLE detalle_traslado (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_traslado INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad DECIMAL(12,3) NOT NULL,

    CONSTRAINT fk_detalle_traslado
        FOREIGN KEY (id_traslado)
        REFERENCES traslado(id_traslado)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_detalle_producto
        FOREIGN KEY (id_producto)
        REFERENCES producto(id_producto)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT chk_detalle_cantidad CHECK (cantidad > 0),

    CONSTRAINT uq_detalle_producto
        UNIQUE (id_traslado, id_producto)
) ENGINE=InnoDB;

CREATE TABLE vehiculo_documento (
    id_vehiculo INT NOT NULL,
    id_documento INT NOT NULL,

    PRIMARY KEY (id_vehiculo, id_documento),

    CONSTRAINT fk_vehiculo_documento_vehiculo
        FOREIGN KEY (id_vehiculo)
        REFERENCES vehiculo(id_vehiculo)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_vehiculo_documento_documento
        FOREIGN KEY (id_documento)
        REFERENCES documento(id_documento)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE conductor_documento (
    id_conductor INT NOT NULL,
    id_documento INT NOT NULL,

    PRIMARY KEY (id_conductor, id_documento),

    CONSTRAINT fk_conductor_documento_conductor
        FOREIGN KEY (id_conductor)
        REFERENCES conductor(id_conductor)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_conductor_documento_documento
        FOREIGN KEY (id_documento)
        REFERENCES documento(id_documento)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE traslado_documento (
    id_traslado INT NOT NULL,
    id_documento INT NOT NULL,

    PRIMARY KEY (id_traslado, id_documento),

    CONSTRAINT fk_traslado_documento_traslado
        FOREIGN KEY (id_traslado)
        REFERENCES traslado(id_traslado)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_traslado_documento_documento
        FOREIGN KEY (id_documento)
        REFERENCES documento(id_documento)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE inspeccion (
    id_inspeccion INT AUTO_INCREMENT PRIMARY KEY,
    fecha_hora DATETIME NOT NULL,
    resultado VARCHAR(20) NOT NULL,
    carga_conforme BOOLEAN NOT NULL,
    observacion VARCHAR(500),

    id_traslado INT NOT NULL,
    id_vigilante INT NOT NULL,

    CONSTRAINT fk_inspeccion_traslado
        FOREIGN KEY (id_traslado)
        REFERENCES traslado(id_traslado)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_inspeccion_vigilante
        FOREIGN KEY (id_vigilante)
        REFERENCES usuario(id_usuario)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT chk_inspeccion_resultado CHECK (
        resultado IN ('CONFORME', 'NO_CONFORME')
    ),

    CONSTRAINT uq_inspeccion_traslado
        UNIQUE (id_traslado)
) ENGINE=InnoDB;

CREATE TABLE evidencia (
    id_evidencia INT AUTO_INCREMENT PRIMARY KEY,
    ruta_archivo VARCHAR(500) NOT NULL,
    descripcion VARCHAR(500),
    fecha_hora DATETIME NOT NULL,
    id_inspeccion INT NOT NULL,

    CONSTRAINT fk_evidencia_inspeccion
        FOREIGN KEY (id_inspeccion)
        REFERENCES inspeccion(id_inspeccion)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE precinto (
    id_precinto INT AUTO_INCREMENT PRIMARY KEY,
    numero VARCHAR(80) NOT NULL,
    fecha_registro DATETIME NOT NULL,
    estado VARCHAR(30) NOT NULL,
    id_traslado INT NOT NULL,

    CONSTRAINT uq_precinto_numero UNIQUE (numero),

    CONSTRAINT fk_precinto_traslado
        FOREIGN KEY (id_traslado)
        REFERENCES traslado(id_traslado)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT uq_precinto_traslado
        UNIQUE (id_traslado)
) ENGINE=InnoDB;

CREATE TABLE recepcion (
    id_recepcion INT AUTO_INCREMENT PRIMARY KEY,
    fecha_hora_llegada DATETIME NOT NULL,
    precinto_conforme BOOLEAN NOT NULL,
    carga_conforme BOOLEAN NOT NULL,
    observacion VARCHAR(500),

    id_traslado INT NOT NULL,
    id_despachador INT NOT NULL,

    CONSTRAINT fk_recepcion_traslado
        FOREIGN KEY (id_traslado)
        REFERENCES traslado(id_traslado)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_recepcion_despachador
        FOREIGN KEY (id_despachador)
        REFERENCES usuario(id_usuario)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT uq_recepcion_traslado
        UNIQUE (id_traslado)
) ENGINE=InnoDB;

CREATE INDEX idx_traslado_fecha
    ON traslado(fecha_programada);

CREATE INDEX idx_traslado_estado
    ON traslado(estado);

CREATE INDEX idx_traslado_vehiculo
    ON traslado(id_vehiculo);

CREATE INDEX idx_traslado_conductor
    ON traslado(id_conductor);

CREATE INDEX idx_traslado_origen
    ON traslado(id_almacen_origen);

CREATE INDEX idx_traslado_destino
    ON traslado(id_almacen_destino);

CREATE INDEX idx_inspeccion_vigilante
    ON inspeccion(id_vigilante);

CREATE INDEX idx_documento_vencimiento
    ON documento(fecha_vencimiento);
