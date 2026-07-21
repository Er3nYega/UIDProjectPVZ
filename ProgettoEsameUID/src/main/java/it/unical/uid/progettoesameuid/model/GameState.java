package it.unical.uid.progettoesameuid.model;

import java.io.Serializable;
import java.util.List;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    public int ondataAttuale;
    public int monete;
    public List<String> nomiAlleatiNegliSlot; // I 6 personaggi sbloccati nella barra

    // Dati delle entità presenti sulla griglia
    public List<AllyData> alleatiPiazzati;
    public List<EnemyData> nemiciInCampo;

    public static class AllyData implements Serializable {
        public String nome;
        public int riga, colonna;
        public int hpAttuali;
    }

    public static class EnemyData implements Serializable {
        public String tipo;
        public double posX, posY;
        public int riga;
        public int hpAttuali;
    }
}