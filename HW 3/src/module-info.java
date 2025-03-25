/**
 * This is the module info
 * @author Prof. Carter
 */
module CSE360TeamProject {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires java.sql;
    requires junit;

    opens application to javafx.fxml, javafx.graphics, javafx.base;
}

