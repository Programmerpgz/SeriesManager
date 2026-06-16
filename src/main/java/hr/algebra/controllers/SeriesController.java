package hr.algebra.controllers;

import hr.algebra.models.*;
import hr.algebra.repository.ISeriesRepository;
import hr.algebra.repository.IWatchlistRepository;
import hr.algebra.repository.RepositoryFactory;
import hr.algebra.services.FormDialogService;
import hr.algebra.utils.AlertUtil;
import hr.algebra.utils.SceneUtil;
import hr.algebra.utils.XmlActivityLogger;
import hr.algebra.utils.XmlUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static hr.algebra.utils.SessionManager.getCurrentUser;

public class SeriesController {

    @FXML private TableView<Series> tableSeries;
    @FXML private TableColumn<Series, String> colPoster;
    @FXML private TableColumn<Series, String> colName;
    @FXML private TableColumn<Series, Integer> colSeasons;
    @FXML private TableColumn<Series, Integer> colEpisodes;
    @FXML private TableColumn<Series, String> colPeriod;
    @FXML private TableColumn<Series, String> colGenres;
    @FXML private TableColumn<Series, String> colPlatforms;
    @FXML private TableColumn<Series, String> colDirector;
    @FXML private TableColumn<Series, String> colActors;
    @FXML private TableColumn<Series, String> colDescription;
    @FXML private Label lblStatus;
    private static final Logger log = LoggerFactory.getLogger(SeriesController.class);
    private static final String FILTER_ERROR = "Filter error";
    private static final String ERROR_MESSAGE = "Error";
    private final ISeriesRepository seriesRepo = RepositoryFactory.series();
    private final IWatchlistRepository watchlistRepo = RepositoryFactory.watchlist();

    @FXML
    public void initialize() {
        setupColumns();
        setupWatchlistContextMenu();
        refresh();
    }

    public void refresh() {
        showSeries(seriesRepo.getAll(), "All series");
    }

    private void setupColumns() {
        colPoster.setCellValueFactory(new PropertyValueFactory<>("posterPath"));
        colPoster.setCellFactory(column -> createPosterTableCell());
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colSeasons.setCellValueFactory(new PropertyValueFactory<>("numberOfSeasons"));
        colEpisodes.setCellValueFactory(new PropertyValueFactory<>("numberOfEpisodes"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPeriod.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getEmittingPeriod()));

        colGenres.setCellValueFactory(cellData -> {
            Series s = cellData.getValue();
            if (s.getGenres() == null || s.getGenres().isEmpty()) {
                return new SimpleStringProperty("N/A");
            }
            return new SimpleStringProperty(
                    s.getGenres().stream()
                            .map(genre -> genre.toString().replace("_", " "))
                            .collect(Collectors.joining(", "))
            );
        });

        colPlatforms.setCellValueFactory(cellData -> {
            Series s = cellData.getValue();
            if (s.getStreamingPlatforms() == null || s.getStreamingPlatforms().isEmpty()) {
                return new SimpleStringProperty("N/A");
            }
            return new SimpleStringProperty(
                    s.getStreamingPlatforms().stream()
                            .map(p -> p.toString().replace("_", " ").replace("Plus", "+"))
                            .collect(Collectors.joining(", "))
            );
        });

        colDirector.setCellValueFactory(cell -> {
            Director d = cell.getValue().getDirector();
            return new SimpleStringProperty(d != null ? d.getFullName() : "—");
        });

        colActors.setCellValueFactory(cell -> {
            String actors = cell.getValue().getActors().stream()
                    .map(Actor::getFullName)
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(actors.isEmpty() ? "—" : actors);
        });
    }

    public void onAllSeries() {
        refresh();
    }

    public void onAddSeries() {
        SceneUtil.openModal(SceneUtil.SERIES_FORM, "New series");
        refresh();
    }

