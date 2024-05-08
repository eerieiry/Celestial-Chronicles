package celestialchronicles.celestialchronicles;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.stage.Screen;
import javafx.scene.image.Image;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class MainMenu extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main-menu.fxml")));
        Screen screen = Screen.getPrimary();
        double screenWidth = 1920;
        double screenHeight = 1080;
        Scene scene = new Scene(root, screenWidth, screenHeight);

        stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/celestialchronicles/celestialchronicles/styles/images/icon.png")));

        stage.setTitle("Celestial Chronicles");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}