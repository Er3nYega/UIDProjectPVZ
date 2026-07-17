package it.unical.uid.progettoesameuid.model;

import javafx.scene.layout.ColumnConstraints;

public class MapMask {
    public Cell[][] maskMatrix;
    private final int ROWS = 9; // Nota: di solito PVZ ha 5 righe, ma se preferisci 9 va benissimo!
    private final int COLOUMNS = 9;

    //monete
    private int monete = 150;


    public MapMask() {
        maskMatrix = new Cell[ROWS][COLOUMNS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLOUMNS; j++) {
                maskMatrix[i][j] = new Cell();
            }
        }
    }

    public void onGame() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLOUMNS; j++) {
                Cell currentCell = maskMatrix[i][j];

                if (currentCell.hasEnemy()) {
                    boolean battle = false;

                    // Dichiariamo la variabile usando la classe astratta Ally!
                    Ally allyToAttack = null;

                    // Facciamo agire l'alleato se presente in questa cella
                    if (currentCell.hasAlly()) {
                        currentCell.getAlly().doAction(MapMask.this, i, j);
                    }

                    // Controlliamo dove si trova l'alleato da attaccare
                    if (currentCell.hasAlly()) {
                        battle = true;
                        allyToAttack = currentCell.getAlly(); // Polimorfismo in azione
                    } else if (j > 0 && maskMatrix[i][j - 1].hasAlly()) {
                        battle = true;
                        allyToAttack = maskMatrix[i][j - 1].getAlly(); // Polimorfismo in azione
                    }

                    // Iteriamo sui nemici presenti nella cella
                    for (Enemy e : currentCell.getEnemyList()) {
                        if (battle && allyToAttack != null) {
                            // Anche se Ally è astratta, beDamaged() risponderà correttamente
                            allyToAttack.beDamaged(e.damage);
                            System.out.println("Uno zombie sta attaccando una pianta!");
                        } else {
                            e.move();

                            int newColumn = (int) e.getLogicX();
                            if (newColumn < 0 || newColumn >= COLOUMNS) {
                                // Qui gestirai la sconfitta o l'uscita del nemico
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


    public int getMonete() {
        return monete;
    }
    public void addMonete(int monete) {
        this.monete += monete;
    }

    public boolean sottraiMonete(int costo) {
        if(this.monete >= costo){
            this.monete -= costo;
            return true;
        }
        return false;
    }

    public boolean cellaLibera(int riga, int colonna) {
        return !maskMatrix[riga][colonna].hasAlly();
    }

    public Cell[][] getMaskMatrix() {
        return maskMatrix;
    }
}