module celestialchronicles.celestialchronicles {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires net.synedra.validatorfx;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.prefs;
    requires java.desktop;
    requires java.sql;

    opens celestialchronicles.celestialchronicles to javafx.fxml;
    exports celestialchronicles.celestialchronicles;
    exports celestialchronicles.celestialchronicles.constellations;
    opens celestialchronicles.celestialchronicles.constellations to javafx.fxml;
}