package it.unical.uid.progettoesameuid.model;

public class Horseman extends Ally {

    public Horseman() {
        // hp: 10000, danno: 1000, costo: 200, range: 1, attackCooldown: 300ms tra un passo e l'altro
        super(10000, 1000, 200, 1, 300L);
    }

    @Override
    public boolean doAction(MapMask map, int row, int col) {
        if (!canAttack()) return false;

        boolean haColpito = false;

        // 1. Infligge danno a TUTTI i nemici presenti nella cella attuale
        var nemiciInCella = map.getMaskMatrix()[row][col].getEnemyList();
        if (!nemiciInCella.isEmpty()) {
            for (Enemy e : nemiciInCella) {
                e.beDamaged(this.damage);
                haColpito = true;
            }
        }

        // 2. Avanza di una casella verso destra nel Model
        int prossimaColonna = col + 1;
        map.getMaskMatrix()[row][col].setAlly(null);

        if (prossimaColonna < map.getColumns()) {
            map.getMaskMatrix()[row][prossimaColonna].setAlly(this);
        } else {
            System.out.println("🐎 L'Horseman ha completato la carica ed è uscito di scena!");
        }

        resetCooldown();
        return true;
    }
}