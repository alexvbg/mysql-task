package filehostingservice.presentation;

import filehostingservice.persistence.ConnectionManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class TaskApplication extends Application  {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/task.fxml"));
        primaryStage.setTitle("MtSQL task");
        primaryStage.getIcons().add(new Image("/db.png"));
        primaryStage.setScene(new Scene(root));
        primaryStage.sizeToScene();
        primaryStage.setResizable(false);
        primaryStage.show();
        ///////////////////
        primaryStage.setOnCloseRequest(event -> ConnectionManager.closeConnection());
    }

    public static void main(String[] args) {
        launch();
    }
}
