package celestialchronicles.celestialchronicles;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;
import static celestialchronicles.celestialchronicles.AudioManager.playAudioAndLoadNextScene;



public class GameMenu {

    public void guidebookClicked(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("guidebook-constellations.fxml")));
        playAudioAndLoadNextScene(actionEvent, root);
    }
    public void level1Clicked(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("guidebook-constellations.fxml")));
        playAudioAndLoadNextScene(actionEvent, root);
    }

    public void level2Clicked(ActionEvent actionEvent) {
    }

    public void level3Clicked(ActionEvent actionEvent) {
    }

    public void level4Clicked(ActionEvent actionEvent) {
    }

    public void level5Clicked(ActionEvent actionEvent) {
    }

    public void menuClicked(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main-menu.fxml")));
        playAudioAndLoadNextScene(actionEvent, root);
    }
}
