package it.unical.uid.progettoesameuid.model;

public class Cavaliere extends Ally {

    public Cavaliere() {

        super(1000, 20, 125, 1, 5000);
    }

    @Override
    public void doAction(MapMask map, int row, int col) {

        if (canAttack() && map.hasEnemyAt(row, col + 1)) {
            // Usiamo getFirst() per consistenza e pulizia
            Enemy bersaglio = map.getMaskMatrix()[row][col + 1].getEnemyList().getFirst();
            bersaglio.beDamaged(this.damage);
            System.out.println("Il Cavaliere in [" + row + "][" + col + "] attacca il nemico davanti a sé!");
            resetCooldown();
        }
    }
}