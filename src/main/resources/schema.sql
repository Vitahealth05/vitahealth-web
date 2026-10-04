-- Esquema de VitaHealth (basado en la evidencia GA6-220501096-AA2-EV03).
-- Compatible con PostgreSQL y con H2 en modo PostgreSQL. Lo ejecuta InicializadorBD al arrancar.
CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario INTEGER GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR(80) NOT NULL,
    apellido VARCHAR(80) NOT NULL,
    correo VARCHAR(120) NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_usuarios PRIMARY KEY (id_usuario),
    CONSTRAINT uq_usuarios_correo UNIQUE (correo)
);
CREATE TABLE IF NOT EXISTS perfiles (
    id_perfil INTEGER GENERATED ALWAYS AS IDENTITY,
    id_usuario INTEGER NOT NULL,
    fecha_nacimiento DATE,
    peso_kg DECIMAL(5,2),
    altura_cm DECIMAL(5,2),
    objetivo VARCHAR(120),
    CONSTRAINT pk_perfiles PRIMARY KEY (id_perfil),
    CONSTRAINT uq_perfiles_usuario UNIQUE (id_usuario),
    CONSTRAINT fk_perfiles_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    CONSTRAINT chk_perfiles_peso CHECK (peso_kg IS NULL OR peso_kg > 0),
    CONSTRAINT chk_perfiles_altura CHECK (altura_cm IS NULL OR altura_cm > 0)
);
CREATE TABLE IF NOT EXISTS actividades (
    id_actividad INTEGER GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    tipo VARCHAR(50) NOT NULL,
    calorias_hora DECIMAL(7,2) NOT NULL,
    CONSTRAINT pk_actividades PRIMARY KEY (id_actividad),
    CONSTRAINT uq_actividades_nombre UNIQUE (nombre),
    CONSTRAINT chk_actividades_calorias CHECK (calorias_hora >= 0)
);
CREATE TABLE IF NOT EXISTS registro_actividad (
    id_registro INTEGER GENERATED ALWAYS AS IDENTITY,
    id_usuario INTEGER NOT NULL,
    id_actividad INTEGER NOT NULL,
    fecha DATE NOT NULL,
    duracion_minutos INTEGER NOT NULL,
    calorias_quemadas DECIMAL(8,2) NOT NULL,
    CONSTRAINT pk_registro_actividad PRIMARY KEY (id_registro),
    CONSTRAINT fk_registro_actividad_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_registro_actividad_actividad FOREIGN KEY (id_actividad) REFERENCES actividades(id_actividad) ON DELETE RESTRICT,
    CONSTRAINT chk_registro_actividad_duracion CHECK (duracion_minutos > 0),
    CONSTRAINT chk_registro_actividad_calorias CHECK (calorias_quemadas >= 0)
);
CREATE TABLE IF NOT EXISTS hidratacion (
    id_hidratacion INTEGER GENERATED ALWAYS AS IDENTITY,
    id_usuario INTEGER NOT NULL,
    fecha DATE NOT NULL,
    cantidad_ml INTEGER NOT NULL,
    CONSTRAINT pk_hidratacion PRIMARY KEY (id_hidratacion),
    CONSTRAINT fk_hidratacion_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    CONSTRAINT chk_hidratacion_cantidad CHECK (cantidad_ml > 0)
);
CREATE INDEX IF NOT EXISTS idx_perfiles_usuario ON perfiles(id_usuario);
CREATE INDEX IF NOT EXISTS idx_registro_actividad_usuario ON registro_actividad(id_usuario);
CREATE INDEX IF NOT EXISTS idx_registro_actividad_actividad ON registro_actividad(id_actividad);
CREATE INDEX IF NOT EXISTS idx_hidratacion_usuario ON hidratacion(id_usuario);
