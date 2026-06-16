package hr.algebra.services;

import hr.algebra.models.Actor;
import hr.algebra.models.Genre;
import hr.algebra.models.Series;
import hr.algebra.models.StreamingPlatform;
import java.sql.*;
import java.util.List;

public class SeriesRelationService {

    private SeriesRelationService() {}

    public static void loadAll(Connection conn, Series s) throws SQLException {
        loadActors(conn, s);
        loadGenres(conn, s);
        loadPlatforms(conn, s);
    }

    private static void loadActors(Connection conn, Series s) throws SQLException {
        String sql = "SELECT id, name, nationality, year_of_birth, surname FROM get_actors_by_series(?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, s.getId());
            try(ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    s.addActor(new Actor(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("surname"),
                            rs.getInt("year_of_birth"),
                            rs.getString("nationality")
                    ));
                }
            }
        }
    }

    private static void loadGenres(Connection conn, Series s) throws SQLException {
        String sql = "SELECT genre FROM get_genres_by_series(?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, s.getId());
            try(ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    s.addGenre(Genre.valueOf(rs.getString("genre")));
                }
            }
        }
    }

    private static void loadPlatforms(Connection conn, Series s) throws SQLException {
        String sql = "SELECT platform FROM get_platforms_by_series(?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, s.getId());
            try(ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    s.addStreamingPlatform(
                            StreamingPlatform.valueOf(rs.getString("platform")));
                }
            }
        }
    }

    public static void saveActors(Connection conn, int seriesId, List<Actor> actors) throws SQLException {
        String sql = "CALL insert_series_actor(?, ?, ?, ?, ?)";
        for (Actor actor : actors) {
            try (CallableStatement stmt = conn.prepareCall(sql)) {
                stmt.setInt(1, seriesId);
                stmt.setString(2, actor.getName());
                stmt.setString(3, actor.getSurname());
                stmt.setInt(4, actor.getYearOfBirth());
                stmt.setString(5, actor.getNationality());

                stmt.execute();
            }
        }
    }

    public static void saveGenres(Connection conn, int seriesId, List<Genre> genres) throws SQLException {
        String sql = "CALL insert_series_genre(?, ?)";
        for (Genre genre : genres) {
            try (CallableStatement stmt = conn.prepareCall(sql)) {
                stmt.setInt(1, seriesId);
                stmt.setString(2, genre.name());
                stmt.execute();
            }
        }
    }

    public static void savePlatforms(Connection conn, int seriesId, List<StreamingPlatform> platforms) throws SQLException {
        String sql = "CALL insert_series_platform(?, ?)";
        for (StreamingPlatform platform : platforms) {
            try (CallableStatement stmt = conn.prepareCall(sql)) {
                stmt.setInt(1, seriesId);
                stmt.setString(2, platform.name());
                stmt.execute();
            }
        }
    }

    public static void deleteActors(Connection conn, int seriesId) throws SQLException {
        String sql = "CALL delete_series_actors(?)";
        try (CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, seriesId);
            stmt.execute();
        }
    }

    public static void deleteGenres(Connection conn, int seriesId) throws SQLException {
        String sql = "CALL delete_series_genres(?)";
        try (CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, seriesId);
            stmt.execute();
        }
    }

    public static void deletePlatforms(Connection conn, int seriesId) throws SQLException {
        String sql = "CALL delete_series_platforms(?)";
        try (CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, seriesId);
            stmt.execute();
        }
    }
}
