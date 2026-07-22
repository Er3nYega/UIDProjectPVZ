package it.unical.uid.progettoesameuid.model;

public class MapMask {
    public Cell[][] maskMatrix;
    private final int ROWS = 5;    // 🎯 Corretto: la mappa ha 5 righe (0..4)
    private final int COLUMNS = 9; // 9 colonne (0..8)

    // Monete per gli acquisti
    private int monete = 1000; // Valore iniziale di gioco di prova (es. 300)

    private int nemiciUccisi = 0;
    private int moneteSpeseTotali = 0;
    private int alleatiPiazzatiTotali = 0;

    public MapMask() {
        maskMatrix = new Cell[ROWS][COLUMNS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                maskMatrix[i][j] = new Cell();
            }
        }
    }

    // --- METODI DI UTILITÀ PER IL GAMEPLAY ---

    public boolean hasEnemyAt(int row, int col) {
        if (row < 0 || row >= ROWS || col < 0 || col >= COLUMNS) {
            return false;
        }
        return maskMatrix[row][col].hasEnemy();
    }

    public boolean cellaLibera(int riga, int colonna) {
        if (riga < 0 || riga >= ROWS || colonna < 0 || colonna >= COLUMNS) {
            return false;
        }
        return !maskMatrix[riga][colonna].hasAlly();
    }

    public int getRows() {
        return ROWS;
    }

    public int getColumns() {
        return COLUMNS;
    }

    public Cell[][] getMaskMatrix() {
        return maskMatrix;
    }

    // --- GESTIONE ECONOMIA ---

    public int getMonete() {
        return monete;
    }

    public void setMonete(int monete) {
        this.monete = monete;
    }

    public void addMonete(int monete) {
        this.monete += monete;
    }

    public boolean sottraiMonete(int somma) {
        if (this.monete >= somma) {
            this.monete -= somma;
            addMoneteSpese(somma);
            return true;
        }
        return false;
    }

    // --- STATISTICHE E METRICHE ---

    public void incrementaNemiciUccisi() { this.nemiciUccisi++; }
    public int getNemiciUccisi() { return nemiciUccisi; }

    public void addMoneteSpese(int somma) { this.moneteSpeseTotali += somma; }
    public int getMoneteSpeseTotali() { return moneteSpeseTotali; }

    public void incrementaAlleatiPiazzati() { this.alleatiPiazzatiTotali++; }
    public int getAlleatiPiazzatiTotali() { return alleatiPiazzatiTotali; }

    // ==========================================
    // 🗄️ SUPPORTO SALVATAGGIO / CARICAMENTO DATABASE
    // ==========================================

    public GameStateDTO creaDTO(int ondata) {
        GameStateDTO dto = new GameStateDTO();
        dto.monete = this.monete;
        dto.ondataAttuale = ondata;
        dto.nemiciUccisi = this.nemiciUccisi;
        dto.alleatiPiazzati = this.alleatiPiazzatiTotali;
        dto.moneteSpese = this.moneteSpeseTotali;

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                Ally alleato = maskMatrix[r][c].getAlly();
                if (alleato != null) {
                    GameStateDTO.CellDataDTO cellaDTO = new GameStateDTO.CellDataDTO();
                    cellaDTO.riga = r;
                    cellaDTO.colonna = c;
                    cellaDTO.tipoAlleato = alleato.getClass().getSimpleName();
                    cellaDTO.hpRimanenti = alleato.getHp();
                    dto.griglia.add(cellaDTO);
                }
            }
        }
        return dto;
    }

    public void ripristinaDaDTO(GameStateDTO dto) {
        this.monete = dto.monete;
        this.nemiciUccisi = dto.nemiciUccisi;
        this.alleatiPiazzatiTotali = dto.alleatiPiazzati;
        this.moneteSpeseTotali = dto.moneteSpese;

        // svuota la griglia da stati precedenti prima di ricaricare
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                maskMatrix[r][c].setAlly(null);
                maskMatrix[r][c].getEnemyList().clear();
            }
        }
    }
}