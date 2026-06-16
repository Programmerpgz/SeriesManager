package hr.algebra.databaserepository;

import hr.algebra.exceptions.RepositoryException;
import hr.algebra.models.*;
import hr.algebra.repository.ISeriesRepository;
import hr.algebra.services.FileService;
import hr.algebra.services.SeriesRelationService;
import hr.algebra.utils.DataSourceSingleton;
import hr.algebra.utils.SessionManager;
import hr.algebra.utils.XmlActivityLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DBSeriesRepository implements ISeriesRepository {
    private static final Logger log = LoggerFactory.getLogger(DBSeriesRepository.class);
    private static final String REPO_NAME = "DBSeriesRepository";
    private static volatile DBSeriesRepository instance;
    private String getCurrentUser(){
        return SessionManager.getCurrentUser().getFullName();
    }
    private DBSeriesRepository() {
    }
    public static DBSeriesRepository getInstance() {
        if (instance == null) {
            synchronized (DBSeriesRepository.class) {
                if (instance == null) {
                    instance = new DBSeriesRepository();
                }
            }
        }
        return instance;
    }

    @Override
    public void save(Series s) {
        if (s == null) {
            throw RepositoryException.nullEntity(REPO_NAME);
        }
        if (s.getId() == 0) {
            insert(s);
        } else {
            update(s);
        }
    }

    @Override
    public void insert(Series s) {
        if (s.getDirector() != null && s.getDirector().getId() == 0) {
            DBDirectorRepository.getInstance().save(s.getDirector());
        }

        String genresStr = s.getGenres().stream()
                .map(Genre::getDescription)
                .collect(Collectors.joining(", "));
        String platformsStr = s.getStreamingPlatforms().stream()
                .map(StreamingPlatform::getName)
                .collect(Collectors.joining(", "));

        String sql = "CALL insert_series(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DataSourceSingleton.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, s.getName());
            stmt.setInt(2, s.getNumberOfSeasons());
            stmt.setInt(3, s.getNumberOfEpisodes());
            stmt.setInt(4, s.getYearOfRelease());

            if (s.getEndYear() == null) {
                stmt.setNull(5, Types.INTEGER);
            } else {
                stmt.setInt(5, s.getEndYear());
            }

            stmt.setString(6, s.getDescription());
            stmt.setString(7, s.getPosterPath());

            stmt.setString(8, genresStr);
            stmt.setString(9, platformsStr);

            if (s.getDirector() == null) {
                stmt.setNull(10, Types.INTEGER);
            } else {
                stmt.setInt(10, s.getDirector().getId());
            }

            stmt.execute();

            int newId = getLastInsertedId(conn);
            s.setId(newId);


            SeriesRelationService.saveActors(conn, newId, s.getActors());
            SeriesRelationService.saveGenres(conn, newId, s.getGenres());
            SeriesRelationService.savePlatforms(conn, newId, s.getStreamingPlatforms());
            log.info("Series successfully inserted: {}", s.getName());
            XmlActivityLogger.logActivity(getCurrentUser(), "Added new series: " + s.getName(), "INFO");

        } catch (SQLException e) {
            throw RepositoryException.operationNotSuccessful(REPO_NAME, "insert", e);
        }
    }

    @Override
    public void update(Series s) {
        Optional<Series> oldSeries = getById(s.getId());
        String oldPosterPath = oldSeries.map(Series::getPosterPath).orElse(null);

        String genresStr = s.getGenres().stream()
                .map(Genre::getDescription)
                .collect(Collectors.joining(", "));

        String platformsStr = s.getStreamingPlatforms().stream()
                .map(StreamingPlatform::getName)
                .collect(Collectors.joining(", "));

        String sql = "CALL update_series(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DataSourceSingleton.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, s.getId());
            stmt.setString(2, s.getName());
            stmt.setInt(3, s.getNumberOfSeasons());
            stmt.setInt(4, s.getNumberOfEpisodes());
            stmt.setInt(5, s.getYearOfRelease());
            if (s.getEndYear() == null) {
                stmt.setNull(6, Types.INTEGER);
            } else {
                stmt.setInt(6, s.getEndYear());
            }
            stmt.setString(7, s.getDescription());
            stmt.setString(8, s.getPosterPath());
            stmt.setString(9, genresStr);
            stmt.setString(10, platformsStr);

            if (s.getDirector() == null) {
                stmt.setNull(11, Types.INTEGER);
            } else {
                stmt.setInt(11, s.getDirector().getId());
            }
            stmt.execute();

            if (oldPosterPath != null && !oldPosterPath.equals(s.getPosterPath())) {
                FileService.deleteImageFile(oldPosterPath);
            }

            SeriesRelationService.deleteActors(conn, s.getId());
            SeriesRelationService.deleteGenres(conn, s.getId());
            SeriesRelationService.deletePlatforms(conn, s.getId());

            SeriesRelationService.saveActors(conn, s.getId(), s.getActors());
            SeriesRelationService.saveGenres(conn, s.getId(), s.getGenres());
            SeriesRelationService.savePlatforms(conn, s.getId(), s.getStreamingPlatforms());
            log.info("Successfully updates series with ID: {}", s.getId());
            XmlActivityLogger.logActivity(getCurrentUser(), "Updated series with ID: " + s.getId() + " (" + s.getName() + ")", "INFO");


        } catch (SQLException e) {
            throw RepositoryException.operationNotSuccessful(REPO_NAME, "update", e);
        }
    }

    @Override
    public Optional<Series> getById(int id) {
        String sql = "SELECT id, name, number_of_seasons, number_of_episodes, year_of_release, end_year, description, " +
                "poster_path, director_id, genre, platform FROM get_series_by_id(?)";
        try (Connection conn = DataSourceSingleton.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Series s = mapRowToSeries(rs);
                SeriesRelationService.loadAll(conn, s);
                return Optional.of(s);
            }
        } catch (SQLException e) {
            throw RepositoryException.operationNotSuccessful(REPO_NAME, "getById", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Series> getAll() {
        String sql = "SELECT id, name, number_of_seasons, number_of_episodes, year_of_release, end_year, description, " +
                "poster_path, director_id, genre, platform FROM get_all_series()";
        List<Series> list = new ArrayList<>();
        try (Connection conn = DataSourceSingleton.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Series s = mapRowToSeries(rs);
                SeriesRelationService.loadAll(conn, s);
                list.add(s);
            }
        } catch (SQLException e) {
            throw RepositoryException.operationNotSuccessful(REPO_NAME, "getAll", e);
        }
        return list;
    }

    @Override
    public boolean deleteById(int id) {
        Optional<Series> series = getById(id);
        String posterPath = series.map(Series::getPosterPath).orElse(null);

        String sql = "CALL delete_series(?)";
        try (Connection conn = DataSourceSingleton.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, id);
            stmt.execute();

            if (posterPath != null && !posterPath.trim().isEmpty()) {
                FileService.deleteImageFile(posterPath);
            }
            log.info("Deleted series wih ID: {}", id);
            XmlActivityLogger.logActivity(getCurrentUser(), "Deleted series with ID: " + series.get().getId() + " (" + series.get().getName() + ")", "INFO");
            return true;
        } catch (SQLException e) {
            throw RepositoryException.operationNotSuccessful(REPO_NAME, "deleteById", e);
        }
    }

    @Override
    public List<Series> getByPlatform(StreamingPlatform platform) {
        String sql = "SELECT id, name, number_of_seasons, number_of_episodes, year_of_release, end_year, description, " +
                "poster_path, director_id, genre, platform FROM get_series_by_platform(?)";
        List<Series> result = new ArrayList<>();
        try (Connection conn = DataSourceSingleton.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + platform.getName() + "%");
            try(ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Series s = mapRowToSeries(rs);
                    SeriesRelationService.loadAll(conn, s);
                    result.add(s);
                }
            }
        } catch (SQLException e) {
            throw RepositoryException.operationNotSuccessful(REPO_NAME, "getByPlatform", e);
        }
        return result;
    }

    @Override
    public List<Series> getByGenre(Genre genre) {
        String searchTerm = genre.getDescription();
        String sql = "SELECT id, name, number_of_seasons, number_of_episodes, year_of_release, end_year, description, " +
                "poster_path, director_id, genre, platform FROM get_series_by_genre(?)";
        List<Series> result = new ArrayList<>();
        try (Connection conn = DataSourceSingleton.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, searchTerm);
            try(ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Series s = mapRowToSeries(rs);
                SeriesRelationService.loadAll(conn, s);
                result.add(s);
            }
        }
        } catch (SQLException e) {
            throw RepositoryException.operationNotSuccessful(REPO_NAME, "getByGenre", e);
        }
        return result;
    }

    @Override
    public List<Series> getByStillEmitting() {
        String sql = "SELECT id, name, number_of_seasons, number_of_episodes, year_of_release, end_year, description, " +
                "poster_path, director_id, genre, platform FROM get_emitting_series()";
        List<Series> result = new ArrayList<>();
        try (Connection conn = DataSourceSingleton.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Series s = mapRowToSeries(rs);
                SeriesRelationService.loadAll(conn, s);
                result.add(s);
            }
        } catch (SQLException e) {
            throw RepositoryException.operationNotSuccessful(REPO_NAME, "getByStillEmitting", e);
        }
        return result;
    }

    @Override
    public List<Series> searchByName(String keyword) {
        String sql = "SELECT id, name, number_of_seasons, number_of_episodes, year_of_release, end_year, description," +
                "poster_path, director_id, genre, platform FROM search_series(?)";
        List<Series> result = new ArrayList<>();
        try (Connection conn = DataSourceSingleton.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, keyword);
            try(ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Series s = mapRowToSeries(rs);
                    SeriesRelationService.loadAll(conn, s);
                    result.add(s);
                }
            }
        } catch (SQLException e) {
            throw RepositoryException.operationNotSuccessful(REPO_NAME, "searchByName", e);
        }
        return result;
    }

    private Series mapRowToSeries(ResultSet rs) throws SQLException {
        int directorId = rs.getInt("director_id");
        Director director = null;
        if (!rs.wasNull()) {
            director = DBDirectorRepository.getInstance().getById(directorId).orElse(null);
        }
        Series s = new Series(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("number_of_seasons"),
                rs.getInt("number_of_episodes"),
                rs.getInt("year_of_release"),
                (Integer) rs.getObject("end_year"),
                rs.getString("description")
        );

        s.setGenreString(rs.getString("genre"));
        s.setPlatformString(rs.getString("platform"));
        s.setPosterPath(rs.getString("poster_path"));
        s.setDirector(director);
        return s;
    }

    private int getLastInsertedId(Connection conn) throws SQLException {
        String sql = "SELECT lastval()";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        throw new SQLException("Cannot retrieve the last ID.");
    }

    @Override
    public void clearEverything() {
        String sql = "TRUNCATE TABLE series_actor, series_genre, series_platform, series, actor, director RESTART IDENTITY CASCADE";
        try (Connection con = DataSourceSingleton.getConnection();
             Statement st = con.createStatement()) {
            st.executeUpdate(sql);
            log.info("All database is successfully deleted!");
            XmlActivityLogger.logActivity(getCurrentUser(), "All series successfully deleted from database.", "INFO");
        } catch (Exception e) {
            log.error("Error while deleting database!", e);
        }
    }
}