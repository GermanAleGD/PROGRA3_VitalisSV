CREATE TABLE especialidades (
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre  VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE medicos (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre           VARCHAR(100) NOT NULL,
    apellido         VARCHAR(100) NOT NULL,
    correo           VARCHAR(150) NOT NULL UNIQUE,
    especialidad_id  BIGINT NOT NULL,
    FOREIGN KEY (especialidad_id) REFERENCES especialidades(id)
) ENGINE=InnoDB;

CREATE TABLE pacientes (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre            VARCHAR(100) NOT NULL,
    apellido          VARCHAR(100) NOT NULL,
    documento         VARCHAR(30)  NOT NULL UNIQUE,
    correo            VARCHAR(150),
    telefono          VARCHAR(30),
    fecha_nacimiento  DATE
) ENGINE=InnoDB;

CREATE TABLE horarios_disponibles (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    medico_id    BIGINT NOT NULL,
    dia_semana   TINYINT NOT NULL,
    hora_inicio  TIME NOT NULL,
    hora_fin     TIME NOT NULL,
    FOREIGN KEY (medico_id) REFERENCES medicos(id),
    CONSTRAINT chk_dia CHECK (dia_semana BETWEEN 1 AND 7),
    CONSTRAINT chk_horas CHECK (hora_fin > hora_inicio)
) ENGINE=InnoDB;

CREATE TABLE citas (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    paciente_id  BIGINT NOT NULL,
    medico_id    BIGINT NOT NULL,
    inicio       DATETIME NOT NULL,
    fin          DATETIME NOT NULL,
    estado       VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    motivo       VARCHAR(255),
    FOREIGN KEY (paciente_id) REFERENCES pacientes(id),
    FOREIGN KEY (medico_id) REFERENCES medicos(id),
    CONSTRAINT chk_fin CHECK (fin > inicio),
    CONSTRAINT chk_estado CHECK (estado IN ('PENDIENTE','CONFIRMADA','CANCELADA','COMPLETADA'))
) ENGINE=InnoDB;

CREATE INDEX idx_citas_medico_inicio ON citas (medico_id, inicio);
CREATE INDEX idx_citas_paciente ON citas (paciente_id);
