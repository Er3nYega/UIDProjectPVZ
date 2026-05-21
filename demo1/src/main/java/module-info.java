module it.unical.uid.demo1 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.almasb.fxgl.all;

    opens it.unical.uid.demo1 to javafx.fxml;
    exports it.unical.uid.demo1;
}