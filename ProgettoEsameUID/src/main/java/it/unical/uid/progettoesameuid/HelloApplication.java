package it.unical.uid.progettoesameuid;

import com.almasb.fxgl.app.MainWindow;
import it.unical.uid.progettoesameuid.controller.GiocoController;
import it.unical.uid.progettoesameuid.controller.MenuController;
import it.unical.uid.progettoesameuid.model.MapMask;
import it.unical.uid.progettoesameuid.view.MapView;
import it.unical.uid.progettoesameuid.view.MenuView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import it.unical.uid.progettoesameuid.Main;
import java.io.IOException;

import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;

public class HelloApplication extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        primaryStage.setTitle("Medieval PvZ - Progetto Esame");
        primaryStage.setResizable(false);

        // Avviamo il menu
        showMenu();

        primaryStage.show();
    }

    public void showMenu() {
        MenuView menuView = new MenuView();
        // Passiamo la callback per cambiare schermata
        MenuController menuController = new MenuController(menuView, this::showMap);

        Scene scenaMenu = new Scene(menuView, 1920, 1080);
        applicaCursoreSpada(scenaMenu);
        primaryStage.setScene(scenaMenu);
    }

    public void showMap() {
        System.out.println("Caricamento mappa...");

        // 1. Creiamo il MODEL
        MapMask model = new MapMask();

        // 2. Creiamo la VIEW
        MapView giocoView = new MapView();

        // 3. Colleghiamo entrambi nel CONTROLLER (passando sia model che view!)
        GiocoController giocoController = new GiocoController(model, giocoView);

        Scene scenaGioco = new Scene(giocoView, 1024, 768);
        applicaCursoreSpada(scenaGioco);
        primaryStage.setScene(scenaGioco);
    }


    public void applicaCursoreSpada(Scene scena) {
        try {
            // 1. Carichiamo l'immagine del cursore dalle risorse
            Image immagineSpada = new Image(getClass().getResourceAsStream("/images/spada_cursore.png"));

            // 2. Creiamo il cursore personalizzato.
            // I due numeri (0, 0) rappresentano l' "Hotspot", ovvero il punto esatto in pixel
            // dell'immagine che registra fisicamente il "click".
            // Se la punta della tua spada è in alto a sinistra, (0, 0) è perfetto.
            ImageCursor cursoreSpada = new ImageCursor(immagineSpada, 0, 0);

            // 3. Impostiamo il cursore sulla scena desiderata
            scena.setCursor(cursoreSpada);

        } catch (Exception e) {
            System.err.println("Impossibile caricare il cursore personalizzato: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
