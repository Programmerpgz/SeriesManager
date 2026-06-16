package hr.algebra.repository;

import hr.algebra.models.User;
import java.util.Optional;

public interface IUserRepository {
    void save(User u);
    void insert(User u);
    Optional<User> getByUsername(String username);
    boolean usernameExists(String username);
}
