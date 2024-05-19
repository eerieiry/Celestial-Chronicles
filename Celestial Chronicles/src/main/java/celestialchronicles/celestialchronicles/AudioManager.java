package celestialchronicles.celestialchronicles;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

import static celestialchronicles.celestialchronicles.MainMenuController.*;

public class AudioManager {
    public static boolean mute = false;

    private static final Media starClick = new Media(Objects.requireNonNull(MainMenuController.class.getResource("star-click.mp3")).toString());
    private static final Media success = new Media(Objects.requireNonNull(MainMenuController.class.getResource("success.mp3")).toString());
    private static final Media error = new Media(Objects.requireNonNull(MainMenuController.class.getResource("error.mp3")).toString());
    private static final Media sound = new Media(Objects.requireNonNull(MainMenuController.class.getResource("click.mp3")).toString());

    private static final MediaPlayer starClickMediaPlayer = new MediaPlayer(starClick);
    private static final MediaPlayer successMediaPlayer = new MediaPlayer(success);
    private static final MediaPlayer errorMediaPlayer = new MediaPlayer(error);
    private static final MediaPlayer mediaPlayer = new MediaPlayer(sound);

    public static MediaPlayer getStarClickMediaPlayer() {
        return starClickMediaPlayer;
    }

    public static MediaPlayer getSuccessMediaPlayer() {
        return successMediaPlayer;
    }

    public static MediaPlayer getErrorMediaPlayer() {
        return errorMediaPlayer;
    }

    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public static void playAudioAndLoadNextScene(ActionEvent event, Parent root) throws IOException {
        Media sound = new Media(Objects.requireNonNull(MainMenuController.class.getResource("click.mp3")).toString());
        if (!mute) {
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.setOnEndOfMedia(() -> {
                try {
                    loadNextScene(event, root);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            mediaPlayer.play();
        } else {
            try {
                loadNextScene(event, root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadNextScene(ActionEvent event, Parent root) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, screenWidth, screenHeight);
        stage.setScene(scene);
        stage.setFullScreen(fullScreenBool);
        stage.show();
    }
}
