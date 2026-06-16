package hr.algebra.repository;

import hr.algebra.models.Director;
import java.util.List;
import java.util.Optional;

public interface IDirectorRepository extends IRepository<Director> {
    void save(Director d);
    void insert(Director d);
    void update(Director d);
    Director findOrCreate(Director director);
    Optional<Director> getById(int id);
    List<Director> getAll();
    boolean deleteById(int id);
}
