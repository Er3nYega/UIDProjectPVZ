package it.unical.uid.progettoesameuid.model;

public class Arciere extends Ally {

    public Arciere() {
        // HP = 100, Danno = 20, Costo = 100, Raggio = 5 celle, Cooldown = 1500ms
        super(100, 20, 100, 5, 1500);
    }

    @Override
    public void doAction(MapMask map, int row, int col) {
        // 1. Controlla se il tempo di ricarica è passato
        if (canAttack()) {

            // 2. Scansiona la riga davanti a sé nel suo raggio d'azione
            boolean nemicoTrovato = false;
            for (int i = col + 1; i <= col + getRange() && i < map.getColumns(); i++) {
                if (map.hasEnemyAt(row, i)) {
                    nemicoTrovato = true;
                    break;
                }
            }

            // 3. Se c'è un nemico, "spara" (innesca lo spawn del proiettile)
            if (nemicoTrovato) {
                System.out.println("L'Arciere in [" + row + "][" + col + "] ha avvistato un bersaglio. Creazione freccia...");

                // NOTA PER IL FUTURO:
                // Qui chiamerai un metodo per aggiungere il proiettile al gioco, ad esempio:
                // map.addProjectile(new Freccia(row, col, this.damage));

                // Fai ripartire il timer del cooldown
                resetCooldown();
            }
        }
    }
}