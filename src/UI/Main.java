package UI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    static Stage screen;

    @Override
    public void start(Stage primaryStage) throws Exception {
        screen = primaryStage;
        Parent root = FXMLLoader.load(Main.class.getResource("sample.fxml"));
        screen.setTitle("Side scroller game");
        screen.setScene(new Scene(root, 1280, 720));
        screen.setResizable(false);
        screen.sizeToScene();
        screen.show();


    }

    public static void main(String[] args) {
        launch(args);
    }
}
