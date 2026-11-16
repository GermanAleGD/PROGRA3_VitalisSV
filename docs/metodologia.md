# Metodología

## Enfoque de desarrollo
Desarrollo iterativo e incremental: primero el modelo de datos, luego las entidades, la lógica de negocio con sus pruebas y finalmente la API. Cada avance se guardó en GitHub con commits pequeños.

## Arquitectura
Aplicación por capas:
- *Controller:* recibe las peticiones HTTP y devuelve respuestas.
- *Service:* contiene las reglas de negocio (CitaService).
- *Repository:* acceso a la base de datos con Spring Data JPA.
- *Model:* entidades que representan las tablas.
- *DTO:* objetos que definen qué datos entran y salen por la API, sin exponer las entidades.

## Tecnologías y por qué
| Tecnología | Motivo |
|---|---|
| Java 21 + Spring Boot | Estándar en el desarrollo de sistemas empresariales |
| MySQL 8 | Base relacional ya disponible en el entorno de desarrollo |
| Flyway | Versiona el esquema de la base de datos y lo recrea en cualquier máquina |
| Maven Wrapper | Permite compilar sin instalar Maven |
| JUnit 5 + Mockito | Pruebas automáticas de las reglas de negocio |

## Modelo de datos
Cinco tablas: especialidades, medicos, pacientes, horarios_disponibles y citas. Un médico tiene una especialidad y varios bloques de horario semanales; una cita une a un paciente con un médico en un intervalo de tiempo y tiene un estado (PENDIENTE, CONFIRMADA, CANCELADA o COMPLETADA).

## Regla central: agendar una cita
CitaService.agendar aplica estas validaciones, en orden:
1. La hora de fin es posterior a la de inicio y ambas son el mismo día.
2. La cita no está en el pasado.
3. El paciente y el médico existen.
4. La cita cae dentro de un bloque de horario del médico.
5. No se solapa con otra cita del médico que no esté cancelada.

Dos citas se solapan si inicio_existente < fin_nueva y fin_existente > inicio_nueva. Por eso una cita de 10:00 a 10:30 y otra de 10:30 a 11:00 no chocan.

## Decisiones de diseño
- Las migraciones de Flyway son la única fuente del esquema; Hibernate solo lo valida (ddl-auto=validate).
- Los datos de prueba son ficticios y viven en una migración separada.

## Limitaciones conocidas
- *Concurrencia:* la validación de choques se hace antes de guardar. Si dos peticiones llegan al mismo tiempo para el mismo médico, ambas podrían pasar. Mejora futura: bloqueo a nivel de base de datos.
- *Seguridad:* la API está abierta temporalmente. Se reemplazará por autenticación con roles antes de usar datos reales.
