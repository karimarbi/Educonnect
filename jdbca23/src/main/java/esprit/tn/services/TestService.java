package esprit.tn.services;

import esprit.tn.entities.Test;
import esprit.tn.utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TestService implements ITestService {
    private Connection connection;

    public TestService() {
        connection = MyConnection.getInstance().getConnection();
    }

    @Override
    public void add(Test test) {
        String sql = "INSERT INTO test (heureDuTest, nomMatiere) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, test.getHeureDuTest());
            preparedStatement.setString(2, test.getNomMatiere());
            preparedStatement.executeUpdate();
            
            // Récupérer l'ID généré
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                test.setId(generatedKeys.getInt(1));
            }
            
            System.out.println("Test ajouté avec succès");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du test: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void update(Test test) {
        String sql = "UPDATE test SET heureDuTest = ?, nomMatiere = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, test.getHeureDuTest());
            preparedStatement.setString(2, test.getNomMatiere());
            preparedStatement.setInt(3, test.getId());
            preparedStatement.executeUpdate();
            System.out.println("Test mis à jour avec succès");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du test: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM test WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            System.out.println("Test supprimé avec succès");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du test: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Test getById(int id) {
        String sql = "SELECT * FROM test WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Test test = new Test();
                test.setId(resultSet.getInt("id"));
                test.setHeureDuTest(resultSet.getString("heureDuTest"));
                test.setNomMatiere(resultSet.getString("nomMatiere"));
                return test;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du test: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Test> getAll() {
        List<Test> tests = new ArrayList<>();
        String sql = "SELECT * FROM test";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Test test = new Test();
                test.setId(resultSet.getInt("id"));
                test.setHeureDuTest(resultSet.getString("heureDuTest"));
                test.setNomMatiere(resultSet.getString("nomMatiere"));
                tests.add(test);
            }
            System.out.println("Nombre de tests récupérés: " + tests.size());
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des tests: " + e.getMessage());
            e.printStackTrace();
        }
        return tests;
    }

    public Test getLastAdded() {
        String sql = "SELECT * FROM test ORDER BY id DESC LIMIT 1";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                Test test = new Test();
                test.setId(resultSet.getInt("id"));
                test.setHeureDuTest(resultSet.getString("heureDuTest"));
                test.setNomMatiere(resultSet.getString("nomMatiere"));
                return test;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du dernier test: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
} 