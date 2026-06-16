package hr.algebra.repository;

import hr.algebra.models.Actor;
import java.util.List;
import java.util.Optional;

public interface IActorRepository extends IRepository<Actor> {
    void insert(Actor a);
    void update(Actor a);
    Optional<Actor> getById(int id);
    List<Actor> getAll();
    boolean deleteById(int id);
}
