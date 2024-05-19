package celestialchronicles.celestialchronicles;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import static celestialchronicles.celestialchronicles.AudioManager.playAudioAndLoadNextScene;
import static celestialchronicles.celestialchronicles.Database.*;


public class GameMenu {
    public Label lvl1BestTime;
    public Label lvl1BestScore;
    public Label lvl2BestTime;
    public Label lvl2BestScore;
    public Label lvl3BestTime;
    public Label lvl3BestScore;
    public Label lvl4BestTime;
    public Label lvl4BestScore;
    public Label lvl5BestTime;
    public Label lvl5BestScore;

    private LevelDAO levelDAO = new LevelDAO();

    public Button level1Button;
    public Button level2Button;
    public Button level3Button;
    public Button level4Button;
    public Button level5Button;
    double bestScore, bestTime;

    @FXML
    public void initialize() {
        try {
            checkLevelUnlocks();
            updateLevelInfo();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateLevelInfo() throws SQLException {
        for (int i = 1; i <= 5; i++) {
            bestTime = getBestTimeFromDatabase(i);
            bestScore = getBestScoreFromDatabase(i);

            if (i == 1 && bestTime != 0) {
                lvl1BestTime.setText("Best time: " + String.format("%.0f:%02.0f", bestTime / 60, bestTime % 60));
                lvl1BestScore.setText("Best score: " + bestScore);
            }

            if (i == 2 && bestTime != 0) {
                lvl2BestTime.setText("Best time: " + String.format("%.0f:%02.0f", bestTime / 60, bestTime % 60));
                lvl2BestScore.setText("Best score: " + bestScore);
            }

            if (i == 3 && bestTime != 0) {
                lvl3BestTime.setText("Best time: " + String.format("%.0f:%02.0f", bestTime / 60, bestTime % 60));
                lvl3BestScore.setText("Best score: " + bestScore);
            }

            if (i == 4 && bestTime != 0) {
                lvl4BestTime.setText("Best time: " + String.format("%.0f:%02.0f", bestTime / 60, bestTime % 60));
                lvl4BestScore.setText("Best score: " + bestScore);
            }

            if (i == 5 && bestTime != 0) {
                lvl5BestTime.setText("Best time: " + String.format("%.0f:%02.0f", bestTime / 60, bestTime % 60));
                lvl5BestScore.setText("Best score: " + bestScore);
            }

        }

    }

    private double getBestTimeFromDatabase(int NumLevel) throws SQLException {
        double bestTime = 0;

        String query = "SELECT best_time FROM levels WHERE level = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, NumLevel);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                bestTime = resultSet.getDouble("best_time");
            }
        }

        return bestTime;
    }

    private double getBestScoreFromDatabase(int NumLevel) throws SQLException {
        double bestScore = 0;

        String query = "SELECT best_score FROM levels WHERE level = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, NumLevel);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                bestScore = resultSet.getDouble("best_score");
            }
        }

        return bestScore;
    }

    private void checkLevelUnlocks() throws SQLException {
        for (int i = 1; i <= 5; i++) {
            levelDAO.getLevel(i);
            Button levelButton = getLevelButton(i);
            if (levelButton != null) {
                levelButton.setDisable(!levelDAO.unlocked);
                if (!levelDAO.unlocked) {
                    levelButton.getStyleClass().add("locked");
                } else {
                    levelButton.getStyleClass().removeAll("locked");
                }
            }
        }
    }

    private Button getLevelButton(int levelNumber) {
        switch (levelNumber) {
            case 1:
                return level1Button;
            case 2:
                return level2Button;
            case 3:
                return level3Button;
            case 4:
                return level4Button;
            case 5:
                return level5Button;
            default:
                return null;
        }
    }

    public void guidebookClicked(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("guidebook-constellations.fxml")));
        playAudioAndLoadNextScene(actionEvent, root);
    }

    public void level1Clicked(ActionEvent actionEvent) throws IOException {
        Level.LEVEL = 1;
        if (bestScore == 0) {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("tutorial.fxml")));
            playAudioAndLoadNextScene(actionEvent, root);
        } else {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("level.fxml")));
            playAudioAndLoadNextScene(actionEvent, root);
        }
    }

    public void level2Clicked(ActionEvent actionEvent) throws IOException {
        Level.LEVEL = 2;
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("level.fxml")));
        playAudioAndLoadNextScene(actionEvent, root);
    }

    public void level3Clicked(ActionEvent actionEvent) throws IOException {
        Level.LEVEL = 3;
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("level.fxml")));
        playAudioAndLoadNextScene(actionEvent, root);
    }

    public void level4Clicked(ActionEvent actionEvent) throws IOException {
        Level.LEVEL = 4;
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("level.fxml")));
        playAudioAndLoadNextScene(actionEvent, root);
    }

    public void level5Clicked(ActionEvent actionEvent) throws IOException {
        Level.LEVEL = 5;
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("level.fxml")));
        playAudioAndLoadNextScene(actionEvent, root);
    }

    public void menuClicked(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main-menu.fxml")));
        playAudioAndLoadNextScene(actionEvent, root);
    }

    public void clearAllLevels(ActionEvent actionEvent) throws SQLException, IOException {
        try {
            clearLevelsInDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("game-menu.fxml")));
        playAudioAndLoadNextScene(actionEvent, root);
    }

    private void clearLevelsInDatabase() throws SQLException {
        String query = "UPDATE levels SET best_time = 0, best_score = 0, unlocked = CASE WHEN level = 1 THEN TRUE ELSE FALSE END";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        }
    }

}
