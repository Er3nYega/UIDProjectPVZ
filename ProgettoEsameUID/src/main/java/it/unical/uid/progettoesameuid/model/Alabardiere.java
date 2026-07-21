package it.unical.uid.progettoesameuid.model;

public class Alabardiere extends Ally {
    public Alabardiere(){
    super(500, 50, 150, 1, 2000);
}
    @Override
    public void doAction(MapMask map, int row, int col) {
        // 1. Controlla se il tempo di ricarica è passato
        if (canAttack()) {
            int targetCol = col + 1;
            boolean haAttaccato = false;
            // Controlla le tre righe (con protezione per non uscire dai bordi della mappa)
            for (int r = row - 1; r <= row + 1; r++) {
                if (r >= 0 && r < map.getRows() && map.hasEnemyAt(r, targetCol)) {
                    Enemy bersaglio = map.getMaskMatrix()[r][targetCol].getEnemyList().getFirst();
                    bersaglio.beDamaged(this.damage);
                    haAttaccato = true;
                }
            }
            if (haAttaccato) resetCooldown();
        }

    }
}
