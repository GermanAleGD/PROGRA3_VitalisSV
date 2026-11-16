# Medición y resultados

## Cómo se midió
1. *Pruebas unitarias* de la lógica de negocio (JUnit 5 y Mockito), ejecutadas con ./mvnw test -Dtest=CitaServiceTest.
2. *Pruebas manuales de la API* con curl, comparando el código HTTP obtenido con el esperado en cada escenario.

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

## Pruebas manuales de la API
Fecha de la prueba: 15/11/2026

| Escenario | Esperado | Obtenido |
|---|---|---|
| Agendar cita válida | 201 | 201 |
| Repetir la misma cita | 409 | 409 |
| Cita solapada (10:15-10:45) | 409 | SIN DATOS |
| Cita contigua (10:30-11:00) | 201 | SIN DATOS |
| Fuera de horario (13:00-13:30) | 409 | 409 |
| Datos incompletos (sin medicoId) | 400 | SIN DATOS |
| Cancelar y volver a agendar el horario | 201 | 201 |

## Conclusiones
Al momendo de compilar el programa si bien es tardado (Al menos en mi maquina) deberia de poder correr, aun no hemos hecho el apartado visual y amigable al consumidor pero ya esta en proceso

## No cubierto aún
La consulta de solapamiento no se ha probado contra MySQL real con pruebas automáticas (las pruebas unitarias simulan los repositorios).
