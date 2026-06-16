package hr.algebra.controllers;

import hr.algebra.repository.ISeriesRepository;
import hr.algebra.repository.RepositoryFactory;
import hr.algebra.utils.*;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.file.Path;
import java.util.Set;

public class AdminDashboardController{
    private static final Logger log = LoggerFactory.getLogger(AdminDashboardController.class);
    private final ISeriesRepository seriesRepo = RepositoryFactory.series();
    private static final Set<String> postersWhoStay = Set.of(
            "1775827426087_noimageavaliable.jpg",
            "last_of_us.jpg",
            "stranger_things.jpg",
            "succession.jpg",
            "the_bear.jpg"
    );

    @FXML
    private void onWipeData() {
        boolean confirm = AlertUtil.showConfirm("Confirmation", "This will delete the database and all images. Are you sure?");

        if (confirm) {
            try {
                seriesRepo.clearEverything();

                Path assetsPath = Path.of("assets");
                AssetsCleanupUtil.deleteAllPostersExceptDefault(assetsPath, postersWhoStay);
                AlertUtil.showInfo("Success", "Database and assets have been successfully deleted!");
                log.info("Database and assets deleted.");
                XmlActivityLogger.logActivity(SessionManager.getCurrentUser().getFullName(), "Deleted the entire database and non-default assets.", "WARN");
            } catch (Exception e) {
                AlertUtil.showError("Error", "Error deleting data!");
                log.error("Error while deleting database.", e);
            }
        }
    }

    @FXML
    private void onUploadData(){
        log.info("Import data from XML started.");
        Task<Void> importTask = new Task<>() {
            @Override
            protected Void call(){
                DataImportService.importFromXml();
                return null;
            }
        };

        importTask.setOnSucceeded(event -> {
            log.info("Import data successful!");
            XmlActivityLogger.logActivity(SessionManager.getCurrentUser().getFullName(), "User started automatic online data import.", "INFO");
            AlertUtil.showInfo("Success", "New data uploaded successfully from the online source.");
            SceneUtil.switchScene(SceneUtil.MAIN_VIEW, "Series Catalog");
        });

        importTask.setOnFailed(event -> {
            Throwable e = importTask.getException();
            log.error("Import data from XML failed.", e);
            AlertUtil.showError("Import Failed","Online source cannot be reached: " + e.getMessage());
        });

        Thread backgroundThread = new Thread(importTask);
        backgroundThread.setDaemon(true);
        backgroundThread.start();
    }

    @FXML
    private void onContinue() {
        SceneUtil.switchScene(SceneUtil.MAIN_VIEW, "Series Catalog");
    }
}
