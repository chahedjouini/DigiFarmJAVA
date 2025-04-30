module esprit.tn.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires itextpdf;

    opens esprit.tn.demo to javafx.fxml;
    exports esprit.tn.demo.tests;
    opens esprit.tn.demo.tests to javafx.fxml;
    opens esprit.tn.demo.controllers.GestionMachine to javafx.fxml;
    exports esprit.tn.demo.controllers.GestionMachine;




    exports esprit.tn.demo.controllers.GestionVente; // Pour que d'autres modules y accèdent
    opens esprit.tn.demo.controllers.GestionVente to javafx.fxml; // Pour que FXMLLoader accède aux annotations @FXML
    exports esprit.tn.demo.entities.GestionVente;

    exports esprit.tn.demo.controllers; // pour que d'autres classes l'utilisent
    opens esprit.tn.demo.controllers to javafx.fxml; // pour autoriser le chargement FXML
    exports esprit.tn.demo.services.GestionVente;

}