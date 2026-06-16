package hr.algebra.databaserepository;

import hr.algebra.exceptions.RepositoryException;
import hr.algebra.models.Director;
import hr.algebra.repository.IDirectorRepository;
import hr.algebra.utils.DataSourceSingleton;
import hr.algebra.utils.SessionManager;
import hr.algebra.utils.XmlActivityLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DBDirectorRepository implements IDirectorRepository {
    private static final String REPO_NAME = "DirectorRepository";
    private static final Logger log = LoggerFactory.getLogger(DBDirectorRepository.class);
    private static volatile DBDirectorRepository instance;

    private String getCurrentUser(){
        return SessionManager.getCurrentUser().getFullName();
    }

    private DBDirectorRepository() {}

    public static DBDirectorRepository getInstance() {
        if (instance == null) {
            synchronized (DBDirectorRepository.class) {
                if (instance == null) {
                    instance = new DBDirectorRepository();
                }
            }
        }
        return instance;
    }

    @Override
    public void save(Director d) {
        if (d == null) {
            throw RepositoryException.nullEntity(REPO_NAME);
        }

        if (d.getId() == 0) {
            insert(d);
        } else {
            update(d);
        }
    }

    @Override
    public void insert(Director d) {

        String sql = "call insert_director(?, ?, ?, ?, ?)";
        try (Connection conn = DataSourceSingleton.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, d.getName());
            stmt.setString(2, d.getSurname());
            stmt.setInt(3, d.getYearOfBirth());
            stmt.setString(4, d.getNationality());
            stmt.setString(5, d.getBiography());

            stmt.execute();
            String currentUser = SessionManager.getCurrentUser().getFullName();
            log.info("User '{}' inserted director '{} {}'", currentUser, d.getName(), d.getSurname());
            XmlActivityLogger.logActivity(getCurrentUser(), "Added new director: " + d.getFullName(), "INFO");

        } catch (SQLException e) {
            throw RepositoryException.operationNotSuccessful(REPO_NAME, "insert", e);
        }
    }

    @Override
    public void update(Director d) {
        String sql = "call update_director(?, ?, ?, ?, ?, ?)";
        try (Connection conn = DataSourceSingleton.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, d.getId());
            stmt.setString(2, d.getName());
            stmt.setString(3, d.getSurname());
            stmt.setInt(4, d.getYearOfBirth());
            stmt.setString(5, d.getNationality());
            stmt.setString(6, d.getBiography());

            stmt.execute();
            log.info("Updated director with ID: {}", d.getId());
            XmlActivityLogger.logActivity(getCurrentUser(), "Updated director with ID: " + d.getId() + " (" + d.getFullName() + ")", "INFO");
        } catch (SQLException e) {
            throw RepositoryException.operationNotSuccessful(REPO_NAME, "update", e);
        }
    }

    @Override
    public Optional<Director> getById(int id) {
        String sql = "SELECT id, name, surname, year_of_birth, nationality, biography FROM get_director_by_id(?)";
        try (Connection conn = DataSourceSingleton.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try(ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Director(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("surname"),
                            rs.getInt("year_of_birth"),
                            rs.getString("nationality"),
                            rs.getString("biography")
                    ));
                }
            }
        } catch (SQLException e) {
            throw RepositoryException.operationNotSuccessful(REPO_NAME, "getById", e);
        }
        return Optional.empty();
    }

    @Override
    public Director findOrCreate(Director director) {
        if (director == null) {
            return null;
        }
        String sql = "SELECT id, name, surname, year_of_birth, nationality, biography FROM get_or_create_director(?, ?, ?, ?, ?)";
        try (Connection conn = DataSourceSingleton.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, director.getName());
            stmt.setString(2, director.getSurname());
            stmt.setInt(3, director.getYearOfBirth());
            stmt.setString(4, director.getNationality());
            stmt.setString(5, director.getBiography());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Director(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("surname"),
                            rs.getInt("year_of_birth"),
                            rs.getString("nationality"),
                            rs.getString("biography")
                    );
                }
            }

        } catch (SQLException e) {
            throw RepositoryException.operationNotSuccessful(REPO_NAME, "findOrCreate", e);
        }
        return director;
    }

    @Override
    public List<Director> getAll() {
        String sql = "SELECT id, name, surname, year_of_birth, nationality, biography FROM get_all_directors()";
        List<Director> list = new ArrayList<>();
        try (Connection conn = DataSourceSingleton.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Director(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getInt("year_of_birth"),
                        rs.getString("nationality"),
                        rs.getString("biography")
                ));
            }
        } catch (SQLException e) {
            throw RepositoryException.operationNotSuccessful(REPO_NAME, "getAll", e);
        }
        return list;
    }

    @Override
    public boolean deleteById(int id) {

        Optional<Director> director = getById(id);
        if (director.isEmpty()) {
            return false;
        }

        String sql = "CALL delete_director(?)";
        try (Connection conn = DataSourceSingleton.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, id);
            stmt.execute();
            log.info("Deleted director with ID: {}", id);
            XmlActivityLogger.logActivity(getCurrentUser(), "Deleted director with ID: " + director.get().getId() + " (" + director.get().getFullName() + ")" , "INFO");
            return true;
        } catch (SQLException e) {
            throw RepositoryException.operationNotSuccessful(REPO_NAME, "deleteById", e);
        }
    }
}