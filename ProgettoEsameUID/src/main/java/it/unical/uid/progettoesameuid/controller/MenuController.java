package it.unical.uid.progettoesameuid.controller;

import it.unical.uid.progettoesameuid.HelloApplication;
import it.unical.uid.progettoesameuid.model.DatabaseManager;
import it.unical.uid.progettoesameuid.model.GameStateDTO;
import it.unical.uid.progettoesameuid.model.MapMask;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.net.URL;

public class MenuController {

    @FXML private VBox panelSettings;
    @FXML private VBox panelSlot;

    @FXML private Button btnSlot1;
    @FXML private Button btnSlot2;
    @FXML private Button btnSlot3;

    private HelloApplication mainApp;
    private final DatabaseManager dbManager = new DatabaseManager();

    @FXML private Slider sliderMusica;

    public MenuController() {}

    public void setMainApp(HelloApplication mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    public void initialize() {

        if (panelSettings != null) {
            panelSettings.setVisible(false);
            panelSettings.setDisable(true);
        }
        if (panelSlot != null) {
            panelSlot.setVisible(false);
            panelSlot.setDisable(true);
        }

        var urlSlot = getClass().getResource("/pannelllo2.png"); // Usa la tua immagine
        if (urlSlot != null) {
            Image imgSlot = new Image(urlSlot.toExternalForm());
            BackgroundSize bgSize = new BackgroundSize(100, 100, true, true, true, false);
            BackgroundImage bgImg = new BackgroundImage(imgSlot, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, bgSize);
            panelSlot.setBackground(new Background(bgImg));
        }

        java.net.URL urlImmagine = getClass().getResource("/pannelllo.png");
        System.out.println("--------------------------------------------------");
        System.out.println("🔍 PERCORSO IMMAGINE TROVATO: " + urlImmagine);
        System.out.println("--------------------------------------------------");

        if (urlImmagine != null) {
            Image img = new Image(urlImmagine.toExternalForm());
            BackgroundImage bgImg = new BackgroundImage(
                    img,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(100, 100, true, true, true, false)
            );
            panelSettings.setBackground(new Background(bgImg));
        }

        // 🎵 Avvia la musica di sottofondo del Menu
        it.unical.uid.progettoesameuid.utility.SoundManager.riproduciMusica("menu_theme.mp3");

        // ⚙️ Collega lo Slider delle Impostazioni al SoundManager
        if (sliderMusica != null) {
            sliderMusica.setValue(it.unical.uid.progettoesameuid.utility.SoundManager.getVolumeMusica());
            sliderMusica.valueProperty().addListener((obs, oldVal, newVal) -> {
                it.unical.uid.progettoesameuid.utility.SoundManager.setVolumeMusica(newVal.doubleValue());
            });
        }
    }

    @FXML
    private void gestisciAvviaGioco() {
        apriSottomenuSalvataggi();
    }

    @FXML
    private void apriSottomenuSalvataggi() {
        aggiornaTestiSlot();
        if (panelSlot != null) {
            panelSlot.setVisible(true);
            panelSlot.setDisable(false);
        }
    }

    @FXML
    private void chiudiSottomenuSalvataggi() {
        if (panelSlot != null) {
            panelSlot.setVisible(false);
            panelSlot.setDisable(true);
        }
    }

    private void aggiornaTestiSlot() {
        aggiornaSingoloPulsanteSlot(btnSlot1, 1);
        aggiornaSingoloPulsanteSlot(btnSlot2, 2);
        aggiornaSingoloPulsanteSlot(btnSlot3, 3);
    }

    private void aggiornaSingoloPulsanteSlot(Button btn, int slotId) {
        if (btn == null) return;

        // Reset pulito delle classi CSS
        btn.getStyleClass().removeAll("slot-pieno", "slot-vuoto");

        GameStateDTO dto = dbManager.caricaPartita(slotId);
        if (dto != null) {
            String data = dto.dataSalvataggio != null ? dto.dataSalvataggio : "";
            btn.setText("SLOT " + slotId + " - Ondata " + dto.ondataAttuale);

            // Assegna classe CSS per slot salvato
            btn.getStyleClass().add("slot-pieno");
        } else {
            btn.setText("SLOT " + slotId + " - VUOTO");

            // Assegna classe CSS per slot vuoto
            btn.getStyleClass().add("slot-vuoto");
        }

        btn.applyCss();
    }

    @FXML private void caricaSlot1(ActionEvent event) { selezionaSlotEAvvia(event, 1); }
    @FXML private void caricaSlot2(ActionEvent event) { selezionaSlotEAvvia(event, 2); }
    @FXML private void caricaSlot3(ActionEvent event) { selezionaSlotEAvvia(event, 3); }

    /**
     * Se lo slot contiene un salvataggio, lo CARICA.
     * Se lo slot è VUOTO, inizia una NUOVA PARTITA associata a quel numero di Slot.
     */
    private void selezionaSlotEAvvia(ActionEvent event, int slotId) {
        GameStateDTO salvataggio = dbManager.caricaPartita(slotId);

        try {
            URL resource = getClass().getResource("/it/unical/uid/progettoesameuid/MappaGioco.fxml");
            if (resource == null) resource = getClass().getResource("/MappaGioco.fxml");

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            GiocoController giocoController = loader.getController();
            MapMask model = new MapMask();
            giocoController.setModel(model);

            // 🎯 Impostiamo lo SlotAttuale selezionato
            giocoController.setSlotAttuale(slotId);

            if (salvataggio != null) {
                // Se c'erano dati, li ricarica
                giocoController.caricaStatoDaDB(salvataggio);
                System.out.println("✅ Caricata partita salvata nello Slot " + slotId);
            } else {
                System.out.println("🎮 Iniziata nuova partita nello Slot " + slotId);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void eliminaSlot1(ActionEvent event) {
        cancellaSalvataggioSlot(1);
    }

    @FXML
    private void eliminaSlot2(ActionEvent event) {
        cancellaSalvataggioSlot(2);
    }

    @FXML
    private void eliminaSlot3(ActionEvent event) {
        cancellaSalvataggioSlot(3);
    }

    private void cancellaSalvataggioSlot(int slotId) {
        boolean successo = dbManager.eliminaSlot(slotId);
        if (successo) {
            System.out.println("🗑️ Slot " + slotId + " svuotato con successo!");
            aggiornaTestiSlot(); // Rinfresca la vista rendendo lo slot "VUOTO"
        }
    }

    @FXML
    private void apriSettings() {
        if (panelSettings != null) {
            panelSettings.setVisible(true);
            panelSettings.setDisable(false);
        }
    }

    @FXML
    private void backSettings() {
        if (panelSettings != null) {
            panelSettings.setVisible(false);
            panelSettings.setDisable(true);
        }
    }

    @FXML
    private void esciGioco() {
        Platform.exit();
        System.exit(0);
    }
}