package it.unical.uid.progettoesameuid.model;

public class Stregone extends Ally {

    public Stregone() {

        super(100, 100, 250, 5, 5000);
    }

    @Override
    public boolean doAction(MapMask map, int row, int col) {
        if (canAttack()) {
            for (int c = col + 1; c < map.getColumns(); c++) {
                if (map.hasEnemyAt(row, c)) {
                    for (int rArea = row - 1; rArea <= row + 1; rArea++) {
                        for (int cArea = c - 1; cArea <= c + 1; cArea++) {
                            if (rArea >= 0 && rArea < map.getRows() && cArea >= 0 && cArea < map.getColumns()) {
                                for (Enemy e : map.getMaskMatrix()[rArea][cArea].getEnemyList()) {
                                    e.beDamaged(this.damage);
                                }
                            }
                        }
                    }
                    resetCooldown();
                    return true; // 🎯 Attacco ad area eseguito
                }
            }
        }
        return false;
    }
}