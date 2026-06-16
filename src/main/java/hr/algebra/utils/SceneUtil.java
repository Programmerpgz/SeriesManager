package hr.algebra.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.URL;

public final class SceneUtil {
    private static Stage primaryStage;
    private static final Logger log = LoggerFactory.getLogger(SceneUtil.class);
    private SceneUtil() {}
    public static final String LOGIN_VIEW    = "/hr/algebra/views/LoginView.fxml";
    public static final String REGISTER_VIEW = "/hr/algebra/views/RegisterView.fxml";
    public static final String MAIN_VIEW     = "/hr/algebra/views/MainView.fxml";
    public static final String SERIES_FORM   = "/hr/algebra/views/SeriesFormView.fxml";
    public static final String ACTOR_FORM    = "/hr/algebra/views/ActorFormView.fxml";
    public static final String DIRECTOR_FORM = "/hr/algebra/views/DirectorFormView.fxml";
    public static final String ADMIN_DASHBOARD = "/hr/algebra/views/AdminDashboardView.fxml";
    public static final String WATCHLIST_VIEW = "/hr/algebra/views/WatchlistView.fxml";

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchScene(String fxmlPath, String title) {
        log.info("Switching scene to: {} ['{}']", fxmlPath, title);
        try {
            URL resource = SceneUtil.class.getResource(fxmlPath);
            if (resource == null) {
                log.error("Resource error: Cannot find FXML layout file at path: {}", fxmlPath);
                AlertUtil.showError("Resource Error", "UI Error: Cannot find FXML layout file at path: " + fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            if (primaryStage == null) {
                log.error("Application error: Primary stage is not initialized.");
                AlertUtil.showError("Application Error", "Primary stage is not initialized.");
                return;
            }
            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Series Manager — " + title);
            primaryStage.show();
            log.info("Successfully switched scene to: {}", title);
        } catch (IOException e) {
            log.error("Error loading FXML: {}", fxmlPath, e);
            AlertUtil.showError("UI Error", "Cannot open view: " + e);
        } catch (Exception e) {
            log.error("Unexpected error while switching scene to {}", fxmlPath, e);
            AlertUtil.showError("Unexpected Error", "An unexpected error occurred: " + e);
        }
    }
    public static FXMLLoader openModal(String fxmlPath, String title) {
        URL resource = SceneUtil.class.getResource(fxmlPath);
        if (resource == null) {
            AlertUtil.showError("Resource Error", "Cannot find FXML: " + fxmlPath);
            return null;
        }

        try {
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            Stage modal = new Stage();
            modal.setTitle(title);
            Scene scene = new Scene(root);
            modal.setScene(scene);

            modal.initModality(Modality.APPLICATION_MODAL);
            if (primaryStage != null) {
                modal.initOwner(primaryStage);
            }

            modal.showAndWait();
            return loader;
        }catch (IOException e){
            log.error("Failed while opening modal window: {}", fxmlPath, e);
            AlertUtil.showError("Error", "Cannot open the window: " + e.getMessage());
            return null;
        }
    }
}
