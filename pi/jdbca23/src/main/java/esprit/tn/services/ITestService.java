package esprit.tn.services;

import esprit.tn.models.Test;
import java.sql.SQLException;
import java.util.List;

public interface ITestService {
    void add(Test test) throws SQLException;
    void update(Test test) throws SQLException;
    void delete(int id) throws SQLException;
    Test getById(int id) throws SQLException;
    List<Test> getAll() throws SQLException;
}