package it.unical.uid.progettoesameuid.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class BulletView extends StackPane {

    private final int riga;
    private final double velocita = 7.0;
    private final int danno; // Non più fisso!

    // Passiamo il danno come parametro al costruttore
    public BulletView(String nomeAlleato, double startX, double startY, int riga, int danno) {
        this.riga = riga;
        this.danno = danno; // Assegnazione dinamica dal modello

        this.setMouseTransparent(true);

        try {
            var stream = getClass().getResourceAsStream("/images/" + nomeAlleato + "_bullet.png");
            if (stream != null) {
                ImageView imgView = new ImageView(new Image(stream));
                imgView.setFitWidth(60);
                imgView.setFitHeight(30);
                imgView.setPreserveRatio(true);
                this.getChildren().add(imgView);
            }
        } catch (Exception e) {
            System.err.println("Bullet sprite non trovato per: " + nomeAlleato);
        }

        this.setLayoutX(startX);
        this.setLayoutY(startY);
    }

    public void muoviADestra() {
        this.setLayoutX(this.getLayoutX() + velocita);
    }

    public int getRiga() { return riga; }
    public int getDanno() { return danno; }
}