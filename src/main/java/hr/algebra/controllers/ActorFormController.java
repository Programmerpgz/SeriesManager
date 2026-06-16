package hr.algebra.controllers;

import hr.algebra.models.Actor;
import hr.algebra.repository.IActorRepository;
import hr.algebra.repository.RepositoryFactory;
import hr.algebra.utils.AlertUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActorFormController {

    @FXML private Label     lblTitle;
    @FXML private TextField txtName;
    @FXML private TextField txtSurname;
    @FXML private TextField txtYearOfBirth;
    @FXML private TextField txtNationality;
    private static final Logger log = LoggerFactory.getLogger(ActorFormController.class);
    private Actor actorForEdit = null;
    private final IActorRepository actorRepo = RepositoryFactory.actors();
    private static final String ERROR_MESSAGE = "Error";

    public void setActorForEdit(Actor actor) {
        this.actorForEdit = actor;

        if (actor != null) {
            lblTitle.setText("Edit actor");
            txtName.setText(actor.getName());
            txtSurname.setText(actor.getSurname());
            txtYearOfBirth.setText(String.valueOf(actor.getYearOfBirth()));
            txtNationality.setText(actor.getNationality());
            log.info("Edit form opened for actor: {}", actor.getFullName());
        }
    }

    @FXML
    public void initialize() {
        lblTitle.setText("Add new actor");
    }

    @FXML
    private void onSave() {
        String name = txtName.getText().trim();
        String surname = txtSurname.getText().trim();
        String yearText = txtYearOfBirth.getText().trim();
        String nationality = txtNationality.getText().trim();

        if(name.isEmpty() || surname.isEmpty() || yearText.isEmpty()) {
            AlertUtil.showError(ERROR_MESSAGE,"All fields except nationality cannot be empty!");
            return;
        }

        try {
            int yearOfBirth = Integer.parseInt(yearText);
            if(yearOfBirth < 1900 || yearOfBirth > 2026){
                AlertUtil.showError(ERROR_MESSAGE,"Year of birth must be between 1900 and 2026!");
                return;
            }

            String finalNationality = nationality.isEmpty() ? "Unknown" : nationality;

            if (actorForEdit == null) {
                Actor newActor = new Actor(name, surname, yearOfBirth, finalNationality);
                actorRepo.insert(newActor);
                log.info("Successfully inserted new actor: {}", newActor.getFullName());
            } else {
                actorForEdit.setName(name);
                actorForEdit.setSurname(surname);
                actorForEdit.setYearOfBirth(yearOfBirth);
                actorForEdit.setNationality(finalNationality);
                actorRepo.update(actorForEdit);
                log.info("Successfully updated actor: {}", actorForEdit.getFullName());
            }
            closeWindow();
        } catch (NumberFormatException e) {
            log.error("Year of birth must be a valid number",e);
            AlertUtil.showError(ERROR_MESSAGE,"Year of birth must be a valid number");
        } catch (Exception e) {
            log.error("Error inserting new actor",e);
            AlertUtil.showError(ERROR_MESSAGE,"Error saving actor");
        }
    }

    @FXML private void onCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) txtName.getScene().getWindow();
        stage.close();
    }

}