module esprit.tn.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens esprit.tn.demo to javafx.fxml;
    exports esprit.tn.demo.tests;
    opens esprit.tn.demo.tests to javafx.fxml;
}