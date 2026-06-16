package hr.algebra.controllers;

import hr.algebra.models.Actor;
import hr.algebra.repository.IActorRepository;
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

public class ActorController {

    @FXML private TableView<Actor> tableActors;
    @FXML private TableColumn<Actor, String> colActorName;
    @FXML private TableColumn<Actor, String> colActorSurname;
    @FXML private TableColumn<Actor, Integer> colActorYear;
    @FXML private TableColumn<Actor, String> colActorNationality;

    private static final Logger log = LoggerFactory.getLogger(ActorController.class);
    private static final String ERROR_MESSAGE = "Error";
    private final IActorRepository actorRepo = RepositoryFactory.actors();

    @FXML
    public void initialize() {
        setupColumns();
        loadActors();
    }

    public void loadActors() {
        try {
            tableActors.setItems(FXCollections.observableArrayList(actorRepo.getAll()));
            tableActors.refresh();
        }catch (Exception e){
            log.error("Failed to load actors.", e);
            AlertUtil.showError(ERROR_MESSAGE, "Failed to load actors.");
        }

    }

    private void setupColumns() {
        colActorName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colActorSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        colActorYear.setCellValueFactory(new PropertyValueFactory<>("yearOfBirth"));
        colActorNationality.setCellValueFactory(new PropertyValueFactory<>("nationality"));
    }

    public void onAddActor() {
        try {
            SceneUtil.openModal(SceneUtil.ACTOR_FORM, "New actor");
            loadActors();
        }catch (Exception e){
            log.error("Failed to open add actor form.", e);
            AlertUtil.showError(ERROR_MESSAGE, "Failed to open add actor form.");
        }
    }

    public void onEditActor() {
        try {
            List<Actor> allActors = actorRepo.getAll();
            if (allActors == null || allActors.isEmpty()) {
                AlertUtil.showInfo("Message", "No actors found in database.");
                return;
            }
            ChoiceDialogService.chooseActor(allActors, "Edit Actor", "Select an actor to edit:")
                    .ifPresent(actor -> {
                        FormDialogService.openActorEditForm(actor);
                        loadActors();
                    });
        } catch (Exception e) {
            log.error("Failed to load actors for edit.", e);
            AlertUtil.showError(ERROR_MESSAGE, "Failed to load actors for edit.");
        }
    }

    public void onDeleteActor() {
        try {
            List<Actor> allActors = actorRepo.getAll();
            if (allActors.isEmpty()) {
                AlertUtil.showInfo("Message", "No actors found in database.");
                return;
            }

            ChoiceDialogService.chooseActor(allActors, "Delete Actor", "Choose an actor to delete:")
                    .ifPresent(actor -> {
                        if (AlertUtil.showDeleteConfirm(actor.getFullName())) {
                            try {
                                actorRepo.deleteById(actor.getId());
                                XmlActivityLogger.logActivity(SessionManager.getCurrentUser().getFullName(), "Deleted Actor: " + actor.getFullName(), "WARN");
                                loadActors();
                                AlertUtil.showInfo("Success", "Actor deleted.");
                            } catch (Exception e) {
                                log.error("Database error while trying to delete actor: {}", actor.getFullName(), e);
                                AlertUtil.showError(ERROR_MESSAGE, "Cannot delete actor.");
                            }
                        }
                    });
        } catch (Exception e) {
            log.error("Failed to delete the actor.",e);
            AlertUtil.showError(ERROR_MESSAGE, "Failed to delete the actor.");
        }
    }
}