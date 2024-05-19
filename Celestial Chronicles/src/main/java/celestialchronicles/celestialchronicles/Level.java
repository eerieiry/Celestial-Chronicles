package celestialchronicles.celestialchronicles;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import static celestialchronicles.celestialchronicles.AudioManager.*;
import static celestialchronicles.celestialchronicles.Database.*;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class Level {

    public static int LEVEL;

    public AnchorPane container;
    private final Set<Line> drawnLines = new HashSet<>();
    public Label timerLabel;
    private int winningLineCount = 0;
    private int MainStars = 0;
    private double[] initialCircleLayoutX = new double[MainStars];
    private double[] initialCircleLayoutY = new double[MainStars];
    private double[][] requiredLines;
    private List<String> constellationNames = new ArrayList<>();
    private double points = 0;
    private Timeline timer;
    private double elapsedTimeSeconds;
    @FXML
    private Label name;

    public void initialize() throws SQLException, IOException {
        connectToDatabase();
        constellationNames = getConstellationNames(LEVEL);
        loadNextConstellation();
        startTimer();

    }

    private int currentConstellationIndex = 0;

    private void loadNextConstellation() throws SQLException, IOException {
        if (currentConstellationIndex < constellationNames.size()) {
            String constellationName = constellationNames.get(currentConstellationIndex);
            if (constellationName != null) {
                Platform.runLater(() -> {
                    clearCurrentConstellation();
                    displayConstellationCircles(constellationName);
                    currentConstellationIndex++;
                    clue.getChildren().clear();
                });
            } else {
            }
        } else {
            stopTimer();
            finishLevel();
            showLevelEndMessage();
        }
    }

    private void showLevelEndMessage() throws IOException {
        MediaPlayer successMediaPlayer = AudioManager.getSuccessMediaPlayer();
        if (!AudioManager.mute) {
            successMediaPlayer.play();
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Congratulations!");
        alert.setHeaderText("You've completed the level.");

        Image image = new Image(getClass().getResource("/celestialchronicles/celestialchronicles/styles/images/icon.png").toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        alert.setGraphic(imageView);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/celestialchronicles/celestialchronicles/styles/images/icon.png")));


        ButtonType exitButton = new ButtonType("Exit");

        alert.getButtonTypes().setAll(exitButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == exitButton) {
            }
        }
    }

    private void clearCurrentConstellation() {
        container.getChildren().clear();
    }

    private void displayConstellationCircles(String constellationName) {

        try {
            String query = "SELECT * FROM constellations WHERE Name = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, constellationName);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int CONSTELLATION_ID = Integer.parseInt(resultSet.getString("id"));
                String imagePath = resultSet.getString("Image");
                if (imagePath != null && !imagePath.isEmpty()) {
                    ImageView constellationImage = new ImageView(new Image(new FileInputStream(imagePath)));
                    constellationImage.setFitWidth(1284);
                    constellationImage.setFitHeight(906);
                    container.getChildren().add(constellationImage);
                }
                name.setText(constellationName);

                winningLineCount = getWinningLineCountFromDB(CONSTELLATION_ID);
                MainStars = getMainStars(CONSTELLATION_ID);
                loadInitialStarPositions(CONSTELLATION_ID);
                requiredLines = loadRequiredLines(CONSTELLATION_ID);

                query = "SELECT * FROM stars WHERE id_constellation = ?";
                statement = connection.prepareStatement(query);
                statement.setInt(1, resultSet.getInt("id"));
                ResultSet starsResultSet = statement.executeQuery();

                while (starsResultSet.next()) {
                    double centerX = starsResultSet.getDouble("x");
                    double centerY = starsResultSet.getDouble("y");
                    double radius = starsResultSet.getDouble("radius_white");

                    Circle circle = new Circle(centerX, centerY, radius);
                    circle.setFill(Color.WHITE);
                    container.getChildren().add(circle);


                    double transparentRadius = starsResultSet.getDouble("radius_transparent");
                    Circle transparentCircle = new Circle(centerX, centerY, transparentRadius);
                    transparentCircle.setFill(Color.TRANSPARENT);
                    container.getChildren().add(transparentCircle);
                    transparentCircle.setOnMouseClicked((MouseEvent event) -> {
                        try {
                            onCircleClicked(event);
                        } catch (SQLException | IOException e) {
                            throw new RuntimeException(e);
                        }
                    });

                    Button deleteLineButton = new Button("Delete Line");
                    deleteLineButton.setLayoutX(1089.0);
                    deleteLineButton.setLayoutY(788.0);
                    deleteLineButton.setPrefWidth(181.0);
                    deleteLineButton.setOnAction(e -> deleteLineClicked());
                    container.getChildren().add(deleteLineButton);

                    Button clearButton = new Button("Clear");
                    clearButton.setLayoutX(1089.0);
                    clearButton.setLayoutY(843.0);
                    clearButton.setPrefWidth(181.0);
                    clearButton.setOnAction(e -> clearClicked());
                    container.getChildren().add(clearButton);

                    Button cutButton = new Button("\u2702");
                    cutButton.setLayoutX(1089.0);
                    cutButton.setLayoutY(733.0);
                    cutButton.setPrefWidth(85.0);
                    cutButton.setOnAction(e -> resetLastClickedCircle());
                    container.getChildren().add(cutButton);

                    Button checkButton = new Button("\u2714");
                    checkButton.setLayoutX(1181.5);
                    checkButton.setLayoutY(733.0);
                    checkButton.setPrefWidth(85.0);
                    checkButton.setOnAction(e -> {
                        try {
                            checkClicked();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                    container.getChildren().add(checkButton);
                }
            }
        } catch (SQLException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void checkClicked() throws SQLException, IOException {
        if (isPatternMatched()) {
            incrementPoints();
            showGameEndMessage();
            loadNextConstellation();
            resetLastClickedCircle();
        } else {
            showIncorrectPatternMessage();
            loadNextConstellation();
            resetLastClickedCircle();
        }
    }

    private Circle previousCircle;

    @FXML
    private void onCircleClicked(javafx.scene.input.MouseEvent event) throws SQLException, IOException {
        MediaPlayer starClickMediaPlayer = getStarClickMediaPlayer();

        if (!AudioManager.mute) {
            starClickMediaPlayer.stop();
            starClickMediaPlayer.play();
        }

        Circle currentCircle = (Circle) event.getSource();

        if (previousCircle == null) {
            previousCircle = currentCircle;
        } else {
            currentCircle.setFill(javafx.scene.paint.Color.rgb(232, 232, 203, 0.5));
            previousCircle.setFill(javafx.scene.paint.Color.rgb(232, 232, 203, 0));
            boolean lineExists = false;
            for (Line line : drawnLines) {
                if ((line.getStartX() == previousCircle.getLayoutX() + previousCircle.getRadius() &&
                        line.getStartY() == previousCircle.getLayoutY() + previousCircle.getRadius() &&
                        line.getEndX() == currentCircle.getLayoutX() + currentCircle.getRadius() &&
                        line.getEndY() == currentCircle.getLayoutY() + currentCircle.getRadius()) ||
                        (line.getStartX() == currentCircle.getLayoutX() + currentCircle.getRadius() &&
                                line.getStartY() == currentCircle.getLayoutY() + currentCircle.getRadius() &&
                                line.getEndX() == previousCircle.getLayoutX() + previousCircle.getRadius() &&
                                line.getEndY() == previousCircle.getLayoutY() + previousCircle.getRadius())) {
                    lineExists = true;
                    break;
                }
            }

            if (!lineExists) {
                Line line = new Line();
                line.setStrokeWidth(2.0);
                line.setStroke(javafx.scene.paint.Color.rgb(255, 255, 255, 1));

                double startX = previousCircle.getCenterX();
                double startY = previousCircle.getCenterY();
                double endX = currentCircle.getCenterX();
                double endY = currentCircle.getCenterY();

                line.setStartX(startX);
                line.setStartY(startY);
                line.setEndX(endX);
                line.setEndY(endY);

                container.getChildren().add(line);
                line.toFront();
                drawnLines.add(line);

                previousCircle = currentCircle;

            }
        }
    }

    private void showIncorrectPatternMessage() {
        MediaPlayer errorMediaPlayer = AudioManager.getErrorMediaPlayer();
        if (!AudioManager.mute) {
            errorMediaPlayer.play();
        }
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Incorrect Pattern");
        alert.setHeaderText("Incorrect Pattern!");
        alert.setContentText("The stars are not arranged correctly.");

        Image image = new Image(getClass().getResource("/celestialchronicles/celestialchronicles/styles/images/stars.png").toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        alert.setGraphic(imageView);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/celestialchronicles/celestialchronicles/styles/images/icon.png")));

        exitLevel(alert);
    }

    public void showGameEndMessage() {
        MediaPlayer successMediaPlayer = AudioManager.getSuccessMediaPlayer();
        if (!AudioManager.mute) {
            successMediaPlayer.play();
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Congratulations!");
        alert.setHeaderText("You've successfully completed the constellation.");

        Image image = new Image(getClass().getResource("/celestialchronicles/celestialchronicles/styles/images/icon.png").toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        alert.setGraphic(imageView);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/celestialchronicles/celestialchronicles/styles/images/icon.png")));

        exitLevel(alert);
    }

    private void exitLevel(Alert alert) {
        ButtonType restartButton = new ButtonType("Next Level");

        alert.getButtonTypes().setAll(restartButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == restartButton) {
                restartLevel();
            }
        }
    }

    private void resetElementPositions() {
        int numElements = Math.min(container.getChildren().size(), Math.min(initialCircleLayoutX.length, initialCircleLayoutY.length));
        int i = 0;
        for (Node node : container.getChildren()) {
            if (node instanceof Circle circle && i < numElements) {
                circle.setLayoutX(initialCircleLayoutX[i]);
                circle.setLayoutY(initialCircleLayoutY[i]);
                i++;
            }
        }
    }

    private void removeLines() {
        container.getChildren().removeAll(drawnLines);
        drawnLines.clear();
    }

    private void resetLastClickedCircle() {
        previousCircle = null;
    }

    private void restartLevel() {
        resetElementPositions();
        removeLines();
        resetLastClickedCircle();
    }

    public boolean isPatternMatched() {

        for (double[] lineCoords : requiredLines) {
            boolean lineFound = false;
            for (Line line : drawnLines) {
                double startX = line.getStartX();
                double startY = line.getStartY();
                double endX = line.getEndX();
                double endY = line.getEndY();
                if ((startX == lineCoords[0] && startY == lineCoords[1] && endX == lineCoords[2] && endY == lineCoords[3]) ||
                        (startX == lineCoords[2] && startY == lineCoords[3] && endX == lineCoords[0] && endY == lineCoords[1])) {
                    lineFound = true;
                    break;
                }
            }
            if (!lineFound) {
                return false;
            }
        }
        return true;
    }

    private void rollbackLastLine() {
        if (!drawnLines.isEmpty()) {
            Line lastLine = drawnLines.iterator().next();
            container.getChildren().remove(lastLine);
            drawnLines.remove(lastLine);
            if (!drawnLines.isEmpty()) {
                previousCircle = getCircleByCoordinates(lastLine.getStartX(), lastLine.getStartY());
            } else {
                previousCircle = null;
            }
        }
    }

    private Circle getCircleByCoordinates(double x, double y) {
        for (Node node : container.getChildren()) {
            if (node instanceof Circle circle && circle.getLayoutX() == x && circle.getLayoutY() == y) {
                return circle;
            }
        }
        return null;
    }

    public void deleteLineClicked() {
        rollbackLastLine();
    }

    public void clearClicked() {
        container.getChildren().removeAll(drawnLines);
        drawnLines.clear();
    }

    private int getMainStars(int constellationId) throws SQLException {
        String query = "SELECT MainStars FROM constellations WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, constellationId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                MainStars = resultSet.getInt("MainStars");
            }
        }
        return MainStars;
    }

    private void loadInitialStarPositions(int constellationId) {
        try {
            String query = "SELECT x, y FROM stars WHERE id_constellation = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, constellationId);
                ResultSet resultSet = statement.executeQuery();
                int i = 0;
                while (resultSet.next() && i < initialCircleLayoutX.length && i < initialCircleLayoutY.length) {
                    initialCircleLayoutX[i] = resultSet.getDouble("x");
                    initialCircleLayoutY[i] = resultSet.getDouble("y");
                    i++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private double[][] loadRequiredLines(int constellationId) {
        try {
            String query = "SELECT s1.x AS start_x, s1.y AS start_y, s2.x AS end_x, s2.y AS end_y " +
                    "FROM constellation_pattern cp " +
                    "JOIN stars s1 ON cp.start = s1.id " +
                    "JOIN stars s2 ON cp.end = s2.id " +
                    "WHERE cp.constellations_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, constellationId);
                ResultSet resultSet = statement.executeQuery();
                List<double[]> linesList = new ArrayList<>();
                while (resultSet.next()) {
                    double startX = resultSet.getDouble("start_x");
                    double startY = resultSet.getDouble("start_y");
                    double endX = resultSet.getDouble("end_x");
                    double endY = resultSet.getDouble("end_y");
                    linesList.add(new double[]{startX, startY, endX, endY});
                }
                return linesList.toArray(new double[0][]);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new double[0][];
        }
    }

    public void nextConstellationClicked() throws SQLException, IOException {
        MediaPlayer errorMediaPlayer = AudioManager.getErrorMediaPlayer();
        if (!AudioManager.mute) {
            errorMediaPlayer.play();
        }
        loadNextConstellation();
    }

    private int getWinningLineCountFromDB(int constellationId) throws SQLException {
        String query = "SELECT WinLines FROM constellations WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, constellationId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                winningLineCount = resultSet.getInt("WinLines");
            }
        }
        return winningLineCount;
    }

    private List<String> getConstellationNames(int levelId) {
        List<String> constellationNames = new ArrayList<>();
        try {
            String query = "SELECT c.Name " +
                    "FROM constellations c " +
                    "LEFT JOIN constellations_level cl ON c.id = cl.id_constellation " +
                    "WHERE cl.id_level = ?";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, levelId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                constellationNames.add(resultSet.getString("Name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return constellationNames;
    }

    public void backClicked(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/celestialchronicles/celestialchronicles/game-menu.fxml")));
        playAudioAndLoadNextScene(actionEvent, root);
    }

    @FXML
    public void clueClicked() {
        if (currentConstellationIndex > 0 && currentConstellationIndex <= constellationNames.size()) {
            String currentConstellationName = constellationNames.get(currentConstellationIndex - 1);
            loadClueImage(currentConstellationName);
        }
        decrementPoints();
    }

    @FXML
    private AnchorPane clue;

    private void loadClueImage(String constellationName) {
        try {
            String query = "SELECT CluePic FROM constellations WHERE Name = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, constellationName);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String cluePicPath = resultSet.getString("CluePic");
                if (cluePicPath != null && !cluePicPath.isEmpty()) {
                    ImageView clueImage = new ImageView(new Image(new FileInputStream(cluePicPath)));
                    clueImage.setFitWidth(clue.getPrefWidth());
                    clueImage.setFitHeight(clue.getPrefHeight());
                    clue.getChildren().clear();
                    clue.getChildren().add(clueImage);
                }
            }
        } catch (SQLException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void startTimer() {
        elapsedTimeSeconds = 0;
        timer = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    elapsedTimeSeconds++;
                    updateTimerLabel();
                })
        );
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void updateTimerLabel() {
        timerLabel.setText(String.format("%.0f:%02.0f", elapsedTimeSeconds / 60, elapsedTimeSeconds % 60));
    }

    private void stopTimer() {
        timer.stop();
        saveLevelTimeToDatabase(elapsedTimeSeconds);
    }

    private void saveLevelTimeToDatabase(double timeSeconds) {
        try {
            double bestTime = getBestTimeFromDatabase();

            if (bestTime == 0 || timeSeconds < bestTime) {
                String query = "UPDATE levels SET best_time = ? WHERE level = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setDouble(1, timeSeconds);
                statement.setInt(2, LEVEL);
                statement.executeUpdate();
            } else {
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private double getBestTimeFromDatabase() throws SQLException {
        double bestTime = 0;

        String query = "SELECT best_time FROM levels WHERE level = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, LEVEL);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            bestTime = resultSet.getDouble("best_time");
        }

        return bestTime;
    }

    private void incrementPoints() {
        points += 1;
    }

    private void decrementPoints() {
        points -= 0.5;
    }

    private void checkAndOpenNextLevel() throws SQLException {
        if (points >= 3 && LEVEL != 5) {
            try {
                String query = "UPDATE levels SET unlocked = 1 WHERE level = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, LEVEL + 1);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private double getBestScoreFromDatabase() throws SQLException {
        double bestScore = 0;

        String query = "SELECT best_score FROM levels WHERE level = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, LEVEL);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            bestScore = resultSet.getDouble("best_score");
        }

        return bestScore;
    }

    private void saveBestScoreToDatabase() throws SQLException {
        double bestScore = getBestScoreFromDatabase();
        try {
            if (points > bestScore) {
                String query = "UPDATE levels SET best_score = ? WHERE level = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setDouble(1, points);
                statement.setInt(2, LEVEL);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void finishLevel() throws SQLException {
        saveBestScoreToDatabase();
        checkAndOpenNextLevel();
    }
}

