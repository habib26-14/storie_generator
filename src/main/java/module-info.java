module com.example.stories {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires gson;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires json.simple;
    requires freetts;

    opens com.example.stories.Dao.impl to javafx.base, javafx.graphics, javafx.fxml;
    opens com.example.stories.Dao to javafx.base, javafx.graphics, javafx.fxml;
    opens com.example.stories.entities to javafx.base, javafx.graphics, javafx.fxml, gson;
    opens com.example.stories.service.File to javafx.base, javafx.graphics, javafx.fxml;
    opens com.example.stories.service to javafx.base, javafx.graphics, javafx.fxml;
    opens com.example.stories to javafx.base, javafx.graphics, javafx.fxml;
    opens com.example.stories.controllers to javafx.base, javafx.graphics, javafx.fxml;

    exports com.example.stories.Dao.impl;
    exports com.example.stories.Dao;
    exports com.example.stories.entities;
    exports com.example.stories.controllers;
    exports com.example.stories;
}