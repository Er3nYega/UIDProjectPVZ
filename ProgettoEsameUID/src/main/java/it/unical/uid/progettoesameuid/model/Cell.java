package it.unical.uid.progettoesameuid.model;

import java.util.List;

public class Cell {
    private Ally ally;
    private List<Enemy> enemyList;

    public Ally getAlly() {
        return this.ally;
    }
    public void setAlly(Ally ally){
        this.ally = ally;
    }

    public List<Enemy> getEnemyList() {
        return enemyList;
    }

    public void setEnemyList(List<Enemy> enemyList) {
        this.enemyList = enemyList;
    }

    public boolean hasAlly(){
        return this.ally != null;
    }

    public boolean hasEnemy(){
        return this.enemyList != null;
    }
}
