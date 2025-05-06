module esprit.tn.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.google.protobuf;
    requires java.net.http;
    requires java.sql;
    requires org.slf4j;
    requires org.apache.poi.ooxml;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpclient;
    requires org.json;



    opens esprit.tn.demo to javafx.fxml;
    opens esprit.tn.demo.controllers.GestionAnimal to javafx.fxml;
    opens esprit.tn.demo.entities.GestionAnimal to javafx.base;

    exports esprit.tn.demo.tests;
}
