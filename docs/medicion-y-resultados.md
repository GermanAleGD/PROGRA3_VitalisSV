# Medición y resultados

## Cómo se midió
1. *Pruebas unitarias* de la lógica de negocio (JUnit 5 y Mockito), con ./mvnw test -Dtest=CitaServiceTest.
2. *Pruebas de integración* de la consulta de solapamiento contra MySQL real, con ./mvnw test -Dtest=CitaRepositoryIntegrationTest.
3. *Pruebas manuales de la API* con el script scripts/probar-api.sh, comparando el código HTTP obtenido con el esperado.
4. *Integración continua* con GitHub Actions, que ejecuta todas las pruebas en cada push.

## Pruebas unitarias
Resultado: 8 pruebas ejecutadas, sin fallos.

| Escenario | Resultado esperado |
|---|---|
| Cita válida | Se guarda como PENDIENTE |
| Fin anterior al inicio | Rechazada |
| Cita en el pasado | Rechazada |
| Paciente inexistente | Rechazada |
| Fuera del horario del médico | Rechazada |
| Choque con otra cita | Rechazada |
| Cancelar cita pendiente | Pasa a CANCELADA |
| Cancelar cita completada | Rechazada |

## Pruebas de integración contra MySQL
Resultado: 7 pruebas ejecutadas, sin fallos. Verifican que la consulta de solapamiento funciona en la base de datos real.

| Escenario | Resultado esperado |
|---|---|
| Misma franja exacta | Choque detectado |
| Solapamiento parcial (10:15-10:45) | Choque detectado |
| Cita contenida dentro de otra (10:10-10:20) | Choque detectado |
| Cita contigua posterior (10:30-11:00) | Sin choque |
| Cita contigua anterior (09:30-10:00) | Sin choque |
| Mismo horario con otro médico | Sin choque |
| Cita existente cancelada | Sin choque |

## Pruebas manuales de la API
Se ejecutó el script scripts/probar-api.sh contra la aplicación en ejecución con MySQL 8.0 local. Las citas de prueba se agendaron para el 24 de noviembre de 2026 con el médico de Medicina General (atiende de lunes a viernes de 8:00 a 12:00).

Fecha de la prueba: 20-11-2026

| Escenario | Esperado | Obtenido | Resultado |
|---|---|---|---|
| Agendar cita válida (10:00-10:30) | 201 | 201 | Aprobado |
| Repetir la misma cita | 409 | 409 | Aprobado |
| Cita solapada (10:15-10:45) | 409 | 409 | Aprobado |
| Cita contigua (10:30-11:00) | 201 | 201 | Aprobado |
| Fuera de horario (13:00-13:30) | 409 | 409 | Aprobado |
| Datos incompletos (sin medicoId) | 400 | 400 | Aprobado |
| Cancelar la primera cita | 200 | 200 | Aprobado |
| Volver a agendar el horario liberado | 201 | 201 | Aprobado |

## Integración continua
En cada push a main, GitHub Actions levanta un servidor MySQL 8.0, compila el proyecto y ejecuta todas las pruebas (unitarias y de integración). Última ejecución verificada: exitosa.

## Conclusiones
- Se cumplieron los objetivos específicos 2 y 3: la API permite agendar, cancelar y listar citas, y la regla rechaza las que quedan fuera del horario del médico o se solapan con otra.
- Las pruebas unitarias (8), las de integración contra MySQL (7) y las manuales de la API (8) dieron el resultado esperado, y la integración continua las ejecuta en cada push.
- La integración continua comprueba que el proyecto compila y pasa las pruebas en una máquina limpia.
- Limitaciones: la API no tiene autenticación, la concurrencia entre dos citas simultáneas no está cubierta y las pruebas manuales usaron un solo médico.
- Mejoras futuras: login con roles, gestión de horarios desde la API, reprogramación de citas y bloqueo a nivel de base de datos para la concurrencia.
