package hr.algebra.databaserepository;

import hr.algebra.exceptions.RepositoryException;
import hr.algebra.models.Role;
import hr.algebra.models.User;
import hr.algebra.repository.IUserRepository;
import hr.algebra.utils.DataSourceSingleton;
import hr.algebra.utils.XmlActivityLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.Optional;

public class DBUserRepository implements IUserRepository {
    private static final Logger log = LoggerFactory.getLogger(DBUserRepository.class);
    private static final String REPO_NAME = "DBUserRepository";
    private static volatile DBUserRepository instance;
    private DBUserRepository() {}

    public static DBUserRepository getInstance() {
        if (instance == null) {
            synchronized (DBUserRepository.class) {
                if (instance == null) {
                    instance = new DBUserRepository();
                }
            }
        }
        return instance;
    }

    @Override
    public void save(User u) {
        if (u == null) {
            throw RepositoryException.nullEntity(REPO_NAME);
        }
        if (u.getId() == 0) {
            insert(u);
        }
    }

    @Override
    public void insert(User u) {
        String sql = "CALL insert_user(?, ?, ?, ?, ?, ?)";
        try (Connection conn = DataSourceSingleton.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, u.getUsername());
            stmt.setString(2, u.getPassword());
            stmt.setString(3, u.getName());
            stmt.setString(4, u.getSurname());
            stmt.setString(5, u.getEmail());
            stmt.setString(6, u.getRole().name());
            stmt.execute();
            log.info("Successfully inserted user in database: {}", u.getUsername());

            String registeringUser = u.getFullName();
            XmlActivityLogger.logActivity(registeringUser, "Added new user: " + u.getFullName(), "INFO");
        } catch (SQLException e) {
            throw RepositoryException.operationNotSuccessful(REPO_NAME, "insert", e);
        }
    }

    @Override
    public Optional<User> getByUsername(String username) {
        String sql = "SELECT id, username, password, surname, name, email, role FROM get_user_by_username(?)";
        try (Connection conn = DataSourceSingleton.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try(ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("surname"),
                            rs.getString("name"),
                            rs.getString("email"),
                            Role.valueOf(rs.getString("role"))
                    );
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw RepositoryException.operationNotSuccessful(REPO_NAME, "getByUsername", e);
        }
        return Optional.empty();
    }

    @Override
    public boolean usernameExists(String username) {
        String sql = "SELECT check_username_exists(?)";
        try (Connection conn = DataSourceSingleton.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw RepositoryException.operationNotSuccessful(REPO_NAME, "usernameExists", e);
        }
        return false;
    }
}