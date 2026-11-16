CREATE DATABASE IF NOT EXISTS agenda_citas CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'agenda'@'localhost' IDENTIFIED BY 'agenda_dev';
GRANT ALL PRIVILEGES ON agenda_citas.* TO 'agenda'@'localhost';
FLUSH PRIVILEGES;
