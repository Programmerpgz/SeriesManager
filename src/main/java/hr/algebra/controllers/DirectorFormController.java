package hr.algebra.controllers;

import hr.algebra.models.Director;
import hr.algebra.repository.IDirectorRepository;
import hr.algebra.repository.RepositoryFactory;
import hr.algebra.utils.AlertUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectorFormController{

    @FXML private Label lblTitle;
    @FXML private TextField txtName;
    @FXML private TextField txtSurname;
    @FXML private TextField txtYearOfBirth;
    @FXML private TextField txtNationality;
    @FXML private TextArea  txtBiography;
    private static final Logger log = LoggerFactory.getLogger(DirectorFormController.class);
    private Director directorForEdit = null;
    private final IDirectorRepository directorRepo = RepositoryFactory.directors();
    private static final String ERROR_MESSAGE = "Error";

    @FXML
    public void initialize() {
        lblTitle.setText("Add new director");
    }

    public void setDirectorForEdit(Director director) {
        this.directorForEdit = director;
        if (director != null) {
            lblTitle.setText("Edit director");
            txtName.setText(director.getName());
            txtSurname.setText(director.getSurname());
            txtYearOfBirth.setText(String.valueOf(director.getYearOfBirth()));
            txtNationality.setText(director.getNationality() != null ? director.getNationality() : "");
            txtBiography.setText(director.getBiography() != null ? director.getBiography() : "");
            log.info("Director form opened in EDIT mode for director: {}", director.getFullName());
        }
    }

    @FXML
    private void onSave() {
        String name = txtName.getText().trim();
        String surname = txtSurname.getText().trim();
        String yearText = txtYearOfBirth.getText().trim();
        String nationality = txtNationality.getText().trim();
        String biography = txtBiography.getText().trim();

        if(name.isEmpty() || surname.isEmpty() || yearText.isEmpty()){
            AlertUtil.showError(ERROR_MESSAGE, "Name, surname and year of birth cannot be empty!");
            return;
        }

        try{
            int yearOfBirth = Integer.parseInt(yearText);
            if(yearOfBirth < 1900 || yearOfBirth > 2026){
                AlertUtil.showError(ERROR_MESSAGE, "Year of birth must be between 1900 and 2026!");
                return;
            }

            String finalNationality = nationality.isEmpty() ? "Unknown" : nationality;
            String finalBiography = biography.isEmpty() ? "No biography available." : biography;

            if (directorForEdit == null) {
                Director newDir = new Director(name, surname, yearOfBirth, finalNationality, finalBiography);
                directorRepo.insert(newDir);
                log.info("Successfully inserted new director: {}", newDir.getFullName());
            } else {
                directorForEdit.setName(name);
                directorForEdit.setSurname(surname);
                directorForEdit.setYearOfBirth(yearOfBirth);
                directorForEdit.setNationality(finalNationality);
                directorForEdit.setBiography(finalBiography);
                directorRepo.update(directorForEdit);
                log.info("Successfully updated director: {}", directorForEdit.getFullName());
            }
            closeWindow();
        } catch (NumberFormatException e) {
            log.warn("User entered invalid year of birth format: '{}'", txtYearOfBirth.getText(),e);
            AlertUtil.showError(ERROR_MESSAGE, "Year of birth must be a valid number");
        }catch (Exception e) {
            log.error("Failed while saving director data!", e);
            AlertUtil.showError(ERROR_MESSAGE, "Failed while saving director data!");
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