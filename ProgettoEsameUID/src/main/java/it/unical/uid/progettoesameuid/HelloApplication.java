package it.unical.uid.progettoesameuid;

import com.almasb.fxgl.app.MainWindow;
import it.unical.uid.progettoesameuid.controller.GiocoController;
import it.unical.uid.progettoesameuid.controller.MenuController;
import it.unical.uid.progettoesameuid.model.MapMask;

import it.unical.uid.progettoesameuid.view.MenuView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unical/uid/progettoesameuid/MenuPrincipale.fxml"));
            Parent root = loader.load();

            MenuController controller = loader.getController();
            controller.setMainApp(this);

            Scene scenaMenu = new Scene(root, 1920, 1080);
            primaryStage.setTitle("Resonance: Medieval Defense");
            primaryStage.setScene(scenaMenu);

            // ==========================================
            // RIGA MAGICA PER IL FULL SCREEN DIRETTAMENTE ALL'AVVIO
            // ==========================================
            primaryStage.setFullScreen(true);
            primaryStage.setFullScreenExitHint(""); // Rende il testo del suggerimento vuoto
            primaryStage.show();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showMap() {
        System.out.println("Caricamento mappa via FXML...");

        try {
            // 1. Carichiamo il file FXML della mappa
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unical/uid/progettoesameuid/MappaGioco.fxml"));
            Parent rootMappa = loader.load();

            // 2. Creiamo il MODEL logico del gioco
            MapMask model = new MapMask();

            // 3. Recuperiamo il CONTROLLER generato automaticamente da JavaFX tramite l'FXML
            GiocoController giocoController = loader.getController();

            // Iniettiamo il modello logico appena creato dentro il controller
            giocoController.setModel(model);

            // 4. Creiamo la Scena con il root FXML caricato
            Scene scenaGioco = new Scene(rootMappa, 1920, 1080);

            // Applichiamo il tuo cursore personalizzato a spada
            applicaCursoreSpada(scenaGioco);

            // Configuri lo Stage
            primaryStage.setTitle("Resonance: Medieval Defense");
            primaryStage.setScene(scenaGioco);

            // ==========================================
            // RIGA MAGICA PER IL FULL SCREEN DIRETTAMENTE ALL'AVVIO
            // ==========================================
            primaryStage.setFullScreen(true);
            primaryStage.setFullScreenExitHint("");
            primaryStage.show();

        } catch (IOException e) {
            System.err.println("Errore fatale nel caricamento di MappaGioco.fxml!");
            e.printStackTrace();
        }
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
