package it.unical.uid.progettoesameuid.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class EnemyView extends StackPane {

    private ImageView spriteE;
    private final double larghezzaCella = 90.0; // La larghezza di una cella della tua griglia

    public EnemyView(String tipoNemico) {
        String percorsoImmagine = "/images/" + tipoNemico.toLowerCase() + ".png"; // o .gif per averlo animato!

        try {
            Image img = new Image(getClass().getResourceAsStream(percorsoImmagine));
            spriteE = new ImageView(img);
            spriteE.setFitWidth(80);
            spriteE.setFitHeight(80);
            spriteE.setPreserveRatio(true);

            this.getChildren().add(spriteE);
        } catch (Exception e) {
            System.err.println("Errore nel caricamento dell'immagine per: " + tipoNemico);
            // Fallback: un cerchio rosso se non trova l'immagine dello zombie
            javafx.scene.shape.Circle placeholder = new javafx.scene.shape.Circle(35, javafx.scene.paint.Color.RED);
            this.getChildren().add(placeholder);
        }
    }

    /**
     * Aggiorna la posizione visiva dello zombie usando il TranslateX di JavaFX.
     * @param xLogica La coordinata X reale memorizzata nel modello (es. da 9.0 a 0.0)
     * @param colonnaIniziale La colonna del GridPane in cui è stato inserito lo zombie all'inizio (es. 8)
     */
    public void aggiornaPosizioneFluida(double xLogica, int colonnaIniziale) {
        // Calcoliamo dove si trova logicamente lo zombie rispetto alla colonna in cui è stato spawnato
        double posizionePixelModello = xLogica * larghezzaCella;
        double posizionePixelInizialeGrid = colonnaIniziale * larghezzaCella;

        // L'offset da dare a TranslateX è la differenza tra dove deve essere e dove la griglia pensa che sia
        double offsetVisuale = posizionePixelModello - posizionePixelInizialeGrid;

        this.setTranslateX(offsetVisuale);
    }
}
