-- Create table for types
CREATE TABLE IF NOT EXISTS type (
    id INT PRIMARY KEY AUTO_INCREMENT,
    mode VARCHAR(50) NOT NULL CHECK (mode IN ('Présentiel', 'En ligne')),
    description TEXT
);

-- Création de la table test
CREATE TABLE IF NOT EXISTS test (
    id INT PRIMARY KEY AUTO_INCREMENT,
    heureDuTest INT NOT NULL CHECK (heureDuTest BETWEEN 8 AND 17),
    nomMatiere VARCHAR(255) NOT NULL,
    nomFormateur VARCHAR(255) NOT NULL,
    nomSalle VARCHAR(100) NOT NULL,
    coefficient DOUBLE NOT NULL CHECK (coefficient > 0)
);

-- Insert default types
INSERT INTO type (mode, description) VALUES 
('Présentiel', 'Test effectué en salle de classe'),
('En ligne', 'Test effectué à distance via plateforme en ligne')
ON DUPLICATE KEY UPDATE mode=mode;
