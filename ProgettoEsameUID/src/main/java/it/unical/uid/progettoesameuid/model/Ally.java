package it.unical.uid.progettoesameuid.model;

public abstract class Ally extends Entity{
    int id;
    int cost;

    public Ally(int hp, int danno, int costo) {
        super();
    }

    //funzione per gli upgrade
    public void upgradeHp(int hp) {
        this.hp = hp;
    }
    public int getCost() {
        return cost;
    }

    public abstract void doAction(MapMask map, int row, int column);

}
