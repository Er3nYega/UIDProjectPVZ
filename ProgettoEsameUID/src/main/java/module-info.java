module it.unical.uid.progettoesameuid {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;
    requires java.sql;

    opens it.unical.uid.progettoesameuid to javafx.fxml;
    exports it.unical.uid.progettoesameuid;
}