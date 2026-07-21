package it.unical.uid.progettoesameuid.model;

public class Enemy extends Entity {
    protected int speed;
    protected int logicX; // Convenzione Java: minuscolo all'inizio

    // Il costruttore che risolve l'errore richiamando super(hp)
    public Enemy(int hp, int damage, int speed, int startColumn) {
        super(hp);
        this.damage = damage; // Assegna il danno ereditato da Entity
        this.speed = speed;
        this.logicX = startColumn; // Di solito i nemici partono dall'ultima colonna a destra
    }

    public void move() {
        this.logicX -= speed;
    }

    public int getLogicX() {
        return logicX;
    }
}