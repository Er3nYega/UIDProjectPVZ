package it.unical.uid.progettoesameuid;

import it.unical.uid.progettoesameuid.controller.GiocoController;
import it.unical.uid.progettoesameuid.controller.MenuController;
import it.unical.uid.progettoesameuid.model.MapMask;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    private Stage primaryStage;

    private static final double LARGHEZZA_BASE = 1920.0;
    private static final double ALTEZZA_BASE = 1080.0;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setTitle("Resonance: Medieval Defense");

        // Avviamo il menu iniziale
        showMenu();
    }

    public void showMenu() {
        try {
            var resource = getClass().getResource("/it/unical/uid/progettoesameuid/MenuPrincipale.fxml");
            if (resource == null) resource = getClass().getResource("/MenuPrincipale.fxml");
            if (resource == null) resource = getClass().getResource("/it/unical/uid/progettoesameuid/Menu.fxml");

            if (resource == null) throw new IOException("Impossibile trovare il file FXML del Menu!");

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof MenuController menuController) {
                menuController.setMainApp(this);
            }

            Scene scenaMenu = creaScenaAutoScalante(root);
            applicaCursoreSpada(scenaMenu);

            primaryStage.setScene(scenaMenu);
            impostaFullscreen();

        } catch (IOException e) {
            System.err.println("Errore nel caricamento del Menu:");
            e.printStackTrace();
        }
    }

    public void showMap() {
        System.out.println("Caricamento mappa via FXML...");

        try {
            var resource = getClass().getResource("/it/unical/uid/progettoesameuid/MappaGioco.fxml");
            if (resource == null) resource = getClass().getResource("/MappaGioco.fxml");

            if (resource == null) throw new IOException("Impossibile trovare MappaGioco.fxml!");

            FXMLLoader loader = new FXMLLoader(resource);
            Parent rootMappa = loader.load();

            MapMask model = new MapMask();
            GiocoController giocoController = loader.getController();
            if (giocoController != null) {
                giocoController.setModel(model);
            }

            Scene scenaGioco = creaScenaAutoScalante(rootMappa);
            applicaCursoreSpada(scenaGioco);

            primaryStage.setScene(scenaGioco);
            impostaFullscreen();

        } catch (IOException e) {
            System.err.println("Errore fatale nel caricamento di MappaGioco.fxml!");
            e.printStackTrace();
        }
    }

    private Scene creaScenaAutoScalante(Parent fxmlContent) {
        // 1. BLOCCHIAMO I CONFINI DELL'FXML (Niente spasmi se la freccia esce a destra!)
        Rectangle clip = new Rectangle(LARGHEZZA_BASE, ALTEZZA_BASE);
        fxmlContent.setClip(clip);

        // 2. StackPane contenitore pulito
        StackPane rootContainer = new StackPane();
        rootContainer.setStyle("-fx-background-color: black;");
        rootContainer.getChildren().add(fxmlContent);

        Scene scene = new Scene(rootContainer, LARGHEZZA_BASE, ALTEZZA_BASE);

        // 3. Trasformazione di scala controllata
        Scale scale = new Scale();
        scale.setPivotX(LARGHEZZA_BASE / 2.0);
        scale.setPivotY(ALTEZZA_BASE / 2.0);

        scale.xProperty().bind(Bindings.min(
                scene.widthProperty().divide(LARGHEZZA_BASE),
                scene.heightProperty().divide(ALTEZZA_BASE)
        ));
        scale.yProperty().bind(scale.xProperty());

        fxmlContent.getTransforms().clear();
        fxmlContent.getTransforms().add(scale);


        // 2. USIAMO addEventFilter PER INTERCETTARE F11 SENZA CANCELLARE IL MENU DI PAUSA
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.F11) {
                primaryStage.setFullScreen(!primaryStage.isFullScreen());
                event.consume(); // Blocca l'evento qui per F11
            }
        });

        return scene;
    }

    private void impostaFullscreen() {
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");

        // 1. Disabilita il tasto ESC nativo del sistema per il Fullscreen
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

        primaryStage.show();
    }

    public void applicaCursoreSpada(Scene scena) {
        try {
            var stream = getClass().getResourceAsStream("/images/spada_cursore.png");
            if (stream != null) {
                Image immagineSpada = new Image(stream);
                ImageCursor cursoreSpada = new ImageCursor(immagineSpada, 0, 0);
                scena.setCursor(cursoreSpada);
            }
        } catch (Exception e) {
            System.err.println("Impossibile caricare il cursore personalizzato: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}