package it.unical.uid.progettoesameuid.model;

public abstract class Entity {
    int hp;
    int maxHp;
    int damage;

    public void beDamaged(int damage){
        this.hp -= damage;
    }
    public int getHp() {
        return this.hp;
    }
    public boolean isDead() {
        return this.hp <= 0;
    }

}
