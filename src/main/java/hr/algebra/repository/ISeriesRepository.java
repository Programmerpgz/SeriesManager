package hr.algebra.repository;

import hr.algebra.models.Genre;
import hr.algebra.models.Series;
import hr.algebra.models.StreamingPlatform;

import java.util.List;
import java.util.Optional;

public interface ISeriesRepository extends IRepository<Series> {
    void save(Series s);
    Optional<Series> getById(int id);
    List<Series> getAll();
    boolean deleteById(int id);
    List<Series> getByPlatform(StreamingPlatform streamingPlatform);
    List<Series> getByGenre(Genre genre);
    List<Series> getByStillEmitting();
    List<Series> searchByName(String keyword);
    void clearEverything();
}
