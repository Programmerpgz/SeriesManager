package hr.algebra.services;

import hr.algebra.controllers.ActorFormController;
import hr.algebra.controllers.DirectorFormController;
import hr.algebra.controllers.SeriesFormController;
import hr.algebra.models.Actor;
import hr.algebra.models.Director;
import hr.algebra.models.Series;
import hr.algebra.models.User;
import hr.algebra.utils.AlertUtil;
import hr.algebra.utils.SceneUtil;
import hr.algebra.utils.SessionManager;
import hr.algebra.utils.XmlActivityLogger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public final class FormDialogService {
    private static final String ERROR_MESSAGE = "Error";
    private static final String MESSAGE = "Could not open the requested form layout.";
    private FormDialogService() {}

    public static void openActorEditForm(Actor actor) {
        if (actor == null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(FormDialogService.class.getResource(SceneUtil.ACTOR_FORM));
            Parent root = loader.load();

            ActorFormController controller = loader.getController();
            controller.setActorForEdit(actor);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Edit Actor: " + actor.getFullName());
            stage.setResizable(false);
            stage.showAndWait();

        } catch (IOException e) {
            AlertUtil.showError(ERROR_MESSAGE, MESSAGE);
        }
    }

    public static void openDirectorEditForm(Director director) {
        if (director == null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(FormDialogService.class.getResource(SceneUtil.DIRECTOR_FORM));
            Parent root = loader.load();

            DirectorFormController controller = loader.getController();
            controller.setDirectorForEdit(director);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Edit Director: " + director.getFullName());
            stage.setResizable(false);
            stage.showAndWait();

        } catch (IOException e) {
            AlertUtil.showError(ERROR_MESSAGE, MESSAGE);
        }
    }

    public static void openSeriesEditForm(Series series) {
        if (series == null){
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(FormDialogService.class.getResource(SceneUtil.SERIES_FORM));
            Parent root = loader.load();

            SeriesFormController controller = loader.getController();
            controller.setSeriesForEdit(series);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Edit Series: ");
            stage.setResizable(false);
            stage.showAndWait();

            User currentUser = SessionManager.getCurrentUser();
            XmlActivityLogger.logActivity(currentUser.getFullName(), "Edited series: " + series.getName(), "INFO");

        } catch (IOException e) {
            AlertUtil.showError(ERROR_MESSAGE, MESSAGE);
        }
    }
}
