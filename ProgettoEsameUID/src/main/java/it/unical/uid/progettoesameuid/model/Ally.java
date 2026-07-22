package it.unical.uid.progettoesameuid.model;

public abstract class Ally extends Entity {
    protected int id;
    protected int cost;
    protected int damage;
    protected int range;       // Quante celle in avanti può colpire (es. 1 per corpo a corpo, 9 per tutta la riga)
    protected long attackCooldown; // Tempo di attesa tra un attacco e l'altro (in millisecondi)
    protected long lastAttackTime; // Memorizza quando ha attaccato l'ultima volta

    public Ally(int hp, int damage, int cost, int range, long attackCooldown) {
        // Assegniamo i valori ereditati da Entity (assumendo che Entity gestisca gli HP)
        super(hp);
        this.damage = damage;
        this.cost = cost;
        this.range = range;
        this.attackCooldown = attackCooldown;
        this.lastAttackTime = 0; // Pronto ad attaccare subito
    }

    // Metodo per verificare se l'alleato può attaccare di nuovo (basato sul tempo)
    public boolean canAttack() {
        return System.currentTimeMillis() - lastAttackTime >= attackCooldown;
    }

    // Resetta il timer dell'attacco dopo che ha agito
    public void resetCooldown() {
        this.lastAttackTime = System.currentTimeMillis();
    }

    // Funzione per gli upgrade
    public void upgradeHp(int additionalHp) {
        this.hp += additionalHp; // Incrementa gli HP attuali
    }

    public int getCost() { return cost; }
    public int getDamage() { return damage; }
    public int getRange() { return range; }

    // Il metodo doAction rimane astratto: ogni alleato farà qualcosa di diverso
    public abstract boolean doAction(MapMask map, int row, int column);
}
