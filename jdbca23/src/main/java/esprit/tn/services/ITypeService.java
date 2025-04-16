package esprit.tn.services;

import esprit.tn.entities.Type;
import java.util.List;

public interface ITypeService {
    void add(Type type);
    void update(Type type);
    void delete(int id);
    Type getById(int id);
    List<Type> getAll();
} 