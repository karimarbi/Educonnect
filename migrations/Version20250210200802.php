<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20250210200802 extends AbstractMigration
{
    public function getDescription(): string
    {
        return '';
    }

    public function up(Schema $schema): void
    {
        // Crée la table utilisateur avec la colonne salaire
        $this->addSql('CREATE TABLE utilisateur (
            id INT AUTO_INCREMENT NOT NULL, 
            nom VARCHAR(255) NOT NULL, 
            prenom VARCHAR(255) NOT NULL, 
            cin VARCHAR(255) NOT NULL, 
            age INT NOT NULL, 
            date_nai DATE NOT NULL, 
            adresse VARCHAR(255) NOT NULL, 
            lieu VARCHAR(255) NOT NULL, 
            email VARCHAR(255) NOT NULL, 
            mdp VARCHAR(255) NOT NULL, 
            role VARCHAR(255) NOT NULL, 
            specialite VARCHAR(255) DEFAULT NULL, 
            salaire DOUBLE PRECISION DEFAULT NULL,  -- Assure-toi que cette ligne est présente
            created_at DATETIME NOT NULL COMMENT \'(DC2Type:datetime_immutable)\', 
            update_at DATETIME DEFAULT NULL COMMENT \'(DC2Type:datetime_immutable)\', 
            PRIMARY KEY(id)
        ) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB');
    }

    public function down(Schema $schema): void
    {
        // Supprimer la table utilisateur en cas de rollback
        $this->addSql('DROP TABLE utilisateur');
    }
}
