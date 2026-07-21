package it.unical.uid.progettoesameuid.controller;

import it.unical.uid.progettoesameuid.HelloApplication;
import it.unical.uid.progettoesameuid.model.*;
import it.unical.uid.progettoesameuid.view.AllyView;
import it.unical.uid.progettoesameuid.view.BulletView;
import it.unical.uid.progettoesameuid.view.EnemyView;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polygon;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public class GiocoController {
    @FXML private ImageView sfondoMappa;
    @FXML private AnchorPane areaGiocoAnchor;
    @FXML private Label testoMonete;

    // --- POPUP DRAFTING E CARTE ---
    @FXML private VBox popupSceltaAlleato;
    @FXML private ImageView imgCard1, imgCard2;
    @FXML private Label nomeCard1, nomeCard2, statsCard1, statsCard2;

    // --- LE 6 IMAGEVIEW DEI SEI SLOT (Iniettate singolarmente da FXML) ---
    @FXML private ImageView slotImg_0;
    @FXML private ImageView slotImg_1;
    @FXML private ImageView slotImg_2;
    @FXML private ImageView slotImg_3;
    @FXML private ImageView slotImg_4;
    @FXML private ImageView slotImg_5;


    @FXML private VBox card2Container;

    @FXML private VBox menuPausa;
    private boolean inPausa = false;

    // La lista che raccoglie le 6 ImageView
    private List<ImageView> immaginiSlotBarra;

    private MapMask model;

    private Supplier<Ally> costruttoreAlleatoSelezionato = null;
    private int costoAlleatoSelezionato = 0;
    private String nomeAlleatoSelezionato = "";

    // LISTE DIVERSE E MAPPE PER COMBATTIMENTO ED ONDATE
    private final List<Supplier<Ally>> alleatiNegliSlot = new ArrayList<>();
    private final List<AllyView> alleatiInGioco = new ArrayList<>();
    private final List<BulletView> proiettiliAttivi = new ArrayList<>();
    private final List<EnemyView> nemiciAttivi = new ArrayList<>();
    private final Map<AllyView, Long> ultimoAttaccoMap = new HashMap<>();

    private int ondataAttuale = 1;
    private final int TOTALE_ONDATE = 10;
    private int nemiciRimanentiOndata = 5;
    private long ultimoSpawnTime = 0;
    private AnimationTimer gameLoop;

    @FXML
    public void initialize() {
        // 1. FONDAMENTALE: INIZIALIZZIAMO LA LISTA PER PRIMA COSA!
        immaginiSlotBarra = List.of(slotImg_0, slotImg_1, slotImg_2, slotImg_3, slotImg_4, slotImg_5);

        if (this.model == null) {
            this.model = new MapMask();
        }

        // 2. INIZIALIZZAZIONE SLOT DI PARTENZA (Slot 0 = Arciere, Slot 1 = Cavaliere)
        alleatiNegliSlot.add(Arciere::new);
        alleatiNegliSlot.add(Cavaliere::new);

        // Carichiamo le prime due icone
        aggiornaIconaSlot(0, "Arciere");
        aggiornaIconaSlot(1, "Cavaliere");

        // Impostiamo la trasparenza ai click del mouse per tutte le icone degli slot
        for (ImageView imgView : immaginiSlotBarra) {
            if (imgView != null) {
                imgView.setMouseTransparent(true);
            }
        }

        // CARICAMENTO MAPPA
        try {
            var risorsaStream = getClass().getResourceAsStream("/map.png");
            if (risorsaStream != null) {
                Image mappaImage = new Image(risorsaStream);
                if (sfondoMappa != null) {
                    sfondoMappa.setImage(mappaImage);
                }
            }
        } catch (Exception e) {
            System.err.println("Errore caricamento mappa: " + e.getMessage());
        }

        if (model != null && testoMonete != null) {
            testoMonete.setText("MONETE: " + model.getMonete());
        }

        areaGiocoAnchor.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                        togglePausa();
                        event.consume(); // Evita conflitti
                    }
                });
            }
        });
        // AVVIO GAME LOOP
        avviaOndata();
    }

    public void togglePausa() {
        if (inPausa) {
            riprendiGioco();
        } else {
            apriPausa();
        }
    }

    @FXML
    public void apriPausa() {
        inPausa = true;
        if (gameLoop != null) {
            gameLoop.stop();
        }

        // Mette in pausa le animazioni
        for (AllyView alleato : alleatiInGioco) { alleato.pausaAnimazione(); }
        for (EnemyView nemico : nemiciAttivi) { nemico.pausaAnimazione(); }

        // CENTRAMENTO PERFETTO DEL MENU SU LARGHEZZA 1920 E ALTEZZA 1080
        if (menuPausa != null) {
            menuPausa.setLayoutX((1920.0 - menuPausa.getMinWidth()) / 2.0);
            menuPausa.setLayoutY((1080.0 - 450.0) / 2.0);
        }

        menuPausa.setVisible(true);
        menuPausa.toFront();
    }

    @FXML
    public void riprendiGioco() {
        inPausa = false;
        menuPausa.setVisible(false);

        // 1. Fai ripartire le animazioni visive
        for (AllyView alleato : alleatiInGioco) {
            alleato.riprendiAnimazione();
        }

        for (EnemyView nemico : nemiciAttivi) {
            nemico.riprendiAnimazione();
        }

        // 2. Fai ripartire il game loop
        if (gameLoop != null) {
            gameLoop.start();
        }
    }
    @FXML
    private void salvaPartita() {
        System.out.println("💾 Partita salvata con successo! (Ondata: " + ondataAttuale + ", Monete: " + model.getMonete() + ")");
        // Qui puoi chiamare una funzione di serializzazione/salvataggio del Model se serve
    }

    @FXML
    private void apriSettings() {
        System.out.println("⚙️ Apertura impostazioni audio/grafica...");
        // Puoi mostrare un sottomenu per volume/effetti sonori
    }

    @FXML
    private void esciSenzaSalvare() {
        System.out.println("🚪 Uscita al menu principale senza salvare...");
        if (gameLoop != null) {
            gameLoop.stop();
        }

        try {
            javafx.stage.Stage stage = (javafx.stage.Stage) areaGiocoAnchor.getScene().getWindow();

            // Carichiamo l'FXML del Menu
            var resource = getClass().getResource("/it/unical/uid/progettoesameuid/MenuPrincipale.fxml");
            if (resource == null) resource = getClass().getResource("/MenuPrincipale.fxml");

            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(resource);
            javafx.scene.Parent root = loader.load();

            // Colleghiamo il MenuController alla finestra principale
            MenuController menuController = loader.getController();
            if (menuController != null) {
                // Se hai un riferimento a HelloApplication o un gestore, riassegnalo
                // Esempio: menuController.setMainApp(app);
            }

            // Usiamo la nostra vista scalata per evitare problemi di risoluzione
            HelloApplication app = new HelloApplication();
            app.start(stage);

        } catch (Exception e) {
            System.err.println("Errore nel caricamento del Menu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void selezionaUnita(String nome, int costo, Supplier<Ally> costruttore) {
        this.nomeAlleatoSelezionato = nome;
        this.costoAlleatoSelezionato = costo;
        this.costruttoreAlleatoSelezionato = costruttore;
        System.out.println("Selezionato: " + nome + " (Costo: " + costo + ")");
    }

    @FXML
    private void gestisciClickCasella(MouseEvent event) {
        // 🛑 SE SIAMO IN PAUSA O IL POPUP SELEZIONE È APERTO, BLOCCA I CLICK SULLA GRIGLIA
        if (inPausa || (popupSceltaAlleato != null && popupSceltaAlleato.isVisible())) {
            System.out.println("⚠️ Impossibile piazzare unità: gioco in pausa o popup attivo!");
            return;
        }

        Polygon poligonoCliccato = (Polygon) event.getSource();
        String id = poligonoCliccato.getId();

        String[] parti = id.split("_");
        int rigaCliccata = Integer.parseInt(parti[1]);
        int colonnaCliccata = Integer.parseInt(parti[2]);

        gestisciPiazzamentoAlleato(rigaCliccata, colonnaCliccata, poligonoCliccato);
    }

    private void gestisciPiazzamentoAlleato(int riga, int colonna, Polygon poligono) {
        System.out.println("DEBUG: Inizio piazzamento. Riga=" + riga + ", Colonna=" + colonna + ", Selezionato=" + nomeAlleatoSelezionato);

        if (costruttoreAlleatoSelezionato == null) {
            System.out.println("DEBUG BLOCCATO: Nessun alleato selezionato!");
            return;
        }

        if (model != null && !model.cellaLibera(riga, colonna)) {
            System.out.println("DEBUG BLOCCATO: Cella occupata secondo il model!");
            return;
        }

        if (model != null && model.getMonete() < costoAlleatoSelezionato) {
            System.out.println("DEBUG BLOCCATO: Oro insufficiente!");
            return;
        }

        Ally nuovoAlleatoLogico = costruttoreAlleatoSelezionato.get();

        if (model == null || model.sottraiMonete(costoAlleatoSelezionato)) {

            if (model != null) {
                model.getMaskMatrix()[riga][colonna].setAlly(nuovoAlleatoLogico);
                testoMonete.setText("Monete d'Oro: " + model.getMonete());
            }

            AllyView alleatoVisivo = new AllyView(nomeAlleatoSelezionato.toLowerCase());

            // IMPONIAMO SUBITO RIGA, COLONNA E RIFERIMENTO LOGICO
            alleatoVisivo.setPosizioneGriglia(riga, colonna);
            alleatoVisivo.setModelloAlleato(nuovoAlleatoLogico);

            // Calcoliamo il centro del poligono
            double centroX = (poligono.getPoints().get(0) + poligono.getPoints().get(4)) / 2;
            double centroY = (poligono.getPoints().get(1) + poligono.getPoints().get(5)) / 2;

            // Offset per centrare un riquadro 200x200
            double offsetX = 100.0;
            double offsetY = 100.0;

            alleatoVisivo.setLayoutX(poligono.getLayoutX() + centroX - offsetX);
            alleatoVisivo.setLayoutY(poligono.getLayoutY() + centroY - offsetY);

            switch(riga) {
                case 0 -> { alleatoVisivo.setScaleX(0.75); alleatoVisivo.setScaleY(0.75); }
                case 1 -> { alleatoVisivo.setScaleX(0.83); alleatoVisivo.setScaleY(0.83); }
                case 2 -> { alleatoVisivo.setScaleX(0.92); alleatoVisivo.setScaleY(0.92); }
                case 3 -> { alleatoVisivo.setScaleX(1.00); alleatoVisivo.setScaleY(1.00); }
                case 4 -> { alleatoVisivo.setScaleX(1.08); alleatoVisivo.setScaleY(1.08); }
            }

            if (alleatoVisivo.getNomeAlleato().contains("arciere") || alleatoVisivo.getNomeAlleato().contains("stregone")) {
                // Evento di attacco a distanza al completamento della relativa animazione
                alleatoVisivo.setOnAttackReleaseListener(() -> {
                    long nemiciSuRiga = nemiciAttivi.stream()
                            .filter(n -> n.getRigaAttuale() == alleatoVisivo.getRiga())
                            .count();

                    if (nemiciSuRiga > 0) {
                        double startX = alleatoVisivo.getLayoutX() + 120.0;
                        double startY = alleatoVisivo.getLayoutY() + 75.0;

                        // Estrazione del danno con le API aggiornate (getDamage)
                        int dannoReale = 20; // Fallback
                        if (alleatoVisivo.getModelloAlleato() != null) {
                            dannoReale = alleatoVisivo.getModelloAlleato().getDamage();
                        }

                        BulletView freccia = new BulletView(alleatoVisivo.getNomeAlleato(), startX, startY, alleatoVisivo.getRiga(), dannoReale);
                        freccia.toFront();

                        proiettiliAttivi.add(freccia);
                        areaGiocoAnchor.getChildren().add(freccia);
                    }
                });
            }

            if (areaGiocoAnchor != null) {
                areaGiocoAnchor.getChildren().add(alleatoVisivo);
                alleatoVisivo.toFront();

                System.out.println("DEBUG: Sprite aggiunto e portato in front!");
                alleatiInGioco.add(alleatoVisivo);
            } else {
                System.err.println("DEBUG ERRORE: areaGiocoAnchor è NULL!");
            }
            System.out.println(nomeAlleatoSelezionato + " piazzato con successo in [" + riga + "][" + colonna + "]");
        }
    }

    public void setModel(MapMask model) {
        this.model = model;
        if (testoMonete != null) {
            testoMonete.setText("Monete d'Oro: " + model.getMonete());
        }
    }

    // --- SELEZIONE UNITA (METODI FXML) ---

    @FXML
    private void gestisciClickSlotBarra(javafx.event.ActionEvent event) {
        javafx.scene.control.Button btn = (javafx.scene.control.Button) event.getSource();
        String id = btn.getId(); // es. "slotBtn_2"
        int indexSlot = Integer.parseInt(id.split("_")[1]);

        if (indexSlot < alleatiNegliSlot.size() && alleatiNegliSlot.get(indexSlot) != null) {
            Supplier<Ally> costruttore = alleatiNegliSlot.get(indexSlot);
            Ally esempio = costruttore.get(); // Istanza per leggere dati reali

            String nome = esempio.getClass().getSimpleName();
            int costo = esempio.getCost();

            selezionaUnita(nome, costo, costruttore);
            System.out.println("✅ Selezionato per il piazzamento dallo Slot " + (indexSlot + 1) + ": " + nome);
        } else {
            System.out.println("⚠️ Lo Slot " + (indexSlot + 1) + " è ancora vuoto!");
        }
    }


    // --- LOGICA DI COMBATTIMENTO E ANIMAZIONI ---

    private void aggiornaLogicaCombattimento(long now) {
        for (AllyView alleato : alleatiInGioco) {
            int rigaAlleato = alleato.getRiga();

            List<EnemyView> nemiciSuRiga = nemiciAttivi.stream()
                    .filter(n -> n.getRigaAttuale() == rigaAlleato)
                    .toList();

            String nomeUnita = alleato.getNomeAlleato().toLowerCase();

            // Unità a distanza (Arciere, Stregone)
            if (nomeUnita.contains("arciere") || nomeUnita.contains("stregone")) {
                if (!nemiciSuRiga.isEmpty()) {
                    alleato.impostaStato(AllyView.StatoAlleato.ATTACCO);
                } else {
                    alleato.impostaStato(AllyView.StatoAlleato.IDLE);
                }

                // Unità da mischia (Soldato, Spadaccino, Alabardiere, Cavaliere)
            } else {
                boolean nemicoVicino = nemiciSuRiga.stream().anyMatch(n -> {
                    double distanzaX = n.getLayoutX() - alleato.getLayoutX();
                    return distanzaX > -30.0 && distanzaX < 150.0;
                });

                if (nemicoVicino) {
                    alleato.impostaStato(AllyView.StatoAlleato.ATTACCO);

                    long ultimoAttacco = ultimoAttaccoMap.getOrDefault(alleato, 0L);
                    if (now - ultimoAttacco > 1_000_000_000L) {
                        EnemyView target = nemiciSuRiga.get(0);
                        int dannoAlleato = (alleato.getModelloAlleato() != null)
                                ? alleato.getModelloAlleato().getDamage() : 25;

                        boolean morto = target.subisciDanno(dannoAlleato);
                        if (morto) {
                            areaGiocoAnchor.getChildren().remove(target);
                            nemiciAttivi.remove(target);
                            System.out.println("⚔️ " + nomeUnita + " ha eliminato un nemico!");
                        }
                        ultimoAttaccoMap.put(alleato, now);
                    }
                } else {
                    alleato.impostaStato(AllyView.StatoAlleato.IDLE);
                }
            }
        }

        aggiornaProiettili();
    }

    private void aggiornaProiettili() {
        for (int i = proiettiliAttivi.size() - 1; i >= 0; i--) {
            BulletView p = proiettiliAttivi.get(i);
            p.muoviADestra();

            for (int j = nemiciAttivi.size() - 1; j >= 0; j--) {
                EnemyView n = nemiciAttivi.get(j);

                if (p.getRiga() == n.getRigaAttuale() && p.getBoundsInParent().intersects(n.getBoundsInParent())) {
                    boolean morto = n.subisciDanno(p.getDanno());

                    areaGiocoAnchor.getChildren().remove(p);
                    proiettiliAttivi.remove(i);

                    if (morto) {
                        areaGiocoAnchor.getChildren().remove(n);
                        nemiciAttivi.remove(j);
                        System.out.println("💀 Nemico eliminato!");
                    }

                    break;
                }
            }

            // 🎯 RIMozione PROIETTILE PRIMA DEL BORDO MAPPA (Evita di espandere l'AnchorPane)
            if (p.getLayoutX() > 1850.0) {
                areaGiocoAnchor.getChildren().remove(p);
                proiettiliAttivi.remove(i);
            }
        }
    }
    // --- GESTIONE ONDATE E SPAWN ---

    public void avviaOndata() {
        System.out.println("=== AVVIO ONDATA " + ondataAttuale + " ===");

        if (gameLoop != null) {
            gameLoop.stop();
        }

        nemiciRimanentiOndata = 5 + (ondataAttuale - 1) * 3;
        ultimoSpawnTime = System.nanoTime();

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (nemiciRimanentiOndata > 0 && (now - ultimoSpawnTime > 3_000_000_000L)) {
                    spawnaNemicoCasual();
                    ultimoSpawnTime = now;
                    nemiciRimanentiOndata--;
                }

                aggiornaPosizioneNemici();
                aggiornaLogicaCombattimento(now);

                if (nemiciRimanentiOndata == 0 && nemiciAttivi.isEmpty()) {
                    gameLoop.stop();
                    completaOndata();
                }
            }
        };

        gameLoop.start();
    }

    private void spawnaNemicoCasual() {
        int rigaCasuale = ThreadLocalRandom.current().nextInt(0, 5);
        int colonnaSpawn = 8;

        String idPoligono = "cell_" + rigaCasuale + "_" + colonnaSpawn;
        Polygon poligonoTarget = (Polygon) areaGiocoAnchor.lookup("#" + idPoligono);

        if (poligonoTarget != null) {
            EnemyView nemico = new EnemyView("Skeleton_Walk", rigaCasuale);

            double centroX = (poligonoTarget.getPoints().get(0) + poligonoTarget.getPoints().get(4)) / 2;
            double centroY = (poligonoTarget.getPoints().get(1) + poligonoTarget.getPoints().get(5)) / 2;

            double offsetX = 45.0;
            double offsetY = 100.0;

            nemico.setLayoutX(poligonoTarget.getLayoutX() + centroX - offsetX);
            nemico.setLayoutY(poligonoTarget.getLayoutY() + centroY - offsetY);

            switch(rigaCasuale) {
                case 0 -> { nemico.setScaleX(0.75); nemico.setScaleY(0.75); }
                case 1 -> { nemico.setScaleX(0.83); nemico.setScaleY(0.83); }
                case 2 -> { nemico.setScaleX(0.92); nemico.setScaleY(0.92); }
                case 3 -> { nemico.setScaleX(1.00); nemico.setScaleY(1.00); }
                case 4 -> { nemico.setScaleX(1.08); nemico.setScaleY(1.08); }
            }

            areaGiocoAnchor.getChildren().add(nemico);
            nemiciAttivi.add(nemico);

            System.out.println("Nemico spawnato in riga " + rigaCasuale);
        }
    }

    private void aggiornaPosizioneNemici() {
        long tempoAttuale = System.nanoTime();

        for (int i = nemiciAttivi.size() - 1; i >= 0; i--) {
            EnemyView nemico = nemiciAttivi.get(i);
            boolean bloccatoDaAlleato = false;

            for (int j = alleatiInGioco.size() - 1; j >= 0; j--) {
                AllyView alleatoVisivo = alleatiInGioco.get(j);

                if (alleatoVisivo.getRiga() == nemico.getRigaAttuale()) {
                    double distanzaX = nemico.getLayoutX() - alleatoVisivo.getLayoutX();

                    if (distanzaX > -20.0 && distanzaX < 80.0) {
                        bloccatoDaAlleato = true;

                        if (tempoAttuale - nemico.getUltimoAttaccoTime() > 1_200_000_000L) {

                            if (alleatoVisivo.getModelloAlleato() != null) {
                                // Subisce danno usando beDamaged e isDead di Entity
                                Ally modello = alleatoVisivo.getModelloAlleato();
                                modello.beDamaged(20);

                                alleatoVisivo.setOpacity(0.5);
                                javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.millis(150));
                                pause.setOnFinished(e -> alleatoVisivo.setOpacity(1.0));
                                pause.play();

                                if (modello.isDead()) {
                                    System.out.println("💀 L'alleato in [" + alleatoVisivo.getRiga() + "][" + alleatoVisivo.getColonna() + "] è stato distrutto!");

                                    int riga = alleatoVisivo.getRiga();
                                    int colonna = alleatoVisivo.getColonna();

                                    // Libera la casella nel Model
                                    if (model != null && model.getMaskMatrix() != null) {
                                        model.getMaskMatrix()[riga][colonna].setAlly(null);
                                    }

                                    areaGiocoAnchor.getChildren().remove(alleatoVisivo);
                                    alleatiInGioco.remove(j);
                                }
                            }

                            nemico.setUltimoAttaccoTime(tempoAttuale);
                        }
                        break;
                    }
                }
            }

            if (!bloccatoDaAlleato) {
                nemico.muoviASinistra();
            }

            if (nemico.getLayoutX() < 350) {
                System.out.println("GAME OVER: I nemici hanno superato le difese!");
                if (gameLoop != null) gameLoop.stop();
            }
        }
    }

    private void completaOndata() {
        System.out.println("🎉 ONDATA " + ondataAttuale + " COMPLETATA!");

        // 1. Fermiamo il game loop se attivo
        if (gameLoop != null) {
            gameLoop.stop();
        }

        // 2. Puliamo le frecce rimaste in volo
        for (BulletView p : proiettiliAttivi) {
            areaGiocoAnchor.getChildren().remove(p);
        }
        proiettiliAttivi.clear();

        // 3. Riportiamo gli alleati in posizione IDLE
        for (AllyView alleato : alleatiInGioco) {
            alleato.impostaStato(AllyView.StatoAlleato.IDLE);
        }

        if (ondataAttuale < TOTALE_ONDATE) {
            // Regalo monete
            if (model != null) {
                model.addMonete(50);
                if (testoMonete != null) {
                    testoMonete.setText("MONETE: " + model.getMonete());
                }
            }

            // MOSTRIAAMO IL POPUP: il gioco si "congela" finché il giocatore non recluta!
            mostraPopupSceltaAlleato();

        } else {
            System.out.println("🏆 VITTORIA FINALE! Hai completato tutte le 10 ondate!");
        }
    }

    // --- GESTIONE DEGLI SLOT BARRA E POPUP ---
  // Registra chi c'è nei 6 slot

    private Ally opzione1, opzione2; // Le due opzioni estratte casualmente
    private int slotDaSostituireIndex = -1; // Gestisce la sostituzione se pieno

    // Registro di tutti i 10 tipi di alleati con i loro costruttori
    private final List<Supplier<Ally>> poolTuttiAlleati = List.of(
            Alabardiere::new, Arciere::new, Cavaliere::new, Horseman::new,
            Prete::new, Sacerdote::new, Soldato::new, Spadaccino::new,
            Stregone::new, Templare::new
    );

    /**
     * Genera il pop-up con 2 carte casuali
     */
    public void mostraPopupSceltaAlleato() {
        // 1. Raccogliamo i nomi dei personaggi attualmente posseduti negli slot
        Set<String> nomiPosseduti = new HashSet<>();
        for (Supplier<Ally> supplier : alleatiNegliSlot) {
            if (supplier != null) {
                nomiPosseduti.add(supplier.get().getClass().getSimpleName());
            }
        }

        // 2. Filtriamo il pool mantenendo solo gli alleati NON posseduti
        List<Supplier<Ally>> poolDisponibili = poolTuttiAlleati.stream()
                .filter(supplier -> !nomiPosseduti.contains(supplier.get().getClass().getSimpleName()))
                .toList();

        // 3. Estrazione in base alla disponibilità residua
        if (poolDisponibili.size() >= 2) {
            int idx1 = ThreadLocalRandom.current().nextInt(poolDisponibili.size());
            int idx2;
            do {
                idx2 = ThreadLocalRandom.current().nextInt(poolDisponibili.size());
            } while (idx1 == idx2);

            opzione1 = poolDisponibili.get(idx1).get();
            opzione2 = poolDisponibili.get(idx2).get();

            impostaDatiCarta(opzione1, imgCard1, nomeCard1, statsCard1);
            impostaDatiCarta(opzione2, imgCard2, nomeCard2, statsCard2);

            popupSceltaAlleato.setVisible(true);
            popupSceltaAlleato.toFront();

        } else if (poolDisponibili.size() == 1) {
            // Se rimanesse solo 1 personaggio non posseduto
            opzione1 = poolDisponibili.get(0).get();
            impostaDatiCarta(opzione1, imgCard1, nomeCard1, statsCard1);

            // Nascondiamo o disabilitiamo la seconda carta se non ce ne sono altre disponibili
            card2Container.setVisible(false);
            popupSceltaAlleato.setVisible(true);
            popupSceltaAlleato.toFront();
        } else {
            System.out.println("Possiedi già tutti gli alleati disponibili!");
            // Salta direttamente alla prossima ondata se li possiedi già tutti
            ondataAttuale++;
            avviaOndata();
        }
    }

    private void impostaDatiCarta(Ally a, ImageView img, Label nome, Label stats) {
        String nomeClasse = a.getClass().getSimpleName(); // Es: "Arciere", "Cavaliere"
        nome.setText(nomeClasse.toUpperCase());

        // CARICAMENTO IMMAGINE CARTA (es. /images/ArciereCard.png)
        try {
            var stream = getClass().getResourceAsStream("/images/" + nomeClasse + "Card.png");
            if (stream != null) {
                img.setImage(new Image(stream));
            } else {
                System.err.println("Carta non trovata: " + nomeClasse + "Card.png");
            }
        } catch (Exception e) {
            System.err.println("Errore caricamento carta per: " + nomeClasse);
        }

        // Statistiche
        stats.setText("HP: " + a.getHp() + "\n" +
                "Danno: " + a.getDamage() + "\n" +
                "Costo: " + a.getCost() + " Oro\n" +
                "Gittata: " + a.getRange());
    }

    @FXML
    private void scegliCarta1() {
        assegnaAlleatoA_Slot(opzione1);
        popupSceltaAlleato.setVisible(false);

        // Passiamo alla nuova ondata e la avviamo!
        ondataAttuale++;
        avviaOndata();
    }

    @FXML
    private void scegliCarta2() {
        assegnaAlleatoA_Slot(opzione2);
        popupSceltaAlleato.setVisible(false);

        // Passiamo alla nuova ondata e la avviamo!
        ondataAttuale++;
        avviaOndata();
    }

    private void assegnaAlleatoA_Slot(Ally alleatoScelto) {
        String nomeClasse = alleatoScelto.getClass().getSimpleName();

        // Trova il primo slot libero (da 0 a 5)
        int slotLibero = -1;
        for (int i = 0; i < 6; i++) {
            if (i >= alleatiNegliSlot.size() || alleatiNegliSlot.get(i) == null) {
                slotLibero = i;
                break;
            }
        }

        if (slotLibero != -1) {
            // C'è uno slot libero (es. lo Slot 2 alla fine dell'Ondata 1)
            if (slotLibero < alleatiNegliSlot.size()) {
                alleatiNegliSlot.set(slotLibero, () -> creoIstanza(nomeClasse));
            } else {
                alleatiNegliSlot.add(() -> creoIstanza(nomeClasse));
            }
            aggiornaIconaSlot(slotLibero, nomeClasse);
            System.out.println("Nuovo alleato " + nomeClasse + " aggiunto nello Slot " + (slotLibero + 1));
        } else {
            // Tutti e 6 gli slot sono pieni -> Sostituzione!
            // Se c'è uno slot selezionato usiamo quello, altrimenti sostituiamo lo slot 0 di default
            int slotTarget = (slotDaSostituireIndex != -1) ? slotDaSostituireIndex : 0;

            alleatiNegliSlot.set(slotTarget, () -> creoIstanza(nomeClasse));
            aggiornaIconaSlot(slotTarget, nomeClasse);

            System.out.println("Slot pieni! " + nomeClasse + " ha rimpiazzato l'alleato nello Slot " + (slotTarget + 1));
            slotDaSostituireIndex = -1; // Resetta il selettore
        }
    }

    private void aggiornaIconaSlot(int indexSlot, String nomeClasse) {
        if (immaginiSlotBarra != null && indexSlot >= 0 && indexSlot < immaginiSlotBarra.size()) {
            ImageView imgView = immaginiSlotBarra.get(indexSlot);
            if (imgView != null) {
                try {
                    // USA IL NOME ESATTO (es. "ArciereCard.png", "CavaliereCard.png") SENZA toLowerCase()
                    String percorsoCarta = "/images/" + nomeClasse + "Card.png";
                    var stream = getClass().getResourceAsStream(percorsoCarta);

                    if (stream != null) {
                        Image cartaImage = new Image(stream);
                        imgView.setImage(cartaImage);

                        // ADATTAMENTO DIMENSIONI SLOT (100x100 o dimensioni del tuo pulsante)
                        imgView.setFitWidth(90);
                        imgView.setFitHeight(90);
                        imgView.setPreserveRatio(true);

                        // Disattiva il ritaglio per non visualizzare lo sprite intero
                        imgView.setViewport(null);
                    } else {
                        System.err.println("⚠️ Immagine carta non trovata nel percorso: " + percorsoCarta);
                    }
                } catch (Exception e) {
                    System.err.println("Errore caricamento icona slot: " + e.getMessage());
                }
            }
        }
    }

    // Helper aggiornato con i 10 alleati
    private Ally creoIstanza(String nome) {
        return switch (nome) {
            case "Arciere" -> new Arciere();
            case "Soldato" -> new Soldato();
            case "Spadaccino" -> new Spadaccino();
            case "Stregone" -> new Stregone();
            case "Alabardiere" -> new Alabardiere();
            case "Cavaliere" -> new Cavaliere();
            case "Horseman" -> new Horseman();
            case "Prete" -> new Prete();
            case "Sacerdote" -> new Sacerdote();
            case "Templare" -> new Templare();
            default -> new Arciere();
        };
    }
}