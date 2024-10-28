/*
 Philip Awini
 1169595
 ESOF-2570 
 */
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SpotOnGame extends Application {
	@Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("SpotOnGame.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(
        		getClass().getResource("SpotOnGame.css").toExternalForm
        		()); // dynamically calling css file from the main

        stage.setTitle("SpotOn Game");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args); // Start the JavaFX application
    }

}
