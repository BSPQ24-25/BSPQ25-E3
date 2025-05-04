-- Script para crear la base de datos y el usuario para la API de biblioteca
CREATE DATABASE IF NOT EXISTS mv;

-- Crear usuario para conexiones remotas
CREATE USER IF NOT EXISTS 'e3'@'%' IDENTIFIED BY 'e3';

-- Conceder privilegios al usuario
GRANT ALL PRIVILEGES ON mv.* TO 'e3'@'%' WITH GRANT OPTION;

-- Aplicar cambios
FLUSH PRIVILEGES;
