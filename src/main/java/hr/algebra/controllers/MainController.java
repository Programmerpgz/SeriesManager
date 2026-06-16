package hr.algebra.controllers;

import hr.algebra.models.User;
import hr.algebra.utils.AlertUtil;
import hr.algebra.utils.SceneUtil;
import hr.algebra.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import static hr.algebra.utils.SessionManager.getCurrentUser;

public class MainController {

    @FXML private TextField txtSearch;
    @FXML private Label lblUser;
    @FXML private MenuItem menuEditSeries;
    @FXML private MenuItem menuDeleteSeries;
    @FXML private Button btnMyWatchlist;
    @FXML private Menu menuActors;
    @FXML private Menu menuDirectors;
    @FXML private MenuItem menuAddSeries;
    @FXML private MenuItem menuBackupAll;
    @FXML private MenuItem menuExportSeries;
    @FXML private VBox seriesView;
    @FXML private VBox actorsView;
    @FXML private VBox directorsView;
    @FXML private SeriesController seriesViewController;
    @FXML private ActorController actorsViewController;
    @FXML private DirectorController directorsViewController;
    private static final String ERROR_MESSAGE = "Error";

    @FXML
    public void initialize() {
        User user = getCurrentUser();
        if (user == null) {
            SceneUtil.switchScene(SceneUtil.LOGIN_VIEW, "Login");
            return;
        }
        lblUser.setText(user.getFullName() + " (" + user.getRole() + ")");
        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> seriesViewController.onSearch(newValue.trim()));
        applySecurityPermissions();
    }

    @FXML
    private void onAllSeries() {
        seriesViewController.onAllSeries();
    }

    @FXML
    private void onAddSeries() {
        seriesViewController.onAddSeries();
    }

    @FXML
    private void onEditSeries(){
        seriesViewController.onEditSeries();
    }

    @FXML
    private void onDeleteSeries() {
        seriesViewController.onDeleteSeries();
    }

    @FXML
    private void onFilterByPlatform() {
        seriesViewController.onFilterByPlatform();
    }

    @FXML
    private void onFilterByGenre() {
        seriesViewController.onFilterByGenre();
    }

    @FXML
    private void onFilterStillEmitting(){
        seriesViewController.onFilterStillEmitting();
    }

    @FXML
    private void onExportToXml(){
        seriesViewController.onExportToXml();
    }

    @FXML
    private void onBackupAll() {
        seriesViewController.onBackupAll();
    }

    @FXML
    private void onAddActor(){
        actorsViewController.onAddActor();
    }

    @FXML
    private void onEditActor(){
        actorsViewController.onEditActor();
    }

    @FXML
    private void onDeleteActor() {
        actorsViewController.onDeleteActor();
    }

    @FXML
    private void onAddDirector() {
        directorsViewController.onAddDirector();
    }

    @FXML
    private void onEditDirector() {
        directorsViewController.onEditDirector();
    }

    @FXML
    private void onDeleteDirector() {
        directorsViewController.onDeleteDirector();
    }

    @FXML
    private void onOpenWatchlist() {
        User currentUser = getCurrentUser();
        if (currentUser != null && currentUser.isAdministrator()) {
            AlertUtil.showError(ERROR_MESSAGE, "Administrators cannot access the watchlist.");
            return;
        }
        SceneUtil.openModal(SceneUtil.WATCHLIST_VIEW, "My Watchlist");
    }

    @FXML
    private void onLogout() {
        SessionManager.logout();
        SceneUtil.switchScene(SceneUtil.LOGIN_VIEW, "Login");
    }

    private void applySecurityPermissions() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return;
        }

        boolean isAdmin = currentUser.isAdministrator();

        setControlVisibility(btnMyWatchlist, !isAdmin);
        setMenuVisibility(menuActors, isAdmin);
        setMenuVisibility(menuDirectors, isAdmin);
        setMenuItemVisibility(menuAddSeries, isAdmin);
        setMenuItemVisibility(menuEditSeries, isAdmin);
        setMenuItemVisibility(menuDeleteSeries, isAdmin);
        setMenuItemVisibility(menuBackupAll, isAdmin);
        setMenuItemVisibility(menuExportSeries, isAdmin);
    }
    private void setControlVisibility(Node node, boolean visible) {
        if (node != null) {
            node.setVisible(visible);
            node.setManaged(visible);
        }
    }
    private void setMenuVisibility(Menu menu, boolean visible) {
        if (menu != null) {
            menu.setVisible(visible);
        }
    }
    private void setMenuItemVisibility(MenuItem item, boolean visible) {
        if (item != null){
            item.setVisible(visible);
        }
    }
}