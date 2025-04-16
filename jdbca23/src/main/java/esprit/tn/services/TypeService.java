package esprit.tn.services;

import esprit.tn.entities.Type;
import esprit.tn.utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TypeService implements ITypeService {
    private Connection connection;

    public TypeService() {
        connection = MyConnection.getInstance().getConnection();
    }

    @Override
    public void add(Type type) {
        String sql = "INSERT INTO type (mode) VALUES (?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, type.getMode());
            preparedStatement.executeUpdate();
            System.out.println("Type ajouté avec succès");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du type: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void update(Type type) {
        String sql = "UPDATE type SET mode = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, type.getMode());
            preparedStatement.setInt(2, type.getId());
            preparedStatement.executeUpdate();
            System.out.println("Type mis à jour avec succès");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du type: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM type WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            System.out.println("Type supprimé avec succès");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du type: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Type getById(int id) {
        String sql = "SELECT * FROM type WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Type type = new Type();
                type.setId(resultSet.getInt("id"));
                type.setMode(resultSet.getString("mode"));
                return type;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du type: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Type> getAll() {
        List<Type> types = new ArrayList<>();
        String sql = "SELECT * FROM type";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Type type = new Type();
                type.setId(resultSet.getInt("id"));
                type.setMode(resultSet.getString("mode"));
                types.add(type);
            }
            System.out.println("Nombre de types récupérés: " + types.size());
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des types: " + e.getMessage());
            e.printStackTrace();
        }
        return types;
    }
} 