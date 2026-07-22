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

    public enum StatoNemico { CAMMINATA, ATTACCO }

    private final ImageView imageView;
    private int rigaAttuale;
    private double velocita = 0.4; // Velocità di avanzamento verso sinistra

    // --- PARAMETRI SPRITESHEET ---
    private static final int LARGHEZZA_FRAME = 200;
    private static final int ALTEZZA_FRAME = 200;
    private static final int TOTALE_FRAME = 8;
    private int salute = 100;

    private Transition animazioneCorrente;
    private long ultimoAttaccoTime = 0;

    private String nomeNemicoBase;
    private StatoNemico statoAttuale = StatoNemico.CAMMINATA;

    public EnemyView(String nomeNemico, int rigaIniziale) {
        this.rigaAttuale = rigaIniziale;
        this.imageView = new ImageView();

        // Estraiamo il nome base (es. se nomeNemico è "Skeleton_Walk", il base diventa "Skeleton")
        this.nomeNemicoBase = nomeNemico.contains("_") ? nomeNemico.split("_")[0] : nomeNemico;

        this.getChildren().add(imageView);

        caricaSpriteSheet(nomeNemicoBase + "_Walk");
        avviaAnimazione();
    }

    public void setStato(StatoNemico nuovoStato) {
        if (this.statoAttuale == nuovoStato) {
            return;
        }

        this.statoAttuale = nuovoStato;

        String nomeFileSprite = switch (nuovoStato) {
            case ATTACCO -> nomeNemicoBase + "_Attack";
            case CAMMINATA -> nomeNemicoBase + "_Walk";
        };

        caricaSpriteSheet(nomeFileSprite);
    }

    private void caricaSpriteSheet(String nomeFile) {
        try {
            String percorso = "/images/" + nomeFile + ".png";
            var stream = getClass().getResourceAsStream(percorso);

            if (stream != null) {
                Image spriteSheet = new Image(stream);
                imageView.setImage(spriteSheet);

                imageView.setViewport(new Rectangle2D(0, 0, LARGHEZZA_FRAME, ALTEZZA_FRAME));

                imageView.setFitWidth(LARGHEZZA_FRAME);
                imageView.setFitHeight(ALTEZZA_FRAME);
                imageView.setPreserveRatio(true);

                avviaAnimazione();
            } else {
                System.err.println("ERRORE: Impossibile trovare lo spritesheet: " + percorso);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void avviaAnimazione() {
        if (animazioneCorrente != null) {
            animazioneCorrente.stop();
        }

        animazioneCorrente = new Transition() {
            {
                setCycleDuration(Duration.millis(600));
                setInterpolator(Interpolator.LINEAR);
            }

            @Override
            protected void interpolate(double k) {
                int index = Math.min((int) Math.floor(k * TOTALE_FRAME), TOTALE_FRAME - 1);
                int x = index * LARGHEZZA_FRAME;
                imageView.setViewport(new Rectangle2D(x, 0, LARGHEZZA_FRAME, ALTEZZA_FRAME));
            }
        };

        animazioneCorrente.setCycleCount(Animation.INDEFINITE);
        animazioneCorrente.play();
    }

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

    public void muoviASinistra() {
        this.setLayoutX(this.getLayoutX() - velocita);
    }

    public boolean subisciDanno(int danno) {
        this.salute -= danno;
        System.out.println("Nemico in riga " + getRigaAttuale() + " ha subito " + danno + " danni. HP rimanenti: " + salute);

        this.setOpacity(0.6);
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.millis(100));
        pause.setOnFinished(e -> this.setOpacity(1.0));
        pause.play();

        return isDead();
    }

    public boolean isDead() {
        return this.salute <= 0;
    }

    // ==========================================
    // 🎯 GETTER E SETTER COMPATIBILI CON DB E CONTROLLER
    // ==========================================

    public String getTipoNemico() {
        return nomeNemicoBase; // Restituisce es: "Skeleton", "Zombie", "Orco"
    }

    public int getHpAttuali() {
        return salute;
    }

    public void setHp(int nuoviHp) {
        this.salute = nuoviHp;
    }

    public int getRigaAttuale() { return rigaAttuale; }
    public int getSalute() { return salute; }
    public long getUltimoAttaccoTime() { return ultimoAttaccoTime; }
    public void setUltimoAttaccoTime(long time) { this.ultimoAttaccoTime = time; }
    public StatoNemico getStatoAttuale() { return statoAttuale; }
}