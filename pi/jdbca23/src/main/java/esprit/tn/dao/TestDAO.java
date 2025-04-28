package esprit.tn.dao;

import esprit.tn.entities.Test;

import esprit.tn.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TestDAO {
    private Connection connection;

    public TestDAO() {
        connection = DatabaseConnection.getInstance().getConnection();
    }

    public void insert(Test test) throws SQLException {
        String sql = "INSERT INTO test (heureDuTest, nomMatiere, type_id, nomFormateur, jourTest, nomSalle, coefficient) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, test.getHeureDuTest());
            pstmt.setString(2, test.getNomMatiere());
            pstmt.setInt(3, test.getType_id());
            pstmt.setString(4, test.getNomFormateur());
            pstmt.setDate(5, java.sql.Date.valueOf(test.getJourTest()));
            pstmt.setString(6, test.getNomSalle());
            pstmt.setDouble(7, test.getCoefficient());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La création du test a échoué, aucune ligne ajoutée.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    test.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("La création du test a échoué, aucun ID obtenu.");
                }
            }
        }
    }

    public void update(Test test) throws SQLException {
        String sql = "UPDATE test SET heureDuTest=?, nomMatiere=?, type_id=?, nomFormateur=?, jourTest=?, nomSalle=?, coefficient=? WHERE id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, test.getHeureDuTest());
            pstmt.setString(2, test.getNomMatiere());
            pstmt.setInt(3, test.getType_id());
            pstmt.setString(4, test.getNomFormateur());
            pstmt.setDate(5, java.sql.Date.valueOf(test.getJourTest()));
            pstmt.setString(6, test.getNomSalle());
            pstmt.setDouble(7, test.getCoefficient());
            pstmt.setInt(8, test.getId());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La mise à jour du test a échoué, aucune ligne modifiée.");
            }
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM test WHERE id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La suppression du test a échoué, aucune ligne supprimée.");
            }
        }
    }

    public Test get(int id) throws SQLException {
        String sql = "SELECT t.*, ty.mode as type_mode FROM test t LEFT JOIN type ty ON t.type_id = ty.id WHERE t.id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractTestFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public List<Test> getAll() throws SQLException {
        List<Test> tests = new ArrayList<>();
        String sql = "SELECT t.*, ty.mode as type_mode FROM test t LEFT JOIN type ty ON t.type_id = ty.id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tests.add(extractTestFromResultSet(rs));
            }
        }
        return tests;
    }

    private Test extractTestFromResultSet(ResultSet rs) throws SQLException {
        Test test = new Test();
        test.setId(rs.getInt("id"));
        test.setHeureDuTest(rs.getInt("heureDuTest"));
        test.setNomMatiere(rs.getString("nomMatiere"));
        test.setNomFormateur(rs.getString("nomFormateur"));
        test.setJourTest(rs.getDate("jourTest").toLocalDate());
        test.setNomSalle(rs.getString("nomSalle"));
        test.setCoefficient(rs.getDouble("coefficient"));
        
        // Création de l'objet Type
        Type type = new Type();
        type.setId(rs.getInt("type_id"));
        type.setMode(rs.getString("type_mode"));
        test.setType_id(type.getId());
        
        return test;
    }
}