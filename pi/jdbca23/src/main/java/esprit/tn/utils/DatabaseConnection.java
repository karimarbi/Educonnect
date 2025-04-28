package esprit.tn.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private static final String DB_NAME = "jdbca23";

    private DatabaseConnection() {
        try {
            // D'abord, se connecter sans spécifier la base de données
            String baseUrl = "jdbc:mysql://localhost:3306/";
            String username = "root";
            String password = "";
            
            connection = DriverManager.getConnection(baseUrl, username, password);
            
            // Supprimer la base de données si elle existe et la recréer
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("DROP DATABASE IF EXISTS " + DB_NAME);
                stmt.executeUpdate("CREATE DATABASE " + DB_NAME);
                System.out.println("Base de données créée");
            }
            
            // Fermer la connexion initiale
            connection.close();
            
            // Se reconnecter avec la base de données spécifiée
            connection = DriverManager.getConnection(baseUrl + DB_NAME, username, password);
            
            // Exécuter le script SQL
            initializeDatabase();
            
            System.out.println("Connexion à la base de données établie avec succès");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion à la base de données : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeDatabase() {
        String sqlFile = getClass().getClassLoader().getResource("database.sql").getFile();
        try (BufferedReader reader = new BufferedReader(new FileReader(sqlFile));
             Statement stmt = connection.createStatement()) {
            
            StringBuilder script = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // Ignorer les commentaires et les lignes vides
                if (!line.trim().startsWith("--") && !line.trim().isEmpty()) {
                    script.append(line);
                    
                    // Si la ligne se termine par un point-virgule, exécuter la requête
                    if (line.trim().endsWith(";")) {
                        String sql = script.toString().trim();
                        // Ignorer les commandes de création/utilisation de la base de données
                        if (!sql.toLowerCase().contains("create database") && 
                            !sql.toLowerCase().contains("use ")) {
                            stmt.executeUpdate(sql);
                        }
                        script.setLength(0);
                    }
                }
            }
            System.out.println("Script SQL exécuté avec succès");
        } catch (IOException | SQLException e) {
            System.err.println("Erreur lors de l'initialisation de la base de données : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            // Vérifier si la connexion est fermée ou null
            if (connection == null || connection.isClosed()) {
                // Se reconnecter
                String url = "jdbc:mysql://localhost:3306/" + DB_NAME;
                String username = "root";
                String password = "";
                connection = DriverManager.getConnection(url, username, password);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la reconnexion : " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }
}