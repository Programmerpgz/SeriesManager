package hr.algebra.controllers;

import hr.algebra.models.Director;
import hr.algebra.repository.IDirectorRepository;
import hr.algebra.repository.RepositoryFactory;
import hr.algebra.services.ChoiceDialogService;
import hr.algebra.services.FormDialogService;
import hr.algebra.utils.AlertUtil;
import hr.algebra.utils.SceneUtil;
import hr.algebra.utils.SessionManager;
import hr.algebra.utils.XmlActivityLogger;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DirectorController {

    @FXML private TableView<Director> tableDirectors;
    @FXML private TableColumn<Director, String> colDirectorName;
    @FXML private TableColumn<Director, String> colDirectorSurname;
    @FXML private TableColumn<Director, Integer> colDirectorYear;
    @FXML private TableColumn<Director, String> colDirectorNationality;
    @FXML private TableColumn<Director, String> colDirectorBiography;

    private static final Logger log = LoggerFactory.getLogger(DirectorController.class);
    private static final String ERROR_MESSAGE = "Error";
    private final IDirectorRepository directorRepo = RepositoryFactory.directors();

    @FXML
    public void initialize() {
        setupColumns();
        loadDirectors();
    }

    public void loadDirectors() {
        try {
            tableDirectors.setItems(FXCollections.observableArrayList(directorRepo.getAll()));
            tableDirectors.refresh();
        }catch (Exception e){
            log.error("Failed to load directors", e);
            AlertUtil.showError(ERROR_MESSAGE, "Failed to load directors.");
        }
    }

    private void setupColumns() {
        colDirectorName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDirectorSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        colDirectorYear.setCellValueFactory(new PropertyValueFactory<>("yearOfBirth"));
        colDirectorNationality.setCellValueFactory(new PropertyValueFactory<>("nationality"));
        colDirectorBiography.setCellValueFactory(new PropertyValueFactory<>("biography"));
    }

    public void onAddDirector() {
        try {
            SceneUtil.openModal(SceneUtil.DIRECTOR_FORM, "New director");
            loadDirectors();
        }catch (Exception e) {
            log.error("Failed to open add director form.",e);
            AlertUtil.showError(ERROR_MESSAGE, "Failed to open add director form.");
        }
    }

    public void onEditDirector() {
        try {
            List<Director> directors = directorRepo.getAll();
            if (directors.isEmpty()) {
                AlertUtil.showWarning("Message", "No directors found in database.");
                return;
            }
            ChoiceDialogService.chooseDirector(directors, "Edit Director", "Select a director to edit")
                    .ifPresent(director -> {
                        FormDialogService.openDirectorEditForm(director);
                        loadDirectors();
                    });
        } catch (Exception e) {
            log.error("Failed to load directors for edit.",e);
            AlertUtil.showError(ERROR_MESSAGE, "Failed to load directors for edit.");
        }
    }

    public void onDeleteDirector() {
        try {
            List<Director> allDirectors = directorRepo.getAll();
            if (allDirectors.isEmpty()) {
                AlertUtil.showInfo("Info", "No directors available in the database.");
                return;
            }
            ChoiceDialogService.chooseDirector(allDirectors, "Delete Director", "Select a director to delete:")
                    .ifPresent(selectedDirector -> {
                        if (AlertUtil.showDeleteConfirm(selectedDirector.getFullName())) {
                            try {
                                directorRepo.deleteById(selectedDirector.getId());
                                loadDirectors();
                                XmlActivityLogger.logActivity(SessionManager.getCurrentUser().getFullName(), "Deleted Director: " + selectedDirector.getFullName(), "WARN");
                                AlertUtil.showInfo("Success", "Director " + selectedDirector.getFullName() + " has been deleted.");
                            } catch (Exception e) {
                                log.error("Failed to delete director", e);
                                AlertUtil.showError(ERROR_MESSAGE, "Failed to delete director.");
                            }
                        }
                    });
        } catch (Exception e) {
            log.error("Failed to delete director", e);
            AlertUtil.showError(ERROR_MESSAGE, "Failed to delete director.");
        }
    }
}