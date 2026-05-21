package it.unical.uid.demo1;

import it.unical.uid.demo1.model.GrigliaModel;
import it.unical.uid.demo1.view.GrigliaView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1820, 920);

        GrigliaModel model = new GrigliaModel();
        GrigliaView view = new GrigliaView();


        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

    }
}
