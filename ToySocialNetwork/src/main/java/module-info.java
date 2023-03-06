module socialnetwork.toysocialnetwork {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens socialnetwork.toysocialnetwork to javafx.fxml;
    exports socialnetwork.toysocialnetwork;
    opens socialnetwork.toysocialnetwork.controller to javafx.fxml;
    exports socialnetwork.toysocialnetwork.controller;
    opens socialnetwork.toysocialnetwork.domain to javafx.fxml;
    exports socialnetwork.toysocialnetwork.domain;
    opens socialnetwork.toysocialnetwork.domain.dto to javafx.fxml;
    exports socialnetwork.toysocialnetwork.domain.dto;
    exports socialnetwork.toysocialnetwork.config;
    opens socialnetwork.toysocialnetwork.config to javafx.fxml;

}