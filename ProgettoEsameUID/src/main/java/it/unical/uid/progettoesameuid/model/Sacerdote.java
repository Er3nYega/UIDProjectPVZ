package it.unical.uid.progettoesameuid.model;

public class Sacerdote extends Ally {

    public Sacerdote() {

        super(100, 0, 50, 0, 10000);
    }

    @Override
    public void doAction(MapMask map, int row, int col) {

        if (canAttack()) {
            map.addMonete(25); // Genera 25 monete
            resetCooldown();
        }
    }
}