package it.unical.uid.progettoesameuid.model;

public class Enemy extends Entity {
    int speed;
    int LogicX;

    public void move() {
        this.LogicX -= speed;
    }

    public int getLogicX() {
        return LogicX;
    }
}
