package esprit.tn.dao;

import esprit.tn.model.Test;
import esprit.tn.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TestDAO {
    private Connection connection;

    public TestDAO() {
        connection = DatabaseConnection.getInstance().getConnection();
    }

    public void insert(Test test) {
        String sql = "INSERT INTO test (name, description) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, test.getName());
            pstmt.setString(2, test.getDescription());
            pstmt.executeUpdate();
            System.out.println("Test inserted successfully");
        } catch (SQLException e) {
            System.out.println("Error inserting test: " + e.getMessage());
        }
    }

    public void update(Test test) {
        String sql = "UPDATE test SET name = ?, description = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, test.getName());
            pstmt.setString(2, test.getDescription());
            pstmt.setInt(3, test.getId());
            pstmt.executeUpdate();
            System.out.println("Test updated successfully");
        } catch (SQLException e) {
            System.out.println("Error updating test: " + e.getMessage());
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM test WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Test deleted successfully");
        } catch (SQLException e) {
            System.out.println("Error deleting test: " + e.getMessage());
        }
    }

    public Test findById(int id) {
        String sql = "SELECT * FROM test WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Test(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error finding test: " + e.getMessage());
        }
        return null;
    }

    public List<Test> findAll() {
        List<Test> tests = new ArrayList<>();
        String sql = "SELECT * FROM test";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tests.add(new Test(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error finding all tests: " + e.getMessage());
        }
        return tests;
    }
} 