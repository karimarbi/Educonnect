-- Create table for types
CREATE TABLE IF NOT EXISTS type (
    id INT PRIMARY KEY AUTO_INCREMENT,
    mode VARCHAR(50) NOT NULL
);

-- Create table for tests
CREATE TABLE IF NOT EXISTS test (
    id INT PRIMARY KEY AUTO_INCREMENT,
    heureDuTest INT NOT NULL CHECK (heureDuTest BETWEEN 8 AND 17),
    nomMatiere VARCHAR(255) NOT NULL,
    type_id INT NOT NULL,
    FOREIGN KEY (type_id) REFERENCES type(id)
);

-- Insert default types
INSERT INTO type (mode) VALUES 
('Présentiel'),
('En ligne')
ON DUPLICATE KEY UPDATE mode=mode;
