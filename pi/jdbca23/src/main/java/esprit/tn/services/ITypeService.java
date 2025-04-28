package esprit.tn.services;

import esprit.tn.models.Type;
import java.sql.SQLException;
import java.util.List;

public interface ITypeService {
    void add(Type type) throws SQLException;
    void update(Type type) throws SQLException;
    void delete(int id) throws SQLException;
    Type getById(int id) throws SQLException;
    List<Type> getAll() throws SQLException;
}