    public void onEditSeries() {
        Series selected = getSelectedSeries();
        if (selected == null) {
            return;
        }
        FormDialogService.openSeriesEditForm(selected);
        try {
            if (getCurrentUser() != null) {
                XmlActivityLogger.logActivity(getCurrentUser().getFullName(), "Series edited: " + selected.getName(), "WARN");
            }
        } catch (Exception e) {
            log.error("Failed to write log for series edit dialog", e);
        }
        refresh();
    }

    public void onDeleteSeries() {
        Series selected = getSelectedSeries();
        if (selected == null) {
            return;
        }
        if (!AlertUtil.showDeleteConfirm(selected.getName())) {
            return;
        }
        try {
            seriesRepo.deleteById(selected.getId());
            XmlActivityLogger.logActivity(getCurrentUser().getFullName(), "Deleted series: " + selected.getName(), "WARN");
            refresh();
            AlertUtil.showInfo("Deleted", "Series '" + selected.getName() + "' has been deleted.");
        } catch (Exception e) {
            log.error("Database error while deleting series: {}", selected.getName(), e);
            AlertUtil.showError("Delete failed", "Failed while trying to delete the series." + e.getMessage());
        }
    }

    public void onFilterByPlatform() {
        ChoiceDialog<StreamingPlatform> dialog = new ChoiceDialog<>(StreamingPlatform.NETFLIX, StreamingPlatform.values());
        dialog.setTitle("Filter by platform");
        dialog.setHeaderText("Select platform:");
        dialog.showAndWait().ifPresent(platform -> {
            try {
                List<Series> filtered = seriesRepo.getByPlatform(platform);
                showSeries(filtered, "Platform: " + platform.name());
            } catch (Exception e) {
                log.error("Error occurred while filtering by platform: ", e);
                AlertUtil.showError(FILTER_ERROR, "Failed while filtering by platform: " + e.getMessage());
            }
        });
    }

    public void onFilterByGenre() {
        ChoiceDialog<Genre> dialog = new ChoiceDialog<>(Genre.DRAMA, Genre.values());
        dialog.setTitle("Filter by genre");
        dialog.setHeaderText("Select genre:");
        dialog.showAndWait().ifPresent(genre -> {
            try {
                List<Series> filtered = seriesRepo.getByGenre(genre);
                showSeries(filtered, "Genre: " + genre.name());
            } catch (Exception e) {
                log.error("Error occurred while filtering by genre: ", e);
                AlertUtil.showError(FILTER_ERROR, "Failed while filtering by genre: " + e.getMessage());
            }
        });
    }

    public void onFilterStillEmitting() {
        try {
            List<Series> filtered = seriesRepo.getByStillEmitting().stream()
                    .sorted(Comparator.comparing(Series::getName))
                    .toList();
            showSeries(filtered, "Still emitting");
        } catch (Exception e) {
            log.error("Error occurred while filtering by still emitting: ", e);
            AlertUtil.showError(FILTER_ERROR, "Failed while filtering still emitting series: " + e.getMessage());
        }
    }

    public void onSearch(String keyword) {
        try {
            if (keyword == null || keyword.isBlank()) {
                refresh();
                return;
            }
            List<Series> results = seriesRepo.searchByName(keyword);
            showSeries(results, "Search: \"" + keyword + "\"");
        } catch (Exception e) {
            log.error("Error occurred while searching: ", e);
            AlertUtil.showError("Search Error", "Failed while searching: " + e.getMessage());
        }
    }

