module celestialchronicles.celestialchronicles {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires net.synedra.validatorfx;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.prefs;
    requires java.desktop;

    opens celestialchronicles.celestialchronicles to javafx.fxml;
    exports celestialchronicles.celestialchronicles;
}