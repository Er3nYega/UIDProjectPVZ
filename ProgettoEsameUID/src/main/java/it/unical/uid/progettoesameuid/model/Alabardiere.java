package it.unical.uid.progettoesameuid.model;

public class Alabardiere extends Ally {
    public Alabardiere(){
    super(500, 50, 150, 1, 2000);
}
    @Override
    public boolean doAction(MapMask map, int row, int col) {
        if (canAttack()) {
            int targetCol = col + 1;
            boolean haAttaccato = false;
            for (int r = row - 1; r <= row + 1; r++) {
                if (r >= 0 && r < map.getRows() && map.hasEnemyAt(r, targetCol)) {
                    Enemy bersaglio = map.getMaskMatrix()[r][targetCol].getEnemyList().getFirst();
                    bersaglio.beDamaged(this.damage);
                    haAttaccato = true;
                }
            }
            if (haAttaccato) {
                resetCooldown();
                return true; // 🎯 Attacco eseguito
            }
        }
        return false;
    }
}
