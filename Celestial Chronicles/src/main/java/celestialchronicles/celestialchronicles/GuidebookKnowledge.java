package celestialchronicles.celestialchronicles;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.*;
import java.sql.*;
import java.util.Objects;

public class GuidebookKnowledge {
    @FXML
    private TextField searchField;

    @FXML
    private Label nameLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private ImageView imageView;

    @FXML
    private ListView<String> constellationsListView;

    private Connection connection;

    public void initialize() {
        connectToDatabase();
        loadConstellations();
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
            String query = "SELECT Name FROM knowledge";
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
            String query = "SELECT * FROM knowledge WHERE Name LIKE ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, "%" + searchTerm + "%");
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("Name");
                String descriptionFilePath  = resultSet.getString("Description");
                String imagePath = resultSet.getString("Picture");

                nameLabel.setText(name);

                StringBuilder descriptionBuilder = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new FileReader(descriptionFilePath ))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        descriptionBuilder.append(line).append("\n");
                    }
                }

                String description = descriptionBuilder.toString();
                descriptionLabel.setText(description);

                imageView.setImage(new Image(new FileInputStream(imagePath)));
            } else {
                nameLabel.setText("Information not found");
                descriptionLabel.setText("");
                imageView.setImage(null);
            }

        } catch (SQLException | IOException e) {
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

    public void constellationsClicked(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("guidebook-constellations.fxml")));
        AudioManager.playAudioAndLoadNextScene(actionEvent, root);
    }
}