    public void onExportToXml() {
        Series selected = getSelectedSeries();
        if (selected == null) {
            AlertUtil.showWarning("Warning", "Please select a series to export.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Series XML");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files", "*.xml"));
        File file = fileChooser.showSaveDialog(tableSeries.getScene().getWindow());

        if (file != null) {
            Task<Void> exportTask = new Task<>() {
                @Override
                protected Void call() {
                    XmlUtils.saveToXml(selected, file.getAbsolutePath());
                    return null;
                }
            };
            exportTask.setOnSucceeded(e -> {
                XmlActivityLogger.logActivity(getCurrentUser().getFullName(), "Export: " + selected.getName(), "INFO");
                AlertUtil.showInfo("Export Successful", "Series '" + selected.getName() + "' has been exported to XML.");
            });
            exportTask.setOnFailed(e -> {
                Throwable ex = exportTask.getException();
                AlertUtil.showError("Export Failed", "Error saving XML: " + (ex != null ? ex.getMessage() : "unknown"));
            });
            Thread backgroundThread = new Thread(exportTask);
            backgroundThread.setDaemon(true);
            backgroundThread.start();
        }
    }

    public void onBackupAll() {
        List<Series> allSeries = seriesRepo.getAll();
        if (allSeries.isEmpty()) {
            AlertUtil.showWarning("Backup failed", "No data in database to backup.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save All Series (Backup)");
        fileChooser.setInitialFileName("All_Series_Backup.xml");
        File file = fileChooser.showSaveDialog(tableSeries.getScene().getWindow());

        if (file != null) {
            Task<Void> backupTask = new Task<>() {
                @Override
                protected Void call() {
                    XmlUtils.saveToXml(new SeriesWrapper(allSeries), file.getAbsolutePath());
                    return null;
                }
            };
            backupTask.setOnSucceeded(e -> {
                AlertUtil.showInfo("Backup Successful", "All series have been backed up.");
                XmlActivityLogger.logActivity(getCurrentUser().getFullName(), "Backup Successful", "INFO");
            });
            backupTask.setOnFailed(e -> {
                Throwable ex = backupTask.getException();
                AlertUtil.showError("Backup Failed", "Error saving backup: " + ex.getMessage());
            });
            Thread backgroundThread = new Thread(backupTask);
            backgroundThread.setDaemon(true);
            backgroundThread.start();
        }
    }

    public void setupWatchlistContextMenu() {
        User user = getCurrentUser();
        if (user == null || user.isAdministrator()) {
            return;
        }

        ContextMenu watchlistMenu = new ContextMenu();
        MenuItem addItem = new MenuItem("Add to watchlist");
        MenuItem removeItem = new MenuItem("Remove from watchlist");

        addItem.setOnAction(e -> handleAddToWatchlist(user));
        removeItem.setOnAction(e -> handleRemoveFromWatchlist(user));

        watchlistMenu.getItems().addAll(addItem, removeItem);
        tableSeries.setContextMenu(watchlistMenu);
    }

    private void handleAddToWatchlist(User user) {
        Series selected = getSelectedSeries();
        if (selected == null){
            return;
        }
        try {
            watchlistRepo.add(user.getId(), selected.getId());
            AlertUtil.showInfo("Added", selected.getName() + " to watchlist!");
        } catch (Exception ex) {
            AlertUtil.showError(ERROR_MESSAGE, "Failed adding series to watchlist: " + ex.getMessage());
        }
    }

    private void handleRemoveFromWatchlist(User user) {
        Series selected = getSelectedSeries();
        if (selected == null) {
            return;
        }
        try {
            watchlistRepo.remove(user.getId(), selected.getId());
            AlertUtil.showInfo("Removed", selected.getName() + " from watchlist!");
        } catch (Exception ex) {
            AlertUtil.showError(ERROR_MESSAGE, "Failed removing series from watchlist: " + ex.getMessage());
        }
    }

    private void showSeries(List<Series> series, String status) {
        tableSeries.setItems(FXCollections.observableArrayList(series));
        lblStatus.setText(status);
        tableSeries.refresh();
    }

    public Series getSelectedSeries() {
        return tableSeries.getSelectionModel().getSelectedItem();
    }

    private TableCell<Series, String> createPosterTableCell() {
        return new TableCell<>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);

                if (empty || imagePath == null || imagePath.isBlank()) {
                    setGraphic(null);
                    return;
                }

                File file = new File(imagePath);
                if (!file.exists()) {
                    setGraphic(null);
                    return;
                }

                Image image = new Image(file.toURI().toString(), 50, 70, true, true);
                imageView.setImage(image);
                setGraphic(imageView);
            }
        };
    }
}