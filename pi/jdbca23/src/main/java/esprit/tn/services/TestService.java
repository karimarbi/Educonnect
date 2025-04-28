package esprit.tn.services;

import esprit.tn.models.Test;
import esprit.tn.models.Type;
import esprit.tn.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TestService implements ITestService {
    private Connection connection;
    private TypeService typeService;

    public TestService() {
        connection = DatabaseConnection.getInstance().getConnection();
        typeService = new TypeService();
    }

    @Override
    public void add(Test test) throws SQLException {
        String query = "INSERT INTO test (heureDuTest, nomMatiere, type_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, test.getHeureDuTest());
            ps.setString(2, test.getNomMatiere());
            ps.setInt(3, test.getType().getId());
            ps.executeUpdate();
            
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                test.setId(rs.getInt(1));
            }
        }
    }

    @Override
    public void update(Test test) throws SQLException {
        String query = "UPDATE test SET heureDuTest = ?, nomMatiere = ?, type_id = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, test.getHeureDuTest());
            ps.setString(2, test.getNomMatiere());
            ps.setInt(3, test.getType().getId());
            ps.setInt(4, test.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM test WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Test getById(int id) throws SQLException {
        String query = "SELECT * FROM test WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Type type = typeService.getById(rs.getInt("type_id"));
                return new Test(
                    rs.getInt("id"),
                    rs.getInt("heureDuTest"),
                    rs.getString("nomMatiere"),
                    type
                );
            }
        }
        return null;
    }

    @Override
    public List<Test> getAll() throws SQLException {
        List<Test> tests = new ArrayList<>();
        String query = "SELECT * FROM test";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                Type type = typeService.getById(rs.getInt("type_id"));
                tests.add(new Test(
                    rs.getInt("id"),
                    rs.getInt("heureDuTest"),
                    rs.getString("nomMatiere"),
                    type
                ));
            }
        }
        return tests;
    }
}