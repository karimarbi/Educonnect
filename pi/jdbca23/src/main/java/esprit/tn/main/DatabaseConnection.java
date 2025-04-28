package esprit.tn.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe singleton pour gérer la connexion à la base de données
 */
public class DatabaseConnection {

    private Connection cnx;
    private static DatabaseConnection instance;

    /**
     * Constructeur privé pour empêcher l'instanciation directe
     */
    private DatabaseConnection() {
        String url = "jdbc:mysql://localhost/esprit";
        String username = "root";
        String password = "";

        try {
            // Charger le pilote JDBC MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Établir la connexion
            cnx = DriverManager.getConnection(url, username, password);
            System.out.println("Connexion établie avec succès");
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données: " + e.getMessage());
            throw new RuntimeException("Impossible de se connecter à la base de données", e);
        } catch (ClassNotFoundException e) {
            System.err.println("Pilote JDBC MySQL non trouvé: " + e.getMessage());
            throw new RuntimeException("Pilote JDBC MySQL non trouvé", e);
        }
    }

    /**
     * Méthode pour obtenir l'instance unique de DatabaseConnection
     * @return l'instance de DatabaseConnection
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Méthode pour obtenir la connexion à la base de données
     * @return la connexion à la base de données
     */
    public Connection getCnx() {
        return cnx;
    }
}
