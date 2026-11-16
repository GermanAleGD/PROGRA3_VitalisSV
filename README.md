# VitalisSV

Sistema de agenda de citas médicas para una clínica u hospital. API REST en Java que permite agendar y cancelar citas, evitando choques de horario y respetando la disponibilidad de cada médico.

> Proyecto académico con datos ficticios. No usar con datos reales de pacientes sin antes implementar autenticación y cumplir la normativa de datos de salud.

## Funcionalidades actuales

- Consulta de médicos y pacientes
- Agendar citas validando: fechas coherentes, que no sean en el pasado, que el médico atienda en ese horario y que no choquen con otra cita
- Cancelar citas (el horario queda libre)
- Listado de citas

## Tecnologías

Java 21, Spring Boot, Spring Data JPA (Hibernate), MySQL 8, Flyway, Maven (wrapper incluido), JUnit 5 y Mockito.

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

## Probar la API

bash
curl http://localhost:8080/api/medicos
curl http://localhost:8080/api/pacientes

curl -X POST http://localhost:8080/api/citas \
  -H "Content-Type: application/json" \
  -d '{"pacienteId":1,"medicoId":1,"inicio":"2026-11-23T10:00:00","fin":"2026-11-23T10:30:00","motivo":"Control general"}'

curl -X PATCH http://localhost:8080/api/citas/1/cancelar
curl http://localhost:8080/api/citas


Ajusta la fecha a un día futuro en que el médico atienda (Ana: lunes a viernes de 8:00 a 12:00).

## Pruebas

bash
./mvnw test -Dtest=CitaServiceTest


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
├── exception/    Errores de negocio
└── config/       Configuración
src/main/resources/db/migration/   Scripts de Flyway
db/                                Script de creación de la base
docs/                              Documentación del proyecto


## Limitaciones conocidas

- La API está abierta, sin autenticación (configuración temporal de desarrollo).
- Dos citas agendadas exactamente al mismo tiempo con el mismo médico podrían pasar la validación a la vez.
