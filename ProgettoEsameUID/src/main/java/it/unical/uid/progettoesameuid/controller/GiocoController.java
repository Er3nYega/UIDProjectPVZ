package it.unical.uid.progettoesameuid.controller;

import it.unical.uid.progettoesameuid.HelloApplication;
import it.unical.uid.progettoesameuid.model.*;
import it.unical.uid.progettoesameuid.view.AllyView;
import it.unical.uid.progettoesameuid.view.BulletView;
import it.unical.uid.progettoesameuid.view.EnemyView;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public class GiocoController {

    public static final int moneteDefault = 50;

    @FXML private ImageView sfondoMappa;
    @FXML private AnchorPane areaGiocoAnchor;
    @FXML private Label testoMonete;

    // --- POPUP DRAFTING E CARTE ---
    @FXML private VBox popupSceltaAlleato;
    @FXML private ImageView imgCard1, imgCard2;
    @FXML private Label nomeCard1, nomeCard2, statsCard1, statsCard2;
    @FXML private VBox card2Container;

    // --- SLOT BARRA ---
    @FXML private ImageView slotImg_0, slotImg_1, slotImg_2, slotImg_3, slotImg_4, slotImg_5;
    private List<ImageView> immaginiSlotBarra;

    // --- PANNELLI DI PAUSA E IMPOSTAZIONI ---
    @FXML private VBox menuPausa;
    @FXML private VBox panelSettingsPausa;
    @FXML private VBox panelSelezionaSlotSalvataggio;
    @FXML private Slider sliderMusicaPausa;

    // --- OVERLAY GAME OVER ---
    @FXML private VBox screenGameOver;
    @FXML private Label lblStatOndata, lblStatNemici, lblStatAlleati, lblStatMonete;

    // --- PULSANATI SALVATAGGIO IN PAUSA ---
    @FXML private Button btnSaveSlot1;
    @FXML private Button btnSaveSlot2;
    @FXML private Button btnSaveSlot3;

    // --- STATO DEL GIOCO ---
    private MapMask model;
    private boolean inPausa = false;

    private Supplier<Ally> costruttoreAlleatoSelezionato = null;
    private int costoAlleatoSelezionato = 0;
    private String nomeAlleatoSelezionato = "";

    private final List<Supplier<Ally>> alleatiNegliSlot = new ArrayList<>();
    private final List<AllyView> alleatiInGioco = new ArrayList<>();
    private final List<BulletView> proiettiliAttivi = new ArrayList<>();
    private final List<EnemyView> nemiciAttivi = new ArrayList<>();

    private int ondataAttuale = 1;
    private final int TOTALE_ONDATE = 10;
    private int nemiciRimanentiOndata = 5;
    private long ultimoSpawnTime = 0;
    private AnimationTimer gameLoop;

    private Ally opzione1, opzione2;
    private final List<Supplier<Ally>> poolTuttiAlleati = List.of(
            Alabardiere::new, Arciere::new, Cavaliere::new, Horseman::new,
            Prete::new, Sacerdote::new, Soldato::new, Spadaccino::new,
            Stregone::new, Templare::new
    );

    private final DatabaseManager dbManager = new DatabaseManager();
    private int slotAttuale = 1;

    @FXML private StackPane rootStackPane;
    @FXML private Group scaledGroup;

    @FXML
    public void initialize() {
        immaginiSlotBarra = List.of(slotImg_0, slotImg_1, slotImg_2, slotImg_3, slotImg_4, slotImg_5);

        if (this.model == null) {
            this.model = new MapMask();
        }

        // 🎨 APPLICAZIONE SFONDO MEDIEVALE A TUTTI I POPUP IN GIOCO
        var urlPannello = getClass().getResource("/pannelllo.png");
        if (urlPannello != null) {
            Image img = new Image(urlPannello.toExternalForm());
            BackgroundSize bgSize = new BackgroundSize(100, 100, true, true, true, false);
            BackgroundImage bgImg = new BackgroundImage(img, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, bgSize);
            Background bg = new Background(bgImg);

            if (menuPausa != null) menuPausa.setBackground(bg);
            if (panelSettingsPausa != null) panelSettingsPausa.setBackground(bg);
            if (panelSelezionaSlotSalvataggio != null) panelSelezionaSlotSalvataggio.setBackground(bg);
        }

        // Slot di partenza
        alleatiNegliSlot.add(Arciere::new);
        alleatiNegliSlot.add(Cavaliere::new);
        aggiornaIconaSlot(0, "Arciere");
        aggiornaIconaSlot(1, "Cavaliere");

        for (ImageView imgView : immaginiSlotBarra) {
            if (imgView != null) imgView.setMouseTransparent(true);
        }

        caricaSfondoMappa();

        if (testoMonete != null) {
            testoMonete.setText("x" + model.getMonete());
        }

        // Gestione tasto ESC
        areaGiocoAnchor.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                        togglePausa();
                        event.consume();
                    }
                });
            }
        });
        it.unical.uid.progettoesameuid.utility.SoundManager.riproduciMusica("battle_theme.mp3");

        if (sliderMusicaPausa != null) {
            sliderMusicaPausa.setValue(it.unical.uid.progettoesameuid.utility.SoundManager.getVolumeMusica());
            sliderMusicaPausa.valueProperty().addListener((obs, oldVal, newVal) -> {
                it.unical.uid.progettoesameuid.utility.SoundManager.setVolumeMusica(newVal.doubleValue());
            });
        }

        if (rootStackPane != null && scaledGroup != null) {
            rootStackPane.widthProperty().addListener((obs, oldW, newW) -> ridimensionaSchermata());
            rootStackPane.heightProperty().addListener((obs, oldH, newH) -> ridimensionaSchermata());
        }

        avviaOndata();
    }

    private void ridimensionaSchermata() {
        if (rootStackPane == null || scaledGroup == null) return;

        double w = rootStackPane.getWidth();
        double h = rootStackPane.getHeight();

        if (w <= 0 || h <= 0) return;

        double scale = Math.min(w / 1920.0, h / 1080.0);
        scaledGroup.setScaleX(scale);
        scaledGroup.setScaleY(scale);
    }

    private void caricaSfondoMappa() {
        try {
            var stream = getClass().getResourceAsStream("/map.png");
            if (stream != null && sfondoMappa != null) {
                sfondoMappa.setImage(new Image(stream));
            }
        } catch (Exception e) {
            System.err.println("Errore mappa: " + e.getMessage());
        }
    }

    // ==========================================
    // 🕹️ GAME LOOP PRINCIPALE
    // ==========================================
    public void avviaOndata() {
        if (gameLoop != null) gameLoop.stop();

        nemiciRimanentiOndata = 5 + (ondataAttuale - 1) * 3;
        ultimoSpawnTime = System.nanoTime();

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (inPausa) return;

                if (nemiciRimanentiOndata > 0 && (now - ultimoSpawnTime > 3_000_000_000L)) {
                    spawnaNemicoCasual();
                    ultimoSpawnTime = now;
                    nemiciRimanentiOndata--;
                }

                aggiornaMovimentoNemiciESincronizzaModel();
                aggiornaLogicaCombattimento();
                aggiornaProiettili();
                sincronizzaNemiciMorti();

                if (nemiciRimanentiOndata == 0 && nemiciAttivi.isEmpty()) {
                    gameLoop.stop();
                    completaOndata();
                }
            }
        };

        gameLoop.start();
    }

    private void aggiornaMovimentoNemiciESincronizzaModel() {
        long tempoAttuale = System.currentTimeMillis();

        for (int i = nemiciAttivi.size() - 1; i >= 0; i--) {
            EnemyView nemicoView = nemiciAttivi.get(i);
            int riga = nemicoView.getRigaAttuale();

            int colonnaVisiva = calcolaColonnaDaSprite(riga, nemicoView);

            boolean bloccato = false;
            Ally alleatoTarget = null;

            if (model != null && colonnaVisiva >= 0 && colonnaVisiva < model.getColumns()) {
                alleatoTarget = model.getMaskMatrix()[riga][colonnaVisiva].getAlly();
                if (alleatoTarget != null) {
                    bloccato = true;
                }
            }

            if (bloccato && alleatoTarget != null) {
                nemicoView.setStato(EnemyView.StatoNemico.ATTACCO);

                if (tempoAttuale - nemicoView.getUltimoAttaccoTime() > 1500) {
                    alleatoTarget.beDamaged(20);
                    nemicoView.setUltimoAttaccoTime(tempoAttuale);

                    if (alleatoTarget.isDead()) {
                        model.getMaskMatrix()[riga][colonnaVisiva].setAlly(null);
                        int r = riga, c = colonnaVisiva;
                        alleatiInGioco.removeIf(a -> {
                            if (a.getRiga() == r && a.getColonna() == c) {
                                areaGiocoAnchor.getChildren().remove(a);
                                return true;
                            }
                            return false;
                        });
                    }
                }
            } else {
                int vecchiaCol = calcolaColonnaDaSprite(riga, nemicoView);
                nemicoView.setStato(EnemyView.StatoNemico.CAMMINATA);
                nemicoView.muoviASinistra();
                int nuovaCol = calcolaColonnaDaSprite(riga, nemicoView);

                if (vecchiaCol != nuovaCol && model != null) {
                    if (vecchiaCol < model.getColumns() && nuovaCol < model.getColumns()) {
                        var listaVecchia = model.getMaskMatrix()[riga][vecchiaCol].getEnemyList();
                        if (!listaVecchia.isEmpty()) {
                            Enemy e = listaVecchia.removeFirst();
                            model.getMaskMatrix()[riga][nuovaCol].getEnemyList().add(e);
                        }
                    }
                }
            }

            if (nemicoView.getLayoutX() < 300.0) {
                gestisciGameOver();
                return;
            }
        }
    }

    private int calcolaColonnaDaSprite(int riga, EnemyView nemico) {
        double centroX = nemico.getLayoutX() + 100.0;

        for (int c = 8; c >= 0; c--) {
            Polygon poly = (Polygon) areaGiocoAnchor.lookup("#cell_" + riga + "_" + c);
            if (poly != null && centroX >= poly.getLayoutX()) {
                return c;
            }
        }
        return 0;
    }

    private void aggiornaLogicaCombattimento() {
        for (int i = alleatiInGioco.size() - 1; i >= 0; i--) {
            AllyView alleatoVisivo = alleatiInGioco.get(i);
            Ally modello = alleatoVisivo.getModelloAlleato();
            if (modello == null) continue;

            int riga = alleatoVisivo.getRiga();
            int colonna = alleatoVisivo.getColonna();

            if (modello instanceof Horseman) {
                alleatoVisivo.impostaStato(AllyView.StatoAlleato.ATTACCO);

                boolean haAvanzato = modello.doAction(this.model, riga, colonna);

                if (haAvanzato) {
                    int nuovaColonna = colonna + 1;

                    if (nuovaColonna < model.getColumns()) {
                        alleatoVisivo.setPosizioneGriglia(riga, nuovaColonna);

                        Polygon polyTarget = (Polygon) areaGiocoAnchor.lookup("#cell_" + riga + "_" + nuovaColonna);
                        if (polyTarget != null) {
                            double centroX = (polyTarget.getPoints().get(0) + polyTarget.getPoints().get(4)) / 2;
                            alleatoVisivo.setLayoutX(polyTarget.getLayoutX() + centroX - 100.0);
                        }
                    } else {
                        areaGiocoAnchor.getChildren().remove(alleatoVisivo);
                        alleatiInGioco.remove(i);
                        continue;
                    }
                }
                continue;
            }

            boolean nemicoNelRange = false;
            for (int c = colonna; c <= colonna + modello.getRange() && c < model.getColumns(); c++) {
                if (model.hasEnemyAt(riga, c)) {
                    nemicoNelRange = true;
                    break;
                }
            }

            boolean haAttaccatoOra = modello.doAction(this.model, riga, colonna);

            if (nemicoNelRange) {
                alleatoVisivo.impostaStato(AllyView.StatoAlleato.ATTACCO);

                if (haAttaccatoOra && !(modello instanceof Arciere) && !(modello instanceof Stregone)) {
                    for (EnemyView nemico : nemiciAttivi) {
                        if (nemico.getRigaAttuale() == riga) {
                            int colNemico = calcolaColonnaDaSprite(riga, nemico);
                            if (colNemico == colonna || colNemico == colonna + 1) {
                                nemico.subisciDanno(modello.getDamage());
                                break;
                            }
                        }
                    }
                }
            } else {
                alleatoVisivo.impostaStato(AllyView.StatoAlleato.IDLE);
            }
        }
    }

    private void aggiornaProiettili() {
        for (int i = proiettiliAttivi.size() - 1; i >= 0; i--) {
            BulletView proiettile = proiettiliAttivi.get(i);
            proiettile.muoviADestra();

            boolean colpito = false;

            for (EnemyView nemico : nemiciAttivi) {
                if (nemico.getRigaAttuale() == proiettile.getRiga()) {
                    if (proiettile.getBoundsInParent().intersects(nemico.getBoundsInParent())) {
                        nemico.subisciDanno(proiettile.getDanno());
                        colpito = true;
                        break;
                    }
                }
            }

            if (colpito || proiettile.getLayoutX() > 1850.0) {
                areaGiocoAnchor.getChildren().remove(proiettile);
                proiettiliAttivi.remove(i);
            }
        }
    }

    private void sincronizzaNemiciMorti() {
        for (int j = nemiciAttivi.size() - 1; j >= 0; j--) {
            EnemyView nemicoView = nemiciAttivi.get(j);

            if (nemicoView.isDead()) {
                int riga = nemicoView.getRigaAttuale();
                int col = calcolaColonnaDaSprite(riga, nemicoView);

                if (model != null && col >= 0 && col < model.getColumns()) {
                    var enemyList = model.getMaskMatrix()[riga][col].getEnemyList();
                    if (!enemyList.isEmpty()) {
                        enemyList.removeFirst();
                    }
                }

                areaGiocoAnchor.getChildren().remove(nemicoView);
                nemiciAttivi.remove(j);

                if (model != null) {
                    model.incrementaNemiciUccisi();
                }
            }
        }
    }

    private void spawnaNemicoCasual() {
        int rigaCasuale = ThreadLocalRandom.current().nextInt(0, 5);
        int colonnaSpawn = 8;

        Polygon poligonoTarget = (Polygon) areaGiocoAnchor.lookup("#cell_" + rigaCasuale + "_" + colonnaSpawn);

        if (poligonoTarget != null) {
            String tipoNemico = "Skeleton_Walk";
            Enemy modelloNemico = creaIstanzaNemico(tipoNemico, colonnaSpawn);

            if (model != null && modelloNemico != null) {
                model.getMaskMatrix()[rigaCasuale][colonnaSpawn].getEnemyList().add(modelloNemico);
            }

            EnemyView nemico = new EnemyView(tipoNemico, rigaCasuale);

            double centroX = (poligonoTarget.getPoints().get(0) + poligonoTarget.getPoints().get(4)) / 2;
            double centroY = (poligonoTarget.getPoints().get(1) + poligonoTarget.getPoints().get(5)) / 2;

            nemico.setLayoutX(poligonoTarget.getLayoutX() + centroX - 45.0);
            nemico.setLayoutY(poligonoTarget.getLayoutY() + centroY - 100.0);

            switch(rigaCasuale) {
                case 0 -> { nemico.setScaleX(0.75); nemico.setScaleY(0.75); }
                case 1 -> { nemico.setScaleX(0.83); nemico.setScaleY(0.83); }
                case 2 -> { nemico.setScaleX(0.92); nemico.setScaleY(0.92); }
                case 3 -> { nemico.setScaleX(1.00); nemico.setScaleY(1.00); }
                case 4 -> { nemico.setScaleX(1.08); nemico.setScaleY(1.08); }
            }

            areaGiocoAnchor.getChildren().add(nemico);
            nemiciAttivi.add(nemico);
        }
    }

    private Enemy creaIstanzaNemico(String tipo, int colonna) {
        return switch (tipo) {
            case "Skeleton_Walk", "Scheletro" -> new Enemy(100, 20, 1, colonna);
            case "Zombie" -> new Enemy(200, 15, 1, colonna);
            case "Orco" -> new Enemy(350, 40, 1, colonna);
            default -> new Enemy(100, 20, 1, colonna);
        };
    }

    @FXML
    private void gestisciClickCasella(MouseEvent event) {
        if (inPausa || (popupSceltaAlleato != null && popupSceltaAlleato.isVisible())) return;

        Polygon poligono = (Polygon) event.getSource();
        String[] parti = poligono.getId().split("_");
        int riga = Integer.parseInt(parti[1]);
        int colonna = Integer.parseInt(parti[2]);

        if (costruttoreAlleatoSelezionato == null || !model.cellaLibera(riga, colonna)) return;
        if (model.getMonete() < costoAlleatoSelezionato) return;

        Ally nuovoAlleatoLogico = costruttoreAlleatoSelezionato.get();

        if (model.sottraiMonete(costoAlleatoSelezionato)) {
            model.getMaskMatrix()[riga][colonna].setAlly(nuovoAlleatoLogico);
            testoMonete.setText("x" + model.getMonete());
            model.incrementaAlleatiPiazzati();

            AllyView alleatoVisivo = new AllyView(nomeAlleatoSelezionato.toLowerCase());
            alleatoVisivo.setPosizioneGriglia(riga, colonna);
            alleatoVisivo.setModelloAlleato(nuovoAlleatoLogico);

            impostaListenerProiettile(alleatoVisivo);

            double centroX = (poligono.getPoints().get(0) + poligono.getPoints().get(4)) / 2;
            double centroY = (poligono.getPoints().get(1) + poligono.getPoints().get(5)) / 2;

            alleatoVisivo.setLayoutX(poligono.getLayoutX() + centroX - 100.0);
            alleatoVisivo.setLayoutY(poligono.getLayoutY() + centroY - 100.0);

            switch(riga) {
                case 0 -> { alleatoVisivo.setScaleX(0.75); alleatoVisivo.setScaleY(0.75); }
                case 1 -> { alleatoVisivo.setScaleX(0.83); alleatoVisivo.setScaleY(0.83); }
                case 2 -> { alleatoVisivo.setScaleX(0.92); alleatoVisivo.setScaleY(0.92); }
                case 3 -> { alleatoVisivo.setScaleX(1.00); alleatoVisivo.setScaleY(1.00); }
                case 4 -> { alleatoVisivo.setScaleX(1.08); alleatoVisivo.setScaleY(1.08); }
            }

            areaGiocoAnchor.getChildren().add(alleatoVisivo);
            alleatiInGioco.add(alleatoVisivo);
        }
    }

    private void impostaListenerProiettile(AllyView alleatoVisivo) {
        Ally modello = alleatoVisivo.getModelloAlleato();

        if (modello instanceof Arciere || modello instanceof Stregone) {
            alleatoVisivo.setOnAttackReleaseListener(() -> {
                long nemiciSuRiga = nemiciAttivi.stream()
                        .filter(n -> n.getRigaAttuale() == alleatoVisivo.getRiga())
                        .count();

                if (nemiciSuRiga > 0) {
                    double startX = alleatoVisivo.getLayoutX() + 120.0;
                    double startY = alleatoVisivo.getLayoutY() + 75.0;

                    BulletView freccia = new BulletView(
                            alleatoVisivo.getNomeAlleato().toLowerCase(),
                            startX,
                            startY,
                            alleatoVisivo.getRiga(),
                            modello.getDamage()
                    );

                    proiettiliAttivi.add(freccia);
                    areaGiocoAnchor.getChildren().add(freccia);
                    freccia.toFront();
                }
            });
        } else {
            alleatoVisivo.setOnAttackReleaseListener(null);
        }
    }

    // ==========================================
    // 🎛️ PAUSA, DRAFTING E UTILITIES
    // ==========================================
    @FXML
    private void gestisciClickSlotBarra(javafx.event.ActionEvent event) {
        javafx.scene.control.Button btn = (javafx.scene.control.Button) event.getSource();
        int indexSlot = Integer.parseInt(btn.getId().split("_")[1]);

        if (indexSlot < alleatiNegliSlot.size() && alleatiNegliSlot.get(indexSlot) != null) {
            Supplier<Ally> costruttore = alleatiNegliSlot.get(indexSlot);
            Ally esempio = costruttore.get();
            selezionaUnita(esempio.getClass().getSimpleName(), esempio.getCost(), costruttore);
        }
    }

    public void selezionaUnita(String nome, int costo, Supplier<Ally> costruttore) {
        this.nomeAlleatoSelezionato = nome;
        this.costoAlleatoSelezionato = costo;
        this.costruttoreAlleatoSelezionato = costruttore;
    }

    public void togglePausa() {
        if (inPausa) riprendiGioco(); else apriPausa();
    }

    @FXML
    public void apriPausa() {
        inPausa = true;
        for (AllyView a : alleatiInGioco) a.pausaAnimazione();
        for (EnemyView n : nemiciAttivi) n.pausaAnimazione();
        if (menuPausa != null) menuPausa.setVisible(true);
    }

    @FXML
    public void riprendiGioco() {
        inPausa = false;
        if (menuPausa != null) menuPausa.setVisible(false);
        for (AllyView a : alleatiInGioco) a.riprendiAnimazione();
        for (EnemyView n : nemiciAttivi) n.riprendiAnimazione();
    }

    private void completaOndata() {
        for (BulletView p : proiettiliAttivi) areaGiocoAnchor.getChildren().remove(p);
        proiettiliAttivi.clear();
        for (AllyView alleato : alleatiInGioco) alleato.impostaStato(AllyView.StatoAlleato.IDLE);

        if (ondataAttuale < TOTALE_ONDATE) {

            model.addMonete(moneteDefault * ondataAttuale);
            testoMonete.setText("x" + model.getMonete());
            mostraPopupSceltaAlleato();
        }
    }

    public void mostraPopupSceltaAlleato() {
        Set<String> posseduti = new HashSet<>();
        for (Supplier<Ally> s : alleatiNegliSlot) {
            if (s != null) posseduti.add(s.get().getClass().getSimpleName());
        }

        List<Supplier<Ally>> disponibili = poolTuttiAlleati.stream()
                .filter(s -> !posseduti.contains(s.get().getClass().getSimpleName()))
                .toList();

        if (disponibili.size() >= 2) {
            int idx1 = ThreadLocalRandom.current().nextInt(disponibili.size());
            int idx2;
            do { idx2 = ThreadLocalRandom.current().nextInt(disponibili.size()); } while (idx1 == idx2);

            opzione1 = disponibili.get(idx1).get();
            opzione2 = disponibili.get(idx2).get();

            impostaDatiCarta(opzione1, imgCard1, nomeCard1, statsCard1);
            impostaDatiCarta(opzione2, imgCard2, nomeCard2, statsCard2);

            popupSceltaAlleato.setVisible(true);
        } else {
            ondataAttuale++;
            avviaOndata();
        }
    }

    private void impostaDatiCarta(Ally a, ImageView img, Label nome, Label stats) {
        String nomeClasse = a.getClass().getSimpleName();
        nome.setText(nomeClasse.toUpperCase());
        try {
            var stream = getClass().getResourceAsStream("/images/" + nomeClasse + "Card.png");
            if (stream != null) img.setImage(new Image(stream));
        } catch (Exception e) {}

        stats.setText("HP: " + a.getHp() + "\nDanno: " + a.getDamage() + "\nCosto: " + a.getCost() + " Oro");
    }

    @FXML private void scegliCarta1() { assegnaAlleatoA_Slot(opzione1); popupSceltaAlleato.setVisible(false); ondataAttuale++; avviaOndata(); }
    @FXML private void scegliCarta2() { assegnaAlleatoA_Slot(opzione2); popupSceltaAlleato.setVisible(false); ondataAttuale++; avviaOndata(); }

    private void assegnaAlleatoA_Slot(Ally alleatoScelto) {
        String nomeClasse = alleatoScelto.getClass().getSimpleName();
        int slotLibero = -1;
        for (int i = 0; i < 6; i++) {
            if (i >= alleatiNegliSlot.size() || alleatiNegliSlot.get(i) == null) {
                slotLibero = i;
                break;
            }
        }
        if (slotLibero != -1) {
            if (slotLibero < alleatiNegliSlot.size()) alleatiNegliSlot.set(slotLibero, () -> creoIstanza(nomeClasse));
            else alleatiNegliSlot.add(() -> creoIstanza(nomeClasse));
            aggiornaIconaSlot(slotLibero, nomeClasse);
        }
    }

    private void aggiornaIconaSlot(int indexSlot, String nomeClasse) {
        if (immaginiSlotBarra != null && indexSlot >= 0 && indexSlot < immaginiSlotBarra.size()) {
            ImageView imgView = immaginiSlotBarra.get(indexSlot);
            if (imgView != null) {
                try {
                    var stream = getClass().getResourceAsStream("/images/" + nomeClasse + "Card.png");
                    if (stream != null) {
                        imgView.setImage(new Image(stream));
                        imgView.setFitWidth(90);
                        imgView.setFitHeight(90);
                        imgView.setPreserveRatio(true);
                        imgView.setViewport(null);
                    }
                } catch (Exception e) {}
            }
        }
    }

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

    private void gestisciGameOver() {
        dbManager.eliminaSlot(this.slotAttuale);

        if (screenGameOver != null) {
            lblStatOndata.setText("Ondata Massima Raggiunta: " + ondataAttuale);
            lblStatNemici.setText("Nemici Sconfitti: " + model.getNemiciUccisi());
            lblStatAlleati.setText("Alleati Reclutati: " + model.getAlleatiPiazzatiTotali());
            lblStatMonete.setText("Oro Speso nella Difesa: " + model.getMoneteSpeseTotali());

            screenGameOver.setVisible(true);
            screenGameOver.toFront();
        }
    }

    // --- METODI NAVIGATION PANNELLI ---

    @FXML
    private void apriSettingsPausa() {
        if (menuPausa != null) menuPausa.setVisible(false);
        if (panelSettingsPausa != null) panelSettingsPausa.setVisible(true);
    }

    @FXML
    private void chiudiSettingsPausa() {
        if (panelSettingsPausa != null) panelSettingsPausa.setVisible(false);
        if (menuPausa != null) menuPausa.setVisible(true);
    }

    @FXML private void esciSenzaSalvare() {
        if (gameLoop != null) gameLoop.stop();
        try {
            HelloApplication app = new HelloApplication();
            app.start((javafx.stage.Stage) areaGiocoAnchor.getScene().getWindow());
        } catch (Exception e) {}
    }
    @FXML private void ritornaAlMenuGameOver() { esciSenzaSalvare(); }

    public void setModel(MapMask model) {
        this.model = model;
        if (testoMonete != null && model != null) {
            testoMonete.setText("x" + model.getMonete());
        }
    }

    public void setSlotAttuale(int slotId) {
        this.slotAttuale = slotId;
    }

    public void caricaStatoDaDB(GameStateDTO dto) {
        if (dto == null) return;

        this.slotAttuale = dto.slotId;
        this.ondataAttuale = dto.ondataAttuale;
        if (this.model != null) {
            this.model.ripristinaDaDTO(dto);
        }

        if (testoMonete != null && model != null) {
            testoMonete.setText("MONETE: " + model.getMonete());
        }

        for (AllyView a : alleatiInGioco) areaGiocoAnchor.getChildren().remove(a);
        for (EnemyView n : nemiciAttivi) areaGiocoAnchor.getChildren().remove(n);
        alleatiInGioco.clear();
        nemiciAttivi.clear();
        proiettiliAttivi.clear();

        for (GameStateDTO.CellDataDTO cella : dto.griglia) {
            String nomeClasse = cella.tipoAlleato;
            Ally alleatoLogico = creoIstanza(nomeClasse);

            if (model != null) {
                model.getMaskMatrix()[cella.riga][cella.colonna].setAlly(alleatoLogico);
            }

            Polygon poligono = (Polygon) areaGiocoAnchor.lookup("#cell_" + cella.riga + "_" + cella.colonna);
            if (poligono != null) {
                AllyView alleatoVisivo = new AllyView(nomeClasse.toLowerCase());
                alleatoVisivo.setPosizioneGriglia(cella.riga, cella.colonna);
                alleatoVisivo.setModelloAlleato(alleatoLogico);
                impostaListenerProiettile(alleatoVisivo);

                double centroX = (poligono.getPoints().get(0) + poligono.getPoints().get(4)) / 2;
                double centroY = (poligono.getPoints().get(1) + poligono.getPoints().get(5)) / 2;

                alleatoVisivo.setLayoutX(poligono.getLayoutX() + centroX - 100.0);
                alleatoVisivo.setLayoutY(poligono.getLayoutY() + centroY - 100.0);

                switch (cella.riga) {
                    case 0 -> { alleatoVisivo.setScaleX(0.75); alleatoVisivo.setScaleY(0.75); }
                    case 1 -> { alleatoVisivo.setScaleX(0.83); alleatoVisivo.setScaleY(0.83); }
                    case 2 -> { alleatoVisivo.setScaleX(0.92); alleatoVisivo.setScaleY(0.92); }
                    case 3 -> { alleatoVisivo.setScaleX(1.00); alleatoVisivo.setScaleY(1.00); }
                    case 4 -> { alleatoVisivo.setScaleX(1.08); alleatoVisivo.setScaleY(1.08); }
                }

                areaGiocoAnchor.getChildren().add(alleatoVisivo);
                alleatiInGioco.add(alleatoVisivo);
            }
        }

        for (GameStateDTO.EnemyDataDTO nDto : dto.nemici) {
            EnemyView nemicoView = new EnemyView(nDto.tipoNemico, nDto.riga);
            nemicoView.setLayoutX(nDto.posX);
            nemicoView.setLayoutY(nDto.posY);
            nemicoView.setHp(nDto.hpRimanenti);

            switch (nDto.riga) {
                case 0 -> { nemicoView.setScaleX(0.75); nemicoView.setScaleY(0.75); }
                case 1 -> { nemicoView.setScaleX(0.83); nemicoView.setScaleY(0.83); }
                case 2 -> { nemicoView.setScaleX(0.92); nemicoView.setScaleY(0.92); }
                case 3 -> { nemicoView.setScaleX(1.00); nemicoView.setScaleY(1.00); }
                case 4 -> { nemicoView.setScaleX(1.08); nemicoView.setScaleY(1.08); }
            }

            int colonna = calcolaColonnaDaSprite(nDto.riga, nemicoView);
            Enemy modelloNemico = creaIstanzaNemico(nDto.tipoNemico, colonna);
            if (modelloNemico != null && model != null) {
                model.getMaskMatrix()[nDto.riga][colonna].getEnemyList().add(modelloNemico);
            }

            areaGiocoAnchor.getChildren().add(nemicoView);
            nemiciAttivi.add(nemicoView);
        }
    }

    @FXML
    private void salvaPartita(javafx.event.ActionEvent event) {
        if (model == null) return;

        GameStateDTO dto = model.creaDTO(this.ondataAttuale);
        dto.slotId = this.slotAttuale;

        java.time.LocalDateTime ora = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        dto.dataSalvataggio = ora.format(fmt);

        for (EnemyView nemicoView : nemiciAttivi) {
            GameStateDTO.EnemyDataDTO eDto = new GameStateDTO.EnemyDataDTO();
            eDto.riga = nemicoView.getRigaAttuale();
            eDto.tipoNemico = nemicoView.getTipoNemico();
            eDto.posX = nemicoView.getLayoutX();
            eDto.posY = nemicoView.getLayoutY();
            eDto.hpRimanenti = nemicoView.getHpAttuali();
            dto.nemici.add(eDto);
        }

        boolean successo = dbManager.salvaPartita(dto);

        if (successo) {
            if (gameLoop != null) gameLoop.stop();

            try {
                java.net.URL resource = getClass().getResource("/it/unical/uid/progettoesameuid/MenuPrincipale.fxml");
                if (resource == null) resource = getClass().getResource("/MenuPrincipale.fxml");

                FXMLLoader loader = new FXMLLoader(resource);
                Parent root = loader.load();

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.getScene().setRoot(root);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void selezionaSlotEAvvia(ActionEvent event, int slotId) {
        GameStateDTO salvataggio = dbManager.caricaPartita(slotId);

        try {
            URL resource = getClass().getResource("/it/unical/uid/progettoesameuid/MappaGioco.fxml");
            if (resource == null) resource = getClass().getResource("/MappaGioco.fxml");

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            GiocoController giocoController = loader.getController();

            if (salvataggio != null) {
                dbManager.eliminaSlot(slotId);

                MapMask model = new MapMask();
                giocoController.setModel(model);
                giocoController.setSlotAttuale(slotId);
            } else {
                MapMask model = new MapMask();
                giocoController.setModel(model);
                giocoController.setSlotAttuale(slotId);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cancellaSalvataggioSlot(int slotId) {
        boolean successo = dbManager.eliminaSlot(slotId);
        if (successo) {
            aggiornaTestiSlotSalvataggio();
        }
    }

    private void aggiornaTestiSlotSalvataggio() {
        aggiornaPulsanteSave(btnSaveSlot1, 1);
        aggiornaPulsanteSave(btnSaveSlot2, 2);
        aggiornaPulsanteSave(btnSaveSlot3, 3);
    }

    private void aggiornaPulsanteSave(Button btn, int slotId) {
        if (btn == null) return;
        GameStateDTO dto = dbManager.caricaPartita(slotId);
        if (dto != null) {
            String data = dto.dataSalvataggio != null ? dto.dataSalvataggio : "";
            btn.setText("SLOT " + slotId + " - Ondata " + dto.ondataAttuale + " (" + data + ") [SOVRASCRIVI]");
        } else {
            btn.setText("SLOT " + slotId + " - VUOTO (Salva qui)");
        }
    }

    @FXML
    private void confermaSovrascrittura(javafx.event.ActionEvent event) {
        eseguiSalvataggioEdEsci(1, event);
    }

    @FXML private void salvaSuSlot1(javafx.event.ActionEvent event) { eseguiSalvataggioEdEsci(1, event); }
    @FXML private void salvaSuSlot2(javafx.event.ActionEvent event) { eseguiSalvataggioEdEsci(2, event); }
    @FXML private void salvaSuSlot3(javafx.event.ActionEvent event) { eseguiSalvataggioEdEsci(3, event); }

    @FXML
    private void chiudiPanelSalvataggio() {
        if (panelSelezionaSlotSalvataggio != null) {
            panelSelezionaSlotSalvataggio.setVisible(false);
        }
    }

    private void eseguiSalvataggioEdEsci(int targetSlot, javafx.event.ActionEvent event) {
        GameStateDTO dto = model.creaDTO(this.ondataAttuale);
        dto.slotId = targetSlot;

        java.time.LocalDateTime ora = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        dto.dataSalvataggio = ora.format(fmt);

        for (EnemyView nemicoView : nemiciAttivi) {
            GameStateDTO.EnemyDataDTO eDto = new GameStateDTO.EnemyDataDTO();
            eDto.riga = nemicoView.getRigaAttuale();
            eDto.tipoNemico = nemicoView.getTipoNemico();
            eDto.posX = nemicoView.getLayoutX();
            eDto.posY = nemicoView.getLayoutY();
            eDto.hpRimanenti = nemicoView.getHpAttuali();
            dto.nemici.add(eDto);
        }

        boolean successo = dbManager.salvaPartita(dto);

        if (successo) {
            if (gameLoop != null) gameLoop.stop();

            try {
                java.net.URL resource = getClass().getResource("/it/unical/uid/progettoesameuid/MenuPrincipale.fxml");
                if (resource == null) resource = getClass().getResource("/MenuPrincipale.fxml");

                FXMLLoader loader = new FXMLLoader(resource);
                Parent root = loader.load();

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.getScene().setRoot(root);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}