package it.unical.uid.progettoesameuid.model;

public class Templare extends Ally {

    public Templare() {

        super(750, 50, 175, 1, 2500);
    }

    @Override
    public void doAction(MapMask map, int row, int col) {

        if (canAttack() && map.hasEnemyAt(row, col + 1)) {
            Enemy bersaglio = map.getMaskMatrix()[row][col + 1].getEnemyList().getFirst();
            bersaglio.beDamaged(this.damage);

            // Cura se stesso
            this.hp = Math.min(this.maxHp, this.hp + 10); // Cura di 15 HP a colpo
            resetCooldown();
        }
    }
}