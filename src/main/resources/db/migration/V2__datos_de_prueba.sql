INSERT INTO especialidades (nombre) VALUES
    ('Medicina General'), ('Pediatria'), ('Cardiologia');

INSERT INTO medicos (nombre, apellido, correo, especialidad_id) VALUES
    ('Ana', 'Ramirez', 'ana.ramirez@vitalis.test',
        (SELECT id FROM especialidades WHERE nombre = 'Medicina General')),
    ('Carlos', 'Lopez', 'carlos.lopez@vitalis.test',
        (SELECT id FROM especialidades WHERE nombre = 'Pediatria')),
    ('Sofia', 'Cruz', 'sofia.cruz@vitalis.test',
        (SELECT id FROM especialidades WHERE nombre = 'Cardiologia'));

INSERT INTO pacientes (nombre, apellido, documento, correo, telefono, fecha_nacimiento) VALUES
    ('Luis', 'Mendez', '10000001', 'luis.mendez@correo.test', '7000-0001', '1990-05-14'),
    ('Marta', 'Flores', '10000002', 'marta.flores@correo.test', '7000-0002', '1985-11-02'),
    ('Pedro', 'Garcia', '10000003', 'pedro.garcia@correo.test', '7000-0003', '2001-03-27');

-- Ana: lunes a viernes 08:00-12:00
INSERT INTO horarios_disponibles (medico_id, dia_semana, hora_inicio, hora_fin)
SELECT m.id, d.dia, '08:00:00', '12:00:00'
FROM medicos m
CROSS JOIN (SELECT 1 AS dia UNION ALL SELECT 2 UNION ALL SELECT 3
            UNION ALL SELECT 4 UNION ALL SELECT 5) d
WHERE m.correo = 'ana.ramirez@vitalis.test';

-- Carlos: lunes a viernes 14:00-18:00
INSERT INTO horarios_disponibles (medico_id, dia_semana, hora_inicio, hora_fin)
SELECT m.id, d.dia, '14:00:00', '18:00:00'
FROM medicos m
CROSS JOIN (SELECT 1 AS dia UNION ALL SELECT 2 UNION ALL SELECT 3
            UNION ALL SELECT 4 UNION ALL SELECT 5) d
WHERE m.correo = 'carlos.lopez@vitalis.test';

-- Sofia: lunes, miercoles y viernes 09:00-13:00
INSERT INTO horarios_disponibles (medico_id, dia_semana, hora_inicio, hora_fin)
SELECT m.id, d.dia, '09:00:00', '13:00:00'
FROM medicos m
CROSS JOIN (SELECT 1 AS dia UNION ALL SELECT 3 UNION ALL SELECT 5) d
WHERE m.correo = 'sofia.cruz@vitalis.test';
