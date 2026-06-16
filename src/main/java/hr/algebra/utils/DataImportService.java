package hr.algebra.utils;

import hr.algebra.models.Series;
import hr.algebra.repository.IDirectorRepository;
import hr.algebra.repository.ISeriesRepository;
import hr.algebra.repository.RepositoryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class DataImportService {
    private DataImportService() {}
    private static final Logger log = LoggerFactory.getLogger(DataImportService.class);
    public static final String REMOTE_URL = "https://gist.githubusercontent.com/Programmerpgz/5bf5b8432bde2597032b8815ec9bfffd/raw/ca2264d0d71a68f09ce035c971c5f3b717338489/series_data.xml";
    private static final ISeriesRepository seriesRepo = RepositoryFactory.series();
    private static final IDirectorRepository directorRepo = RepositoryFactory.directors();

    public static void importFromXml(){
        try {
            List<Series> seriesList = SeriesParser.parse(REMOTE_URL);
            if (seriesList.isEmpty()) {
                log.warn("System error: No series found in XML");
                return;
            }
            Set<Series> uniqueSeries = new HashSet<>(seriesList);
            List<Series> finalSafeList = new ArrayList<>(uniqueSeries);

            Function<Series, String> seriesProcessor = s -> {
                s.setDirector(directorRepo.findOrCreate(s.getDirector()));
                try {
                    seriesRepo.save(s);
                } catch (Exception e) {
                    log.error("Failed to save series to database: {}", s.getName(), e);
                }

                return "Successfully processed and saved: " + s.getName() + " | Director: "
                        + (s.getDirector() != null ? s.getDirector().getFullName() : "Unknown");
            };

            Consumer<String> importLogger = msg -> log.info("{}", msg);

            finalSafeList.stream()
                    .filter(s -> s.getName() != null && !s.getName().trim().isEmpty())
                    .sorted(Comparator.comparing(Series::getName))
                    .map(seriesProcessor)
                    .forEach(importLogger);

        } catch (Exception e) {
            log.error("Error during XML import: ", e);
        }
    }
}