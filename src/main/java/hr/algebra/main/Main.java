package hr.algebra.main;

import hr.algebra.utils.AppConfig;
import hr.algebra.utils.DataSourceSingleton;
import hr.algebra.utils.SceneUtil;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        SceneUtil.setPrimaryStage(primaryStage);
        AppConfig config = DataSourceSingleton.getAppConfig();

        if(config != null){
            primaryStage.setWidth(config.getScreenWidth());
            primaryStage.setHeight(config.getScreenHeight());
            primaryStage.setMinWidth(config.getScreenWidth());
            primaryStage.setMinHeight(config.getScreenHeight());
        }

        primaryStage.setTitle("Series Manager");
        primaryStage.setResizable(true);
        SceneUtil.switchScene(SceneUtil.LOGIN_VIEW, "Login");
    }

    @Override
    public void stop() {
        DataSourceSingleton.closeConnection();
    }
}