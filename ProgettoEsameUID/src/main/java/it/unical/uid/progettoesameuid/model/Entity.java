package it.unical.uid.progettoesameuid.model;

public abstract class Entity {
    // Usiamo protected così Ally e Enemy possono leggere e modificare queste variabili
    protected int hp;
    protected int maxHp;
    protected int damage;

    // Aggiungiamo il costruttore che mancava!
    public Entity(int hp) {
        this.hp = hp;
        this.maxHp = hp; // All'inizio la vita attuale è pari alla vita massima
    }

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