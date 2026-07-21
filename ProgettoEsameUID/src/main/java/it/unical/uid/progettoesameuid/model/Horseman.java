package it.unical.uid.progettoesameuid.model;

public class Horseman extends Ally {

    public Horseman() {

        super(10000, 1000, 200, 1, 100);
    }

    @Override
    public void doAction(MapMask map, int row, int col) {

        // Infligge danni a chiunque sia nella sua cella attuale
        for (Enemy e : map.getMaskMatrix()[row][col].getEnemyList()) {
            e.beDamaged(this.damage);
        }

// Si sposta in avanti nella griglia
        int prossimaColonna = col + 1;
        map.getMaskMatrix()[row][col].setAlly(null); // Lascia la vecchia cella

        if (prossimaColonna < map.getColumns()) {
            map.getMaskMatrix()[row][prossimaColonna].setAlly(this); // Entra nella nuova cella
        } else {
            // È uscito dallo schermo, il suo compito è finito
            System.out.println("L'Horseman ha completato la carica ed è uscito di scena!");
        }
    }
}