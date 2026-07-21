package it.unical.uid.progettoesameuid.view;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class EnemyView extends StackPane {

    private final ImageView imageView;
    private int rigaAttuale;
    private double velocita = 0.4; // Velocità di avanzamento verso sinistra

    // --- PARAMETRI SPRITESHEET ---
    private static final int LARGHEZZA_FRAME = 200; // Larghezza di un singolo fotogramma in px
    private static final int ALTEZZA_FRAME = 200;   // Altezza di un singolo fotogramma in px
    private static final int TOTALE_FRAME = 8;      // Numero di frame presenti nella camminata
    private int salute = 100;

    private Transition animazioneCamminata; // Riferimento per pausa e ripresa
    private long ultimoAttaccoTime = 0;

    public EnemyView(String nomeNemico, int rigaIniziale) {
        this.rigaAttuale = rigaIniziale;
        this.imageView = new ImageView();

        try {
            // Carichiamo lo spritesheet (es: /images/scheletro.png)
            String percorso = "/images/" + nomeNemico + ".png";
            var stream = getClass().getResourceAsStream(percorso);

            if (stream != null) {
                Image spriteSheet = new Image(stream);
                imageView.setImage(spriteSheet);

                // Mostra inizialmente solo il primo fotogramma
                imageView.setViewport(new Rectangle2D(0, 0, LARGHEZZA_FRAME, ALTEZZA_FRAME));

                // Forziamo le dimensioni visive reali
                imageView.setFitWidth(LARGHEZZA_FRAME);
                imageView.setFitHeight(ALTEZZA_FRAME);
                imageView.setPreserveRatio(true);

                this.getChildren().add(imageView);

                // AVVIAMO L'ANIMAZIONE DI CAMMINATA
                avviaAnimazioneCamminata();
            } else {
                System.err.println("ERRORE: Impossibile trovare lo spritesheet per il nemico: " + nomeNemico);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void avviaAnimazioneCamminata() {
        animazioneCamminata = new Transition() {
            {
                // Un ciclo completo di camminata dura 600 millisecondi
                setCycleDuration(Duration.millis(600));
                setInterpolator(Interpolator.LINEAR);
            }

            @Override
            protected void interpolate(double k) {
                // Calcola l'indice del frame da mostrare (da 0 a TOTALE_FRAME - 1)
                int index = Math.min((int) Math.floor(k * TOTALE_FRAME), TOTALE_FRAME - 1);

                // Sposta la finestra di taglio lungo l'asse X dello spritesheet
                int x = index * LARGHEZZA_FRAME;

                imageView.setViewport(new Rectangle2D(x, 0, LARGHEZZA_FRAME, ALTEZZA_FRAME));
            }
        };

        // Ripeti la camminata all'infinito finché il nemico è vivo
        animazioneCamminata.setCycleCount(Animation.INDEFINITE);
        animazioneCamminata.play();
    }

    // --- GESTIONE PAUSA/RIPRESA ANIMAIONE ---
    public void pausaAnimazione() {
        if (animazioneCamminata != null) {
            animazioneCamminata.pause();
        }
    }

    public void riprendiAnimazione() {
        if (animazioneCamminata != null) {
            animazioneCamminata.play();
        }
    }

    public void muoviASinistra() {
        this.setLayoutX(this.getLayoutX() - velocita);
    }

    public boolean subisciDanno(int danno) {
        this.salute -= danno;
        System.out.println("Nemico in riga " + getRigaAttuale() + " ha subito " + danno + " danni. HP rimanenti: " + salute);

        // Un piccolo effetto visivo quando viene colpito (lampeggia di rosso)
        this.setOpacity(0.6);
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.millis(100));
        pause.setOnFinished(e -> this.setOpacity(1.0));
        pause.play();

        return this.salute <= 0;
    }

    // GETTER E SETTER
    public int getRigaAttuale() { return rigaAttuale; }
    public int getSalute() { return salute; }
    public long getUltimoAttaccoTime() { return ultimoAttaccoTime; }
    public void setUltimoAttaccoTime(long time) { this.ultimoAttaccoTime = time; }
}