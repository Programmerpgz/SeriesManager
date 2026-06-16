package hr.algebra.databaserepository;

import hr.algebra.exceptions.RepositoryException;
import hr.algebra.models.Series;
import hr.algebra.repository.IWatchlistRepository;
import hr.algebra.utils.DataSourceSingleton;
import hr.algebra.utils.SessionManager;
import hr.algebra.utils.XmlActivityLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBWatchlistRepository implements IWatchlistRepository {
    private static final Logger log = LoggerFactory.getLogger(DBWatchlistRepository.class);
    private static final String REPO_NAME = "DBWatchlistRepository";
    private String getCurrentUser(){
        return SessionManager.getCurrentUser().getFullName();
    }
    private static volatile DBWatchlistRepository instance;

    private DBWatchlistRepository() {
    }

    public static DBWatchlistRepository getInstance() {
        if (instance == null) {
            synchronized (DBWatchlistRepository.class) {
                if (instance == null) {
                    instance = new DBWatchlistRepository();
                }
            }
        }
        return instance;
    }

    @Override
    public void add(int userId, int seriesId) {
        String sql = "CALL insert_watchlist_item(?, ?)";

        try (Connection conn = DataSourceSingleton.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, seriesId);
            stmt.execute();

            log.info("User '{}' added series with ID {} in watchlist.", getCurrentUser(), seriesId);
            XmlActivityLogger.logActivity(getCurrentUser(), "Added series to watchlist: " + seriesId, "INFO");

        } catch (SQLException e) {
            throw RepositoryException.operationNotSuccessful(REPO_NAME, "add", e);
        }
    }

    @Override
    public void remove(int userId, int seriesId) {
        String sql = "CALL delete_watchlist_item(?, ?)";
        try (Connection conn = DataSourceSingleton.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, seriesId);
            stmt.execute();

            log.info("User '{}' removed series with ID {} from watchlist.", getCurrentUser(), seriesId);
            XmlActivityLogger.logActivity(getCurrentUser(), "Removed series from watchlist: " + seriesId, "INFO");
        } catch (SQLException e) {
            throw RepositoryException.operationNotSuccessful(REPO_NAME, "remove", e);
        }
    }

    @Override
    public List<Series> getAll(int userId) {
        List<Series> list = new ArrayList<>();
        String sql = "SELECT idseries, seriesname FROM get_user_watchlist(?)";

        try (Connection conn = DataSourceSingleton.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Series s = new Series();
                    s.setId(rs.getInt("idSeries"));
                    s.setName(rs.getString("seriesName"));
                    list.add(s);
                }
            }
        } catch (SQLException e) {
            throw RepositoryException.operationNotSuccessful(REPO_NAME, "getAll", e);
        }
        return list;
    }
}
