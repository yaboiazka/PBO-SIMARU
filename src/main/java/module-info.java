module com.simaru {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.simaru.controller to javafx.fxml;
    opens com.simaru.dao to javafx.fxml;
    opens com.simaru.model to javafx.base, javafx.fxml;
    exports com.simaru;
}
