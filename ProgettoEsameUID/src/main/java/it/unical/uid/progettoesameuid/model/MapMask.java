package it.unical.uid.progettoesameuid.model;

public class MapMask {
    public Cell[][] maskMatrix;
    private final int ROWS = 9;
    private final int COLUMNS = 9; // Corretto il nome della variabile

    // Monete per gli acquisti
    private int monete = 10000000;

    public MapMask() {
        maskMatrix = new Cell[ROWS][COLUMNS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                maskMatrix[i][j] = new Cell();
            }
        }
    }

    public void onGame() {
        // FASE 1: Fai agire tutti gli Alleati (es. l'Arciere che cerca nemici sulla riga)
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                Cell currentCell = maskMatrix[i][j];
                if (currentCell.hasAlly()) {
                    currentCell.getAlly().doAction(this, i, j);
                }
            }
        }

        // FASE 2: Fai agire i Nemici e gestisci i combattimenti ravvicinati
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                Cell currentCell = maskMatrix[i][j];

                if (currentCell.hasEnemy()) {
                    boolean battle = false;
                    Ally allyToAttack = null;

                    // Controlliamo se c'è un alleato da attaccare nella cella corrente o in quella precedente
                    if (currentCell.hasAlly()) {
                        battle = true;
                        allyToAttack = currentCell.getAlly();
                    } else if (j > 0 && maskMatrix[i][j - 1].hasAlly()) {
                        battle = true;
                        allyToAttack = maskMatrix[i][j - 1].getAlly();
                    }

                    // Iteriamo su una copia o lista dei nemici presenti nella cella
                    for (Enemy e : currentCell.getEnemyList()) {
                        if (battle && allyToAttack != null) {
                            allyToAttack.beDamaged(e.damage);
                            System.out.println("Un nemico sta attaccando un alleato!");
                        } else {
                            e.move();
                            int newColumn = (int) e.getLogicX();
                            if (newColumn < 0 || newColumn >= COLUMNS) {
                                // Gestione sconfitta o uscita dal tabellone
                            }
                        }
                    }

                    // Pulizia: se l'alleato è morto, lo rimuoviamo
                    if (allyToAttack != null && allyToAttack.isDead()) {
                        if (currentCell.hasAlly() && currentCell.getAlly() == allyToAttack) {
                            currentCell.setAlly(null);
                        } else if (j > 0 && maskMatrix[i][j - 1].getAlly() == allyToAttack) {
                            maskMatrix[i][j - 1].setAlly(null);
                        }
                        System.out.println("Un alleato è stato sconfitto");
                    }
                }
            }
        }
    }

    // --- METODI DI UTILITÀ GENERICI ---

    // Rende generico il controllo sulla presenza di nemici
    public boolean hasEnemyAt(int row, int col) {
        if (row < 0 || row >= ROWS || col < 0 || col >= COLUMNS) {
            return false;
        }
        return maskMatrix[row][col].hasEnemy();
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

    public void addMonete(int monete) {
        this.monete += monete;
    }

    public boolean sottraiMonete(int costo) {
        if (this.monete >= costo) {
            this.monete -= costo;
            return true;
        }
        return false;
    }

    public boolean cellaLibera(int riga, int colonna) {
        return !maskMatrix[riga][colonna].hasAlly();
    }
}