package celestialchronicles.celestialchronicles;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.Objects;

import static celestialchronicles.celestialchronicles.AudioManager.playAudioAndLoadNextScene;

public class Tutorial {

    public void level1Clicked(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("level.fxml")));
        playAudioAndLoadNextScene(actionEvent, root);
    }
}