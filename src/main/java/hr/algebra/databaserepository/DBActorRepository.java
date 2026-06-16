package hr.algebra.databaserepository;

import hr.algebra.exceptions.RepositoryException;
import hr.algebra.models.Actor;
import hr.algebra.repository.IActorRepository;
import hr.algebra.utils.DataSourceSingleton;
import hr.algebra.utils.SessionManager;
import hr.algebra.utils.XmlActivityLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DBActorRepository implements IActorRepository {
    private static final String REPO_NAME = "ActorRepository";
    private static final Logger log = LoggerFactory.getLogger(DBActorRepository.class);
    private static volatile DBActorRepository instance;
    private DBActorRepository() {}

    private String getCurrentUser(){
        return SessionManager.getCurrentUser().getFullName();
    }

    public static DBActorRepository getInstance() {
        if (instance == null) {
            synchronized (DBActorRepository.class) {
                if (instance == null) {
                    instance = new DBActorRepository();
                }
            }
        }
        return instance;
    }

    @Override
    public void insert(Actor a) {
        String sql  = "call insert_actor(?, ?, ?, ?)";
        try (Connection conn = DataSourceSingleton.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, a.getName());
            stmt.setString(2, a.getSurname());
            stmt.setInt(3, a.getYearOfBirth());
            stmt.setString(4, a.getNationality());

            stmt.execute();
            log.info("Added actor: {}", a.getSurname());
            XmlActivityLogger.logActivity(getCurrentUser(), "Added new actor: " + a.getFullName(), "INFO");
        }catch (SQLException e){
            throw RepositoryException.operationNotSuccessful(REPO_NAME, "insert", e);
        }
    }

    @Override
    public void update(Actor a) {

        String sql = "call update_actor(?, ?, ?, ?, ?)";
        try (Connection conn = DataSourceSingleton.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, a.getId());
            stmt.setString(2, a.getName());
            stmt.setString(3, a.getSurname());
            stmt.setInt(4, a.getYearOfBirth());
            stmt.setString(5, a.getNationality());

            stmt.execute();
            log.info("Updated actor ID: {}", a.getId());
            XmlActivityLogger.logActivity(getCurrentUser(), "Updated actor with ID: " + a.getId() + " (" + a.getFullName() + ")", "INFO");

        } catch (SQLException e) {
            throw RepositoryException.operationNotSuccessful(REPO_NAME, "update", e);
        }
    }

    @Override
    public Optional<Actor> getById(int id) {
        String sql = "SELECT id, name, surname, year_of_birth, nationality FROM get_actor_by_id(?)";
        try (Connection conn = DataSourceSingleton.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try(ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Actor a = new Actor(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("surname"),
                            rs.getInt("year_of_birth"),
                            rs.getString("nationality")
                            );
                    return Optional.of(a);
                }
            }
        } catch (SQLException e) {
            throw RepositoryException.operationNotSuccessful(REPO_NAME, "getById", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Actor> getAll() {
        String sql = "SELECT id, name, surname, year_of_birth, nationality FROM get_all_actors()";
        List<Actor> list = new ArrayList<>();
        try (Connection conn = DataSourceSingleton.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Actor a = new Actor(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getInt("year_of_birth"),
                        rs.getString("nationality")
                );
                list.add(a);
            }
        } catch (SQLException e) {
            throw RepositoryException.operationNotSuccessful(REPO_NAME, "getAll", e);
        }
        return list;
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "CALL delete_actor(?)";
        try (Connection conn = DataSourceSingleton.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, id);
            stmt.execute();
            log.info("Deleted actor with ID: {}", id);
            XmlActivityLogger.logActivity(getCurrentUser(), "Deleted actor with ID: " + id, "INFO");
            return true;
        } catch (SQLException e) {
            throw RepositoryException.operationNotSuccessful(REPO_NAME, "deleteById", e);
        }
    }
}