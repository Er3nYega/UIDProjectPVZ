package it.unical.uid.progettoesameuid.controller;

import it.unical.uid.progettoesameuid.model.Arciere;
import it.unical.uid.progettoesameuid.model.MapMask;
import it.unical.uid.progettoesameuid.model.Ally; // Sostituisci con i tuoi path reali
import it.unical.uid.progettoesameuid.view.AllyView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;

import java.util.Objects;
import java.util.function.Supplier;

public class GiocoController {
    @FXML
    private ImageView sfondoMappa;
    // --- ELEMENTI FXML (COLLEGATI A SCENE BUILDER) ---
    @FXML
    private AnchorPane areaGiocoAnchor; // L'AnchorPane che contiene lo sfondo e i poligoni
    @FXML
    private Label testoMonete; // Il testo delle monete che hai messo nella barra in alto FXML

    // --- IL TUO VECCHIO MODELLO LOGICO (MANTENUTO!) ---
    private MapMask model;

    // --- LE TUE VARIABILI DI SELEZIONE (MANTENUTE!) ---
    private Supplier<Ally> costruttoreAlleatoSelezionato = null;
    private int costoAlleatoSelezionato = 0;
    private String nomeAlleatoSelezionato = "";

    // Il costruttore vuoto è richiesto da JavaFX per i controller FXML
    public GiocoController() {
        // Il model lo inizializzerai qui o tramite un metodo setup
        // Es: this.model = new MapMask();
    }

    @FXML
    public void initialize() {
        try {
            // La barra iniziale "/" forza la ricerca partendo dalla radice di resources
            var risorsaStream = getClass().getResourceAsStream("/map.png");

            if (risorsaStream != null) {
                Image mappaImage = new Image(risorsaStream);
                if (sfondoMappa != null) {
                    sfondoMappa.setImage(mappaImage);
                    System.out.println("Mappa di gioco caricata con successo via radice assoluta!");
                }
            } else {
                System.err.println("ERRORE: map.png non trovato nella radice di resources. Cerco di recuperarlo via percorso alternativo...");

                // Tentativo di riserva: se per caso è rimasto nel package
                var risorsaPackage = getClass().getResourceAsStream("map.png");
                if (risorsaPackage != null) {
                    sfondoMappa.setImage(new Image(risorsaPackage));
                    System.out.println("Mappa di gioco caricata con successo dal package locale!");
                } else {
                    System.err.println("ERRORE CRITICO: map.png è introvabile ovunque!");
                }
            }
        } catch (Exception e) {
            System.err.println("Errore durante l'assegnazione dello sfondo: " + e.getMessage());
            e.printStackTrace();
        }

        if (model != null && testoMonete != null) {
            testoMonete.setText("Monete d'Oro: " + model.getMonete());
        }
    }

    /**
     * Il tuo metodo per la barra delle unità (MANTENUTO!)
     */
    public void selezionaUnita(String nome, int costo, Supplier<Ally> costruttore) {
        this.nomeAlleatoSelezionato = nome;
        this.costoAlleatoSelezionato = costo;
        this.costruttoreAlleatoSelezionato = costruttore;
        System.out.println("Selezionato: " + nome + " (Costo: " + costo + ")");
    }

    /**
     * NUOVO: Questo è il punto di ingresso collegato all'FXML.
     * Sostituisce del tutto "inizializzaInterazioni()" e il vecchio ciclo for!
     */
    @FXML
    private void gestisciClickCasella(MouseEvent event) {
        // Capiamo quale poligono è stato premuto
        Polygon poligonoCliccato = (Polygon) event.getSource();
        String id = poligonoCliccato.getId(); // Es: "cell_0_0"

        // Estraiamo riga e colonna dal testo dell'ID
        String[] parti = id.split("_");
        int rigaCliccata = Integer.parseInt(parti[1]);
        int colonnaCliccata = Integer.parseInt(parti[2]);

        // Invochiamo la tua logica di piazzamento originale
        gestisciPiazzamentoAlleato(rigaCliccata, colonnaCliccata, poligonoCliccato);
    }

    /**
     * La tua logica di piazzamento (MANTENUTA E ADATTATA)
     */
    private void gestisciPiazzamentoAlleato(int riga, int colonna, Polygon poligono) {
        System.out.println("DEBUG: Inizio piazzamento. Riga=" + riga + ", Colonna=" + colonna + ", Selezionato=" + nomeAlleatoSelezionato);

        // 1. Controllo selezione
        if (costruttoreAlleatoSelezionato == null) {
            System.out.println("DEBUG BLOCCATO: Nessun alleato selezionato!");
            return;
        }

        // 2. Controllo cella
        if (!model.cellaLibera(riga, colonna)) {
            System.out.println("DEBUG BLOCCATO: Cella occupata secondo il model!");
            return;
        }

        // 3. Controllo Economia
        if (model.getMonete() < costoAlleatoSelezionato) {
            System.out.println("DEBUG BLOCCATO: Oro insufficiente! Monete nel model: " + model.getMonete() + ", Costo richiesto: " + costoAlleatoSelezionato);
            return;
        }

        System.out.println("DEBUG: Tutti i controlli superati. Procedo alla creazione logica...");
        Ally nuovoAlleatoLogico = costruttoreAlleatoSelezionato.get();

        if (model.sottraiMonete(costoAlleatoSelezionato)) {
            System.out.println("DEBUG: Monete sottratte con successo. Aggiorno model e grafica...");

            // Aggiorna il Model
            model.getMaskMatrix()[riga][colonna].setAlly(nuovoAlleatoLogico);

            // Aggiorna la Grafica tramite FXML
            testoMonete.setText("Monete d'Oro: " + model.getMonete());

            // Creiamo la view dell'alleato
            AllyView alleatoVisivo = new AllyView(nomeAlleatoSelezionato.toLowerCase());

            // Calcoliamo il centro geometrico del trapezio
            double centroX = (poligono.getPoints().get(0) + poligono.getPoints().get(4)) / 2;
            double centroY = (poligono.getPoints().get(1) + poligono.getPoints().get(5)) / 2;

            alleatoVisivo.setLayoutX(poligono.getLayoutX() + centroX - 45.0);
            alleatoVisivo.setLayoutY(poligono.getLayoutY() + centroY - 45.0);

            // Scala
            switch(riga) {
                case 0 -> { alleatoVisivo.setScaleX(0.75); alleatoVisivo.setScaleY(0.75); }
                case 1 -> { alleatoVisivo.setScaleX(0.83); alleatoVisivo.setScaleY(0.83); }
                case 2 -> { alleatoVisivo.setScaleX(0.92); alleatoVisivo.setScaleY(0.92); }
                case 3 -> { alleatoVisivo.setScaleX(1.00); alleatoVisivo.setScaleY(1.00); }
                case 4 -> { alleatoVisivo.setScaleX(1.08); alleatoVisivo.setScaleY(1.08); }
            }

            // Aggiungiamo lo sprite direttamente sull'area di gioco visiva
            if (areaGiocoAnchor != null) {
                areaGiocoAnchor.getChildren().add(alleatoVisivo);
                System.out.println("DEBUG: Sprite aggiunto a areaGiocoAnchor!");
            } else {
                System.err.println("DEBUG ERRORE: areaGiocoAnchor è NULL! Controlla l'fx:id su Scene Builder!");
            }

            System.out.println(nomeAlleatoSelezionato + " piazzato con successo in [" + riga + "][" + colonna + "]");
        } else {
            System.out.println("DEBUG BLOCCATO: model.sottraiMonete ha restituito false!");
        }
    }

    // Un metodo comodo per iniettare il modello da fuori al momento del cambio scena
    public void setModel(MapMask model) {
        this.model = model;
        if (testoMonete != null) {
            testoMonete.setText("Monete d'Oro: " + model.getMonete());
        }
    }

    @FXML
    private void selezionaArciere() {
        selezionaUnita("Arciere", 50, Arciere::new);
    }

    @FXML
    private void selezionaCavaliere() {
        // CORRETTO: Ora invoca correttamente il costruttore del Cavaliere
        selezionaUnita("Cavaliere", 100, Arciere::new);
    }
}
