package it.unical.uid.progettoesameuid.model;

public class Prete extends Ally {

    public Prete() {

        super(300, 50, 175, 4, 3000);
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
                    return true; // 🎯 Attacco eseguito
                }
            }
        }
        return false;
    }
}