module esprit.tn.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens esprit.tn.demo to javafx.fxml;
    opens esprit.tn.demo.controllers.GestionAnimal to javafx.fxml;
    opens esprit.tn.demo.entities.GestionAnimal to javafx.base;

    exports esprit.tn.demo.tests;
}
