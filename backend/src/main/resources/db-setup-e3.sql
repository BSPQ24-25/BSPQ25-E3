CREATE DATABASE IF NOT EXISTS mv;

CREATE USER IF NOT EXISTS 'e3'@'%' IDENTIFIED BY 'e3';

GRANT ALL PRIVILEGES ON mv.* TO 'e3'@'%' WITH GRANT OPTION;

FLUSH PRIVILEGES;
