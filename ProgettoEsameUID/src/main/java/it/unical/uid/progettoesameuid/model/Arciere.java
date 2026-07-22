package it.unical.uid.progettoesameuid.model;

public class Arciere extends Ally {

    public Arciere() {
        // Parametri: hp (100), danno (20), costo (100 oro), range (5 colonne), attackCooldown (1500 ms)
        super(100, 20, 100, 5, 1500L);
    }

    @Override
    public boolean doAction(MapMask map, int row, int col) {
        if (canAttack()) {
            boolean nemicoTrovato = false;

            // Scansione della riga dalla casella adiacente fino al massimo range visibile
            for (int i = col + 1; i <= col + getRange() && i < map.getColumns(); i++) {
                if (map.hasEnemyAt(row, i)) {
                    nemicoTrovato = true;
                    break;
                }
            }

            if (nemicoTrovato) {
                resetCooldown(); // Fai ripartire il cooldown da 1.5s ereditato da Ally
                return true;     // 🎯 Attacco eseguito in questo frame!
            }
        }
        return false; // Nessun attacco eseguito
    }
}