# VitalisSV

[![CI](https://github.com/GermanAleGD/PROGRA3_VitalisSV/actions/workflows/ci.yml/badge.svg)](https://github.com/GermanAleGD/PROGRA3_VitalisSV/actions/workflows/ci.yml)

Sistema de agenda de citas médicas para una clínica u hospital. API REST en Java que permite agendar, reprogramar y cancelar citas, evitando choques de horario y respetando la disponibilidad de cada médico.

> Proyecto académico con datos ficticios. No usar con datos reales de pacientes sin antes implementar autenticación y cumplir la normativa de datos de salud.

## Funcionalidades

- Agendar citas validando: fechas coherentes, que no sean en el pasado, que el médico atienda en ese horario y que no choquen con otra cita
- Reprogramar y cancelar citas (al cancelar, el horario queda libre)
- Agenda de un médico por día e historial de citas de un paciente
- Alta, consulta y actualización de pacientes, médicos y especialidades

## Tecnologías

Java 21, Spring Boot 4.1, Spring Data JPA (Hibernate), MySQL 8, Flyway, Maven (wrapper incluido), JUnit 5, Mockito y GitHub Actions.

## Requisitos

- JDK 21
- MySQL 8.0.16 o superior, en el puerto 3306
- No hace falta instalar Maven: el proyecto incluye mvnw

## Cómo ejecutarlo

bash
git clone https://github.com/GermanAleGD/PROGRA3_VitalisSV.git
cd PROGRA3_VitalisSV

# 1. Crear la base de datos y el usuario (pide la contraseña de root de MySQL)
mysql -u root -p < db/crear_base.sql

# 2. Arrancar la aplicación (Flyway crea las tablas y carga datos de prueba)
./mvnw spring-boot:run


En Windows, ejecuta los comandos desde Git Bash. La API queda en http://localhost:8080.

Si tu MySQL usa otras credenciales, define las variables de entorno DB_URL, DB_USER y DB_PASSWORD.

## Endpoints principales

| Método y ruta | Qué hace |
|---|---|
| POST /api/citas | Agenda una cita |
| PATCH /api/citas/{id}/reprogramar | Mueve una cita a otra franja |
| PATCH /api/citas/{id}/cancelar | Cancela una cita |
| GET /api/citas | Lista las citas |
| GET /api/medicos/{id}/citas?fecha=AAAA-MM-DD | Agenda de un médico (sin canceladas) |
| GET /api/pacientes/{id}/citas | Historial de un paciente |
| GET, POST /api/pacientes y GET, PUT /api/pacientes/{id} | Gestión de pacientes |
| GET, POST /api/medicos y GET, PUT /api/medicos/{id} | Gestión de médicos |
| GET, POST /api/especialidades | Gestión de especialidades |

## Probar la API

Con la aplicación corriendo, el script scripts/probar-api.sh ejecuta 8 escenarios de agendado y muestra el código HTTP esperado y el obtenido:

bash
./scripts/probar-api.sh 2026-12-02


Usa una fecha futura entre lunes y viernes (el médico de ejemplo atiende de 8:00 a 12:00). También puedes probar a mano:

bash
curl http://localhost:8080/api/medicos

curl -X POST http://localhost:8080/api/citas \
  -H "Content-Type: application/json" \
  -d '{"pacienteId":1,"medicoId":1,"inicio":"2026-12-03T10:00:00","fin":"2026-12-03T10:30:00","motivo":"Control general"}'


## Pruebas automáticas

bash
./mvnw verify


Ejecuta las pruebas unitarias y las de integración. Las de integración usan MySQL real, así que debe estar encendido con la base creada.

## Documentación

- [Objetivos](docs/objetivos.md)
- [Metodología](docs/metodologia.md)
- [Medición y resultados](docs/medicion-y-resultados.md)

## Estructura


src/main/java/com/clinica/VitalisSV/
├── controller/   Endpoints REST
├── service/      Reglas de negocio
├── repository/   Acceso a datos
├── model/        Entidades JPA
├── dto/          Datos de entrada y salida de la API
├── exception/    Errores de negocio y respuestas HTTP
└── config/       Configuración
src/main/resources/db/migration/   Scripts de Flyway
db/                                Script de creación de la base
scripts/                           Script de pruebas manuales de la API
docs/                              Documentación del proyecto


## Limitaciones conocidas

- La API está abierta, sin autenticación ni roles (configuración temporal de desarrollo).
- Los horarios de atención solo se cargan desde la migración de datos de prueba. Un médico nuevo no puede recibir citas hasta que tenga horarios.
- No hay baja de pacientes ni de médicos.
- Dos citas agendadas exactamente al mismo tiempo con el mismo médico podrían pasar la validación a la vez.

## Trabajo futuro

Autenticación con roles, gestión de horarios de atención desde la API, baja lógica de pacientes y médicos, y bloqueo a nivel de base de datos para la concurrencia.
