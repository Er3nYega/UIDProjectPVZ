package it.unical.uid.progettoesameuid.controller;
import it.unical.uid.progettoesameuid.HelloApplication;
import it.unical.uid.progettoesameuid.Main;
import it.unical.uid.progettoesameuid.view.MenuView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.sql.SQLOutput;

public class MenuController {
    private Runnable onStartGame;
    private MenuView view;
    public MenuController()
    {

    }



    private HelloApplication mainApp; // Riferimento alla classe principale per cambiare scena

    @FXML
    private VBox panelSettings;

    @FXML
    private Button bottoneGioca; // Deve avere lo stesso identico fx:id di Scene Builder

    // Questo serve per dare al controller il riferimento all'applicazione principale
    public void setMainApp(HelloApplication mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void gestisciAvviaGioco() {
        System.out.println("Il controller Menu dice: Avvia Gioco");
        if (mainApp != null) {
            // Chiamiamo il metodo in puro codice Java che abbiamo sistemato oggi!
            mainApp.showMap();
        }
    }

    @FXML
    public void initialize() {
        // Ci assicuriamo programmaticamente che all'avvio il pannello sia nascosto
        if (panelSettings != null) {
            panelSettings.setVisible(false);
        }
    }

    /**
     * Viene chiamato quando il giocatore clicca sul pulsante SETTINGS
     */
    @FXML
    private void apriSettings() {
        if (panelSettings != null) {
            panelSettings.setVisible(true);
            panelSettings.setDisable(false); // Si assicura che sia abilitato

            // Forza il contenitore a ridisegnare i componenti grafici
            panelSettings.requestLayout();

            System.out.println("Il VBox è ora visibile? " + panelSettings.isVisible());
        } else {
            System.out.println("Errore: panelSettings è ancora NULL!");
        }
    }

    /**
     * Viene chiamato quando il giocatore clicca sul pulsante CHIUDI dentro le impostazioni
     */
    @FXML
    private void backSettings() {
        panelSettings.setVisible(false);
        panelSettings.setDisable(true);
    }
}

