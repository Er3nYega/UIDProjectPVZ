package it.unical.uid.demo1;

import it.unical.uid.demo1.model.GrigliaModel;
import it.unical.uid.demo1.model.Pianta;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.input.MouseEvent;

public class HelloController {
    // L'iniettore collega questa variabile all'oggetto con lo stesso fx:id nell'FXML
    @FXML
    private GridPane gridPane;

    // Riferimento al Model della griglia (logica di gioco)
    private GrigliaModel model;

    private String piantaSelezionata = "Sparasemi"; // Stato di test

    @FXML
    public void initialize() {
        // 1. Inizializza il modello logico (es. matrice 5x9)
        model = new GrigliaModel();

        // 2. Popola il GridPane con i componenti interattivi
        popolaGrigliaVisiva();
    }

    private void popolaGrigliaVisiva() {
        int righe = 5;
        int colonne = 9;

        for (int r = 0; r < righe; r++) {
            for (int c = 0; c < colonne; c++) {
                // Crea un contenitore per la singola cella
                StackPane cellaInterattiva = new StackPane();

                // CORREZIONE: Diamo un colore verde e un bordo visibile per il testing!
                cellaInterattiva.setStyle("-fx-background-color: #2e7d32; -fx-border-color: rgba(0, 0, 0, 0.3);");

                // Memorizza le coordinate correnti all'interno del nodo
                cellaInterattiva.setUserData(new int[]{r, c});

                // Associa l'evento di click direttamente alla cella
                cellaInterattiva.setOnMouseClicked(this::gestisciClickCella);

                // CORREZIONE: Assicurati che sia (colonna, riga) -> c, r
                gridPane.add(cellaInterattiva, c, r);
            }
        }
    }

    private void gestisciClickCella(MouseEvent event) {
        // Recupera lo StackPane che ha catturato il click
        StackPane cellaCliccata = (StackPane) event.getSource();

        // Estrae le coordinate associate
        int[] coords = (int[]) cellaCliccata.getUserData();
        int riga = coords[0];
        int colonna = coords[1];

        /**if (piantaSelezionata != null) {
         // Verifica la fattibilità del posizionamento nel Model
         //Pianta nuovaPianta = new Pianta(piantaSelezionata, 300);
         //boolean posizionato = model.posizionaPianta(riga, colonna, nuovaPianta);

         if (posizionato) {
         // Se il modello valida l'azione, aggiorna l'interfaccia visiva
         ImageView sprite = new ImageView(new Image("file:risorse/sparasemi.png"));
         sprite.setFitWidth(70);
         sprite.setFitHeight(70);

         cellaCliccata.getChildren().add(sprite);
         System.out.println("Pianta inserita nell'area: [" + riga + ", " + colonna + "]");
         } else {
         System.out.println("Area occupata o risorse insufficienti.");
         }
         }**/
    }
}

