package celestialchronicles.celestialchronicles;

import eu.hansolo.tilesfx.addons.Switch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import java.util.prefs.Preferences;
import java.io.IOException;
import java.util.EventObject;
import java.util.Objects;
import javafx.geometry.Rectangle2D;

public class MainMenuController {

    //Variables for window resolution
    public static double screenWidth = 1920;
    public static double screenHeight = 1080;
    //public Circle circle;

    private void showMainMenuScreen(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main-menu.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, screenWidth, screenHeight);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void newGameClicked(ActionEvent event) throws IOException {
    }

    @FXML
    private void settingsClicked(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("settings.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, screenWidth, screenHeight);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    private void closeWindow(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("exit-confirmation.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, screenWidth, screenHeight);
        stage.setScene(scene);
        stage.show();

    }

    @FXML
    private void exitClicked(ActionEvent event) throws IOException {
        closeWindow(event);
    }

    //Settings
    @FXML
    private CheckBox fullscreenCheckBox;
    public ChoiceBox resolutionChoiceBox;

    @FXML
    private void applySettings(ActionEvent event) {
    }
    @FXML
    public void applyClicked(ActionEvent event) throws IOException {
        String selectedSize = (String) resolutionChoiceBox.getValue();
        switch (selectedSize) {
            case "800x600":
                screenWidth = 800;
                screenHeight = 600;
                break;

            case "1024x768":
                screenWidth = 1024;
                screenHeight = 768;
                break;

            case "1280x720":
                screenWidth = 1280;
                screenHeight = 720;
                break;

            case "1920x1080":
                screenWidth = 1920;
                screenHeight = 1080;
                break;
        }

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("settings.fxml")));
        Screen screen = Screen.getPrimary();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, screenWidth, screenHeight);
        stage.setX((screen.getBounds().getWidth() - screenWidth)/2);
        stage.setY((screen.getBounds().getHeight() - screenHeight)/2);
        stage.setScene(scene);
        stage.show();

        Button btn = (Button)event.getSource();
        btn.setText("Text");
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