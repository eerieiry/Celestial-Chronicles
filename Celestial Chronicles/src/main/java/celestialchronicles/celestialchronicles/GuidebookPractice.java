package celestialchronicles.celestialchronicles;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class GuidebookPractice {

    public AnchorPane container;
    public AnchorPane anchorPane;
    @FXML
    private TextField searchField;
    @FXML
    private ListView<String> constellationsListView;

    private Connection connection;

    private final Set<Line> drawnLines = new HashSet<>();
    private int winningLineCount = 0;
    private int MainStars = 0;
    private double[] initialCircleLayoutX = new double[MainStars];
    private double[] initialCircleLayoutY =new double[MainStars];
    private double[][] requiredLines;

    public void initialize() {
        connectToDatabase();
        loadConstellations();
        if (container != null) {
            for (Node node : container.getChildren()) {
                if (node instanceof Circle circle) {
                    circle.setOnMouseClicked(this::onCircleClicked);
                }
            }
        } else {
            System.err.println("AnchorPane is null. Please check your FXML file.");
        }
    }

    private void connectToDatabase() {
        try {
            String url = "jdbc:mysql://localhost:4044/constellationsdb?user=root";
            String username = "root";
            String password = "12345";
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadConstellations() {
        ObservableList<String> constellations = FXCollections.observableArrayList();
        try {
            String query = "SELECT Name FROM constellations";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String name = resultSet.getString("Name");
                constellations.add(name);
            }

            constellationsListView.setItems(constellations);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void search(ActionEvent actionEvent) {
        String searchTerm = searchField.getText();
        displayConstellationInfo(searchTerm);
    }
    private void displayConstellationInfo(String searchTerm) {
        String selectedConstellation = constellationsListView.getSelectionModel().getSelectedItem();
        displayConstellationCircles(selectedConstellation);
    }

    private void displayConstellationCircles(String constellationName){

        try {
            String query = "SELECT * FROM constellations WHERE Name = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, constellationName);
            ResultSet resultSet = statement.executeQuery();

            container = (AnchorPane) constellationsListView.getScene().lookup("#container");
            container.getChildren().clear();

            if (resultSet.next()) {
                int CONSTELLATION_ID = Integer.parseInt(resultSet.getString("id"));
                String imagePath = resultSet.getString("Image");
                if (imagePath != null && !imagePath.isEmpty()) {
                    ImageView constellationImage = new ImageView(new Image(new FileInputStream(imagePath)));
                    constellationImage.setFitWidth(1284);
                    constellationImage.setFitHeight(906);
                    container.getChildren().add(constellationImage);
                }

                winningLineCount = getWinningLineCountFromDB(CONSTELLATION_ID);
                MainStars = getMainStars(CONSTELLATION_ID);
                loadInitialStarPositions(CONSTELLATION_ID);
                requiredLines = loadRequiredLines(CONSTELLATION_ID);

                // Загружаем и добавляем круги
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


                    // Добавить прозрачный круг
                    double transparentRadius = starsResultSet.getDouble("radius_transparent");
                    Circle transparentCircle = new Circle(centerX, centerY, transparentRadius);
                    transparentCircle.setFill(Color.TRANSPARENT);
                    container.getChildren().add(transparentCircle);
                    transparentCircle.setOnMouseClicked((MouseEvent event) -> onCircleClicked(event));

                    // Создаем кнопки
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
                }
            }
        } catch (SQLException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Circle previousCircle;

    @FXML
    private void onCircleClicked(javafx.scene.input.MouseEvent event) {

        Circle currentCircle = (Circle) event.getSource();

        if (previousCircle == null) {
            previousCircle = currentCircle;
        }
        else {
            currentCircle.setFill(javafx.scene.paint.Color.rgb(232, 232, 203, 0.5));
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

                if (drawnLines.size() == winningLineCount) {
                    if (isPatternMatched()) {
                        showGameEndMessage();
                    } else {
                        showIncorrectPatternMessage();
                    }
                }
            }
        }
    }

    private void showIncorrectPatternMessage() {
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

        // Створення повідомлення
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

        // Додавання кнопок для виходу або перезапуску рівня
        ButtonType exitButton = new ButtonType("Exit");
        ButtonType restartButton = new ButtonType("Restart Level");

        alert.getButtonTypes().setAll(exitButton, restartButton);

        // Обробка вибору користувача
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == exitButton) {
            } else if (result.get() == restartButton) {
                restartLevel();
            }
        }
    }

    private void exitLevel(Alert alert) {
        ButtonType exitButton = new ButtonType("Exit Level");
        ButtonType restartButton = new ButtonType("Restart Level");

        alert.getButtonTypes().setAll(exitButton, restartButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == exitButton) {
            } else if (result.get() == restartButton) {
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

    public void displaySelectedConstellation(MouseEvent mouseEvent) {
        String selectedConstellation = constellationsListView.getSelectionModel().getSelectedItem();
        displayConstellationInfo(selectedConstellation);
    }

    public void backClicked(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("game-menu.fxml")));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, MainMenuController.screenWidth, MainMenuController.screenHeight);
        stage.setScene(scene);
        stage.setFullScreen(MainMenuController.fullScreenBool);
        stage.show();
    }

    public void knowledgeClicked(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("guidebook-knowledge.fxml")));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, MainMenuController.screenWidth, MainMenuController.screenHeight);
        stage.setScene(scene);
        stage.setFullScreen(MainMenuController.fullScreenBool);
        stage.show();
    }

    public void constellationsClicked(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("guidebook-constellations.fxml")));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, MainMenuController.screenWidth, MainMenuController.screenHeight);
        stage.setScene(scene);
        stage.setFullScreen(MainMenuController.fullScreenBool);
        stage.show();
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
            String query = "SELECT start_x, start_y, end_x, end_y FROM constellation_pattern WHERE constellations_id = ?";
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
}

