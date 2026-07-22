package it.unical.uid.progettoesameuid.model;

public class Cavaliere extends Ally {

    public Cavaliere() {
        // hp: 200, danno: 30, costo: 150, range: 1, cooldown: 1200ms
        super(200, 30, 150, 1, 1200L);
    }

    @Override
    public boolean doAction(MapMask map, int row, int col) {
        if (canAttack()) {
            boolean nemicoTrovato = false;

            // Controlla sia la propria casella (col) sia quella davanti (col + 1)
            for (int i = col; i <= col + getRange() && i < map.getColumns(); i++) {
                if (map.hasEnemyAt(row, i)) {
                    // Infligge danno al nemico presente nella casella
                    Enemy bersaglio = map.getMaskMatrix()[row][i].getEnemyList().getFirst();
                    bersaglio.beDamaged(this.damage);
                    nemicoTrovato = true;
                    break;
                }
            }

            if (nemicoTrovato) {
                resetCooldown();
                return true; // 🎯 Attacco melee eseguito!
            }
        }
        return false;
    }
}