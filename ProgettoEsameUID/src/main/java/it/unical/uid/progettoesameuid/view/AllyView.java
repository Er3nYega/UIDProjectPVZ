package it.unical.uid.progettoesameuid.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class AllyView extends StackPane {

    private ImageView sprite;

    public AllyView(String tipoAlleato) {
        // Carichiamo l'immagine corretta in base al tipo di alleato
        // Nota: Assicurati di avere le immagini nella cartella resources!
        String percorsoImmagine = "/images/" + tipoAlleato.toLowerCase() + ".png";

        try {
            Image img = new Image(getClass().getResourceAsStream(percorsoImmagine));
            sprite = new ImageView(img);

            // Ridimensioniamo l'immagine per adattarla alla cella (90x90 pixel)
            sprite.setFitWidth(80);
            sprite.setFitHeight(80);
            sprite.setPreserveRatio(true);

            this.getChildren().add(sprite);
        } catch (Exception e) {
            System.err.println("Errore nel caricamento dell'immagine per: " + tipoAlleato);
            // Fallback temporaneo: se manca l'immagine, mostriamo un cerchio colorato per non crashare
            javafx.scene.shape.Circle placeholder = new javafx.scene.shape.Circle(35, javafx.scene.paint.Color.BLUE);
            this.getChildren().add(placeholder);
        }
    }
}
