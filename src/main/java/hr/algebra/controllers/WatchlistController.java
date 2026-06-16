package hr.algebra.controllers;

import hr.algebra.models.Series;
import hr.algebra.models.User;
import hr.algebra.repository.IWatchlistRepository;
import hr.algebra.repository.RepositoryFactory;
import hr.algebra.utils.AlertUtil;
import hr.algebra.utils.SessionManager;
import hr.algebra.utils.XmlActivityLogger;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WatchlistController {

    @FXML private TableView<Series> tableWatchlist;
    @FXML private TableColumn<Series, String> colName;

    private static final Logger log = LoggerFactory.getLogger(WatchlistController.class);
    private final IWatchlistRepository watchlistRepo = RepositoryFactory.watchlist();
    private static final String ERROR_MESSAGE = "Error";

    @FXML
    public void initialize() {
        User user = SessionManager.getCurrentUser();
        if (user == null) {
            AlertUtil.showError(ERROR_MESSAGE, "No user logged in.");
            return;
        }

        if (user.isAdministrator()) {
            log.info("Access denied, administrators do not have a watchlist.");
            AlertUtil.showError("Access Denied", "Administrators do not have a watchlist.");
            tableWatchlist.setPlaceholder(new javafx.scene.control.Label("Access Denied"));
            return;
        }

        setupTableColumn();
        setupContextMenu();
        refreshTable();
    }

    private void setupContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem removeMenuItem = new MenuItem("Remove from watchlist");
        removeMenuItem.setOnAction(event -> {
            Series selected = tableWatchlist.getSelectionModel().getSelectedItem();
            if (selected != null) {
                User user = SessionManager.getCurrentUser();
                try {
                    watchlistRepo.remove(user.getId(), selected.getId());
                    refreshTable();
                    XmlActivityLogger.logActivity(user.getFullName(), "Removed series: " + selected.getName(), "INFO");
                    AlertUtil.showInfo("Removed", selected.getName() + " removed from watchlist.");
                } catch (Exception e) {
                    log.error("Failed while removing series from watchlist: {}", selected.getName(), e);
                    AlertUtil.showError(ERROR_MESSAGE, "Cannot remove series from watchlist.");
                }
            }
        });
        contextMenu.getItems().add(removeMenuItem);
        tableWatchlist.setContextMenu(contextMenu);
    }

    private void setupTableColumn() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
    }

    private void refreshTable() {
        try {
            if(SessionManager.getCurrentUser() == null){
                AlertUtil.showError(ERROR_MESSAGE, "No user logged in. Please log in to view your watchlist.");
                return;
            }
            int currentUserId = SessionManager.getCurrentUser().getId();
            tableWatchlist.setItems(FXCollections.observableArrayList(watchlistRepo.getAll(currentUserId)));
        } catch (Exception e) {
            AlertUtil.showError(ERROR_MESSAGE, "Cannot load watchlist from database.");
        }
    }
}
