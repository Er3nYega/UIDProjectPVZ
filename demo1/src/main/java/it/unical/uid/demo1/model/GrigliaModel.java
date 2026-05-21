package it.unical.uid.demo1.model;

public class GrigliaModel {
    private final int RIGHE = 5;
    private final int COLONNE = 9;

    private Pianta[][] matriceCelle;

    public GrigliaModel(){
        matriceCelle = new Pianta[RIGHE][COLONNE];
    }

    public boolean posizionaPianta(int riga, int colonna, Pianta selectedPianta){
        if(matriceCelle[riga][colonna] == null){
            matriceCelle[riga][colonna] = selectedPianta;
            return true;
        }
        return false;
    }
    public Pianta getPianta(int riga, int colonna){
        return matriceCelle[riga][colonna];
    }
}
