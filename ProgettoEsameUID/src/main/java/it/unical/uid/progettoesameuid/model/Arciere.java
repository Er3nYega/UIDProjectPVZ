package it.unical.uid.progettoesameuid.model;

public class Arciere extends Ally {

    public Arciere() {
        // Chiamiamo il costruttore della classe madre (Ally)
        // passando: HP = 100, Danno = 20, Costo = 50 monete
        super(100, 20, 50);
    }

    @Override
    public void doAction(MapMask map, int row, int col) {
        // Per ora stampiamo solo un messaggio in console per testare che funzioni.
        // Nella Fase 3 (Game Loop) qui faremo spawnare una freccia!
        System.out.println("L'Arciere in [" + row + "][" + col + "] sta prendendo la mira...");
    }
}
