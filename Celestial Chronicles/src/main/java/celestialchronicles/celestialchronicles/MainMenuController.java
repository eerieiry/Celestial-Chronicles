package celestialchronicles.celestialchronicles;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainMenuController {

    //Variables for window resolution
    public static double screenWidth = 1920;
    public static double screenHeight = 1080;
    public static boolean fullScreenBool = false;

    //public Circle circle;

    private void showMainMenuScreen(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main-menu.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, screenWidth, screenHeight);
        stage.setScene(scene);
        stage.setFullScreen(fullScreenBool);
        stage.show();
    }

    @FXML
    private void playClicked(ActionEvent event) throws IOException {
    }

    @FXML
    private void exitClicked(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main-menu.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    //Settings
    @FXML
    private RadioButton fullscreenCheckBox;
    @FXML
    private ChoiceBox resolutionChoiceBox;

    @FXML
    private void settingsClicked(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("settings.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, screenWidth, screenHeight);
        stage.setScene(scene);
        stage.setFullScreen(fullScreenBool);
        stage.show();
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
        //stage.setX((screen.getBounds().getWidth() - screenWidth)/2);
        //stage.setY((screen.getBounds().getHeight() - screenHeight)/2);
    }

    @FXML
    public void backClicked(ActionEvent event) throws IOException {
        showMainMenuScreen(event);
    }



   /*public void circleClicked(MouseEvent mouseEvent) {
        Circle circle =(Circle)event.getSource();
        circle.setFill(Color.OLIVEDRAB);
    }*/
}