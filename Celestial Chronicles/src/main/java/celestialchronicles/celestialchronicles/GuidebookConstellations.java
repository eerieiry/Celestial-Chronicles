package celestialchronicles.celestialchronicles;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.ListView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.Objects;

import static celestialchronicles.celestialchronicles.Database.*;

public class GuidebookConstellations {
    @FXML
    private TextField searchField;

    @FXML
    private Label nameLabel;

    @FXML
    private Label mainStarsLabel;

    @FXML
    private Label brightestStarLabel;

    @FXML
    private Label nearestConstellationLabel;

    @FXML
    private ImageView imageView;

    @FXML
    private ListView<String> constellationsListView;

    public void initialize() {
        connectToDatabase();
        loadConstellations();
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
        try {
            String query = "SELECT * FROM constellations WHERE Name LIKE ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, "%" + searchTerm + "%");
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("Name");
                String mainStars = resultSet.getString("MainStars");
                String brightestStar = resultSet.getString("BrightestStar");
                String borderingConstellations = resultSet.getString("BorderingConstellations");
                String imagePath = resultSet.getString("Picture");

                nameLabel.setText(name);
                mainStarsLabel.setText("Main stars: " + mainStars);
                brightestStarLabel.setText("Brightest star: " + brightestStar);
                nearestConstellationLabel.setText("Bordering constellations:\n" + borderingConstellations);
                imageView.setImage(new Image(new FileInputStream(imagePath)));
            } else {
                nameLabel.setText("Constellation not found");
                mainStarsLabel.setText("");
                brightestStarLabel.setText("");
                nearestConstellationLabel.setText("");
                imageView.setImage(null);
            }

        } catch (SQLException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void displaySelectedConstellation(MouseEvent mouseEvent) {
        String selectedConstellation = constellationsListView.getSelectionModel().getSelectedItem();
        displayConstellationInfo(selectedConstellation);
    }

    public void backClicked(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("game-menu.fxml")));
        AudioManager.playAudioAndLoadNextScene(actionEvent, root);
    }

    public void practiceClicked(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("guidebook-practice.fxml")));
        AudioManager.playAudioAndLoadNextScene(actionEvent, root);
    }

    public void knowledgeClicked(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("guidebook-knowledge.fxml")));
        AudioManager.playAudioAndLoadNextScene(actionEvent, root);
    }

}
