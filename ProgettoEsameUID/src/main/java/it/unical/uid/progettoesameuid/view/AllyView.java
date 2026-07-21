package it.unical.uid.progettoesameuid.view;

import it.unical.uid.progettoesameuid.model.Ally;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class AllyView extends StackPane {

    public enum StatoAlleato { IDLE, ATTACCO }

    private final ImageView imageView;
    private StatoAlleato statoAttuale = null;

    private Image spriteSheetIdle;
    private Image spriteSheetAttacco;

    private Transition animazioneCorrente;
    private final String nomeAlleato;

    private int riga;
    private int colonna;

    // Flag per evitare che scocchi più frecce nello stesso ciclo di animazione
    private boolean colpoScoccatoInQuestoCiclo = false;

    private Ally modelloAlleato; // Riferimento al Model dell'alleato

    // Interfaccia/Callback per notificare il Controller quando scoccare
    public interface OnAttackFrameListener {
        void onAttackRelease();
    }
    private OnAttackFrameListener attackListener;

    public void setOnAttackReleaseListener(OnAttackFrameListener listener) {
        this.attackListener = listener;
    }

    public AllyView(String nomeAlleato) {
        this.nomeAlleato = nomeAlleato.toLowerCase();
        this.imageView = new ImageView();

        // 1. DIMENSIONI FISSE PER LO STACKPANE (200x200)
        this.setPrefSize(200, 200);
        this.setMaxSize(200, 200);
        this.setMinSize(200, 200);

        // 2. Ancoraggio dei piedi in basso al centro per evitare scatti tra gli stati
        StackPane.setAlignment(imageView, javafx.geometry.Pos.BOTTOM_CENTER);

        try {
            var streamIdle = getClass().getResourceAsStream("/images/" + this.nomeAlleato + "_idle.png");
            var streamAttacco = getClass().getResourceAsStream("/images/" + this.nomeAlleato + "_attacco.png");

            if (streamIdle != null) spriteSheetIdle = new Image(streamIdle);
            if (streamAttacco != null) spriteSheetAttacco = new Image(streamAttacco);

            this.getChildren().add(imageView);
            this.toFront();

            impostaStato(StatoAlleato.IDLE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void impostaStato(StatoAlleato nuovoStato) {
        if (this.statoAttuale == nuovoStato) return;

        this.statoAttuale = nuovoStato;

        if (animazioneCorrente != null) {
            animazioneCorrente.stop();
        }

        Image immagineDaUsare = (nuovoStato == StatoAlleato.ATTACCO && spriteSheetAttacco != null)
                ? spriteSheetAttacco
                : spriteSheetIdle;

        if (immagineDaUsare != null) {
            imageView.setImage(immagineDaUsare);

            double altezzaFrame = immagineDaUsare.getHeight();
            double larghezzaSingoloFrame = altezzaFrame;
            int totaleFrame = (int) Math.round(immagineDaUsare.getWidth() / larghezzaSingoloFrame);
            if (totaleFrame <= 0) totaleFrame = 1;

            imageView.setViewport(new Rectangle2D(0, 0, larghezzaSingoloFrame, altezzaFrame));

            // AGGIORNIAMO L'IMAGEVIEW A 200x200
            imageView.setFitWidth(200);
            imageView.setFitHeight(200);
            imageView.setPreserveRatio(true);

            // Durata ciclo animazione
            double durata = (nuovoStato == StatoAlleato.ATTACCO) ? 1200 : 800;
            avviaLoopAnimazione(totaleFrame, larghezzaSingoloFrame, altezzaFrame, durata, nuovoStato == StatoAlleato.ATTACCO);
        }
    }

    private void avviaLoopAnimazione(int totaleFrame, double frameWidth, double frameHeight, double durataMillis, boolean eAttacco) {
        colpoScoccatoInQuestoCiclo = false;

        animazioneCorrente = new Transition() {
            {
                setCycleDuration(Duration.millis(durataMillis));
                setInterpolator(Interpolator.LINEAR);
            }

            @Override
            protected void interpolate(double k) {
                int index = Math.min((int) Math.floor(k * totaleFrame), totaleFrame - 1);

                // Resettiamo il flag all'inizio del ciclo di animazione (frame 0)
                if (index == 0) {
                    colpoScoccatoInQuestoCiclo = false;
                }

                // FRAME DEL RILASCIO
                int frameRilascio = Math.max(1, totaleFrame - 2);

                if (eAttacco && index == frameRilascio && !colpoScoccatoInQuestoCiclo) {
                    colpoScoccatoInQuestoCiclo = true;
                    if (attackListener != null) {
                        attackListener.onAttackRelease(); // Notifica il Controller!
                    }
                }

                imageView.setViewport(new Rectangle2D(index * frameWidth, 0, frameWidth, frameHeight));
            }
        };

        animazioneCorrente.setCycleCount(Animation.INDEFINITE);
        animazioneCorrente.play();
    }

    public void setPosizioneGriglia(int riga, int colonna) {
        this.riga = riga;
        this.colonna = colonna;
    }

    // --- GESTIONE PAUSA ANIMAIONE (Utilizza animazioneCorrente) ---
    public void pausaAnimazione() {
        if (animazioneCorrente != null) {
            animazioneCorrente.pause();
        }
    }

    public void riprendiAnimazione() {
        if (animazioneCorrente != null) {
            animazioneCorrente.play();
        }
    }

    // --- GETTER E SETTER ---
    public int getRiga() { return riga; }
    public int getColonna() { return colonna; }
    public String getNomeAlleato() { return nomeAlleato; }
    public StatoAlleato getStatoAttuale() { return statoAttuale; }

    public void setModelloAlleato(Ally modello) {
        this.modelloAlleato = modello;
    }

    public Ally getModelloAlleato() {
        return modelloAlleato;
    }
}