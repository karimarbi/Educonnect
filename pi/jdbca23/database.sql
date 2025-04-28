-- Création de la base de données
CREATE DATABASE IF NOT EXISTS jdbca23;
USE jdbca23;

-- Suppression des tables si elles existent
DROP TABLE IF EXISTS test;
DROP TABLE IF EXISTS type;

-- Création de la table type
CREATE TABLE type (
    id INT PRIMARY KEY AUTO_INCREMENT,
    mode VARCHAR(100) NOT NULL
);

-- Création de la table test avec suppression en cascade
CREATE TABLE test (
    id INT PRIMARY KEY AUTO_INCREMENT,
    heureDuTest INT NOT NULL,
    nomMatiere VARCHAR(100) NOT NULL,
    type_id INT NOT NULL,
    nomFormateur VARCHAR(100) NOT NULL,
    jourTest DATE NOT NULL,
    nomSalle VARCHAR(100) NOT NULL,
    coefficient DOUBLE NOT NULL,
    FOREIGN KEY (type_id) REFERENCES type(id) ON DELETE CASCADE
);

-- Insertion de quelques types de test
INSERT INTO type (mode) VALUES
    ('Examen'),
    ('Contrôle'),
    ('Quiz'),
    ('TP');