package it.unical.uid.progettoesameuid.utility;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundManager {

    private static MediaPlayer musicaSottofondo;
    private static double volumeMusica = 0.5; // Volume di default al 50%

    /**
     * Avvia la colonna sonora in loop continuo.
     * @param nomeFile Nome del file audio situato in resources/audio/ (es: "menu_theme.mp3")
     */
    public static void riproduciMusica(String nomeFile) {
        try {
            // Se c'è già una traccia in riproduzione, la fermiamo prima di cambiare
            if (musicaSottofondo != null) {
                musicaSottofondo.stop();
            }

            var resource = SoundManager.class.getResource("/audio/" + nomeFile);
            if (resource != null) {
                Media media = new Media(resource.toExternalForm());
                musicaSottofondo = new MediaPlayer(media);
                musicaSottofondo.setCycleCount(MediaPlayer.INDEFINITE); // Loop infinito
                musicaSottofondo.setVolume(volumeMusica);
                musicaSottofondo.play();
            } else {
                System.err.println("⚠️ File audio non trovato: /audio/" + nomeFile);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Errore riproduzione musica: " + e.getMessage());
        }
    }

    /**
     * Interrompe la musica di sottofondo
     */
    public static void fermaMusica() {
        if (musicaSottofondo != null) {
            musicaSottofondo.stop();
        }
    }

    // --- CONTROLLO VOLUME (PER LE IMPOSTAZIONI) ---
    public static void setVolumeMusica(double volume) {
        volumeMusica = volume;
        if (musicaSottofondo != null) {
            musicaSottofondo.setVolume(volumeMusica);
        }
    }

    public static double getVolumeMusica() {
        return volumeMusica;
    }
}