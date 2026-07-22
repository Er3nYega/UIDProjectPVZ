module it.unical.uid.progettoesameuid {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;
    requires java.sql;
    requires org.xerial.sqlitejdbc; // Se te lo richiede esplicitamente l'IDE

    requires javafx.media;

    opens it.unical.uid.progettoesameuid to javafx.fxml;
    opens it.unical.uid.progettoesameuid.controller to javafx.fxml;

    exports it.unical.uid.progettoesameuid;
    opens it.unical.uid.progettoesameuid.model to javafx.fxml;


}