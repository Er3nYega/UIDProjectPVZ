package it.unical.uid.progettoesameuid.model;

public class Spadaccino extends Ally {

    public Spadaccino() {

        super(150, 20, 150, 2, 500);
    }

    @Override
    public void doAction(MapMask map, int row, int col) {

        if (canAttack()) {
            for (int c = col + 1; c <= col + 2 && c < map.getColumns(); c++) {
                if (map.hasEnemyAt(row, c)) {
                    Enemy bersaglio = map.getMaskMatrix()[row][c].getEnemyList().getFirst();
                    bersaglio.beDamaged(this.damage);
                    resetCooldown();
                    break; // Colpisce un solo bersaglio per turno
                }
            }
        }
    }
}