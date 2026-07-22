package it.unical.uid.progettoesameuid.model;

public class Soldato extends Ally {

    public Soldato() {

        super(200, 25, 75, 1, 1000);
    }

    @Override
    public boolean doAction(MapMask map, int row, int col) {
        if (canAttack() && map.hasEnemyAt(row, col + 1)) {
            Enemy bersaglio = map.getMaskMatrix()[row][col + 1].getEnemyList().getFirst();
            bersaglio.beDamaged(this.damage);
            System.out.println("Il Soldato in [" + row + "][" + col + "] attacca il nemico davanti a sé!");
            resetCooldown();
            return true; // 🎯 Attacco eseguito
        }
        return false;
    }
}