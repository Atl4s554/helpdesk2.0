package DAO;

import java.util.List;

/** Classe que define o CRUD e as classes abstratas para se comunicar com o banco de dados **/

public interface DAO<T> {
    void create(T entity);
    T read(int id);
    void update(T entity);
    void delete(int id);
    List<T> findAll();
}
