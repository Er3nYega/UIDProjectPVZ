package it.unical.uid.progettoesameuid.model;

import java.util.ArrayList;
import java.util.List;

public class Cell {
    private Ally ally;
    private List<Enemy> enemyList;

    // Costruttore: inizializziamo la lista dei nemici vuota per ogni cella
    public Cell() {
        this.ally = null;
        this.enemyList = new ArrayList<>();
    }

    public Ally getAlly() {
        return this.ally;
    }

    public void setAlly(Ally ally){
        this.ally = ally;
    }

    public List<Enemy> getEnemyList() {
        return enemyList;
    }

    // Metodi di utilità per aggiungere e rimuovere nemici al volo
    public void addEnemy(Enemy enemy) {
        this.enemyList.add(enemy);
    }

    public void removeEnemy(Enemy enemy) {
        this.enemyList.remove(enemy);
    }

    public boolean hasAlly(){
        return this.ally != null;
    }

    // restituisce true solo se ci sono EFFETTIVAMENTE nemici nella lista
    public boolean hasEnemy(){
        return enemyList != null && !enemyList.isEmpty();
    }
}