-- Création de la base de données
CREATE DATABASE IF NOT EXISTS educonnect;
USE educonnect;

-- Suppression des tables si elles existent
DROP TABLE IF EXISTS test;
DROP TABLE IF EXISTS type;

-- Création de la table type
CREATE TABLE type (
    id INT PRIMARY KEY AUTO_INCREMENT,
    mode VARCHAR(100) NOT NULL
);

-- Création de la table test
CREATE TABLE test (
    id INT PRIMARY KEY AUTO_INCREMENT,
    heureDuTest VARCHAR(50) NOT NULL,
    nomMatiere VARCHAR(100) NOT NULL,
    type_id INT,
    FOREIGN KEY (type_id) REFERENCES type(id)
); 