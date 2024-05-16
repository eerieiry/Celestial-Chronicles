package celestialchronicles.celestialchronicles;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.ResourceBundle;

import static celestialchronicles.celestialchronicles.AudioManager.playAudioAndLoadNextScene;

public class MainMenuController {

    //Variables for window resolution
    public static double screenWidth = 1920;
    public static double screenHeight = 1080;
    public static boolean fullScreenBool = false;




    public void showMainMenuScreen(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main-menu.fxml")));
        playAudioAndLoadNextScene(event, root);
    }

    @FXML
    private void playClicked(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("game-menu.fxml")));
        playAudioAndLoadNextScene(event, root);
    }

    @FXML
    private void exitClicked(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main-menu.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    @FXML
    private ChoiceBox resolutionChoiceBox;

    @FXML
    private void settingsClicked(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("settings.fxml")));
        playAudioAndLoadNextScene(event, root);
    }

    @FXML
    public void applyClicked(ActionEvent event) throws IOException {

        String selectedSize = (String) resolutionChoiceBox.getValue();
        switch (selectedSize) {
            case "800x600":

                screenWidth = 800;
                screenHeight = 600;
                fullScreenBool = false;
                break;

            case "1024x768":
                screenWidth = 1024;
                screenHeight = 768;
                fullScreenBool = false;
                break;

            case "1280x720":
                screenWidth = 1280;
                screenHeight = 720;
                fullScreenBool = false;
                break;

            case "1920x1080":
                screenWidth = 1920;
                screenHeight = 1080;
                fullScreenBool = false;
                break;

            case "Full Screen":
                fullScreenBool = true;
                break;
            default: System.out.println("Invalid resolution size");
        }
        settingsClicked(event);
    }

    @FXML
    public void backClicked(ActionEvent event) throws IOException {
     showMainMenuScreen(event);
    }
}
