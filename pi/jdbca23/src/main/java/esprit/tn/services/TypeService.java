package esprit.tn.services;

import esprit.tn.models.Type;
import esprit.tn.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TypeService implements ITypeService {
    private Connection connection;

    public TypeService() {
        connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void add(Type type) throws SQLException {
        String query = "INSERT INTO type (mode) VALUES (?)";
        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, type.getMode());
            ps.executeUpdate();
            
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                type.setId(rs.getInt(1));
            }
        }
    }

    @Override
    public void update(Type type) throws SQLException {
        String query = "UPDATE type SET mode = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, type.getMode());
            ps.setInt(2, type.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM type WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Type getById(int id) throws SQLException {
        String query = "SELECT * FROM type WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Type(rs.getInt("id"), rs.getString("mode"));
            }
        }
        return null;
    }

    @Override
    public List<Type> getAll() throws SQLException {
        List<Type> types = new ArrayList<>();
        String query = "SELECT * FROM type";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                types.add(new Type(rs.getInt("id"), rs.getString("mode")));
            }
        }
        return types;
    }
}
