package hr.algebra.repository;

import hr.algebra.models.Series;

import java.util.List;

public interface IWatchlistRepository {
    void add(int userId, int seriesId);
    void remove(int userId, int seriesId);
    List<Series> getAll(int userId);
}
