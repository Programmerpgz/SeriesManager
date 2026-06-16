package hr.algebra.repository;

import java.util.List;
import java.util.Optional;

public interface IRepository<T> {
    void insert(T entity);
    void update(T entity);
    Optional<T> getById(int id);
    List<T> getAll();
    boolean deleteById(int id);
}
