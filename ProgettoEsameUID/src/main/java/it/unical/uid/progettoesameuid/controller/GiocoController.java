package it.unical.uid.progettoesameuid.controller;

import it.unical.uid.progettoesameuid.model.MapMask;
import it.unical.uid.progettoesameuid.model.Arciere; // Assicurati di avere una classe concreta!
import it.unical.uid.progettoesameuid.view.AllyView;
import it.unical.uid.progettoesameuid.view.MapView;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class GiocoController {

    private MapMask model;
    private MapView view;

    public GiocoController(MapMask model, MapView view) {
        this.model = model;
        this.view = view;

        // Sincronizziamo subito il testo delle monete all'avvio
        this.view.aggiornaMoneteGrafica(this.model.getMonete());

        inizializzaInterazioni();
    }

    private void inizializzaInterazioni() {
        // Scorriamo tutte le celle visive del GridPane per agganciare l'evento di Click
        for (Node nodo : view.getGrigliaMappa().getChildren()) {
            if (nodo instanceof StackPane) {
                StackPane cellaVisiva = (StackPane) nodo;

                cellaVisiva.setOnMouseClicked(event -> {
                    // Recuperiamo le coordinate cliccate
                    int rigaCliccata = GridPane.getRowIndex(cellaVisiva);
                    int colonnaCliccata = GridPane.getColumnIndex(cellaVisiva);

                    gestisciPiazzamentoAlleato(rigaCliccata, colonnaCliccata);
                });
            }
        }
    }

    private void scegliAlleato(){

    }

    private void gestisciPiazzamentoAlleato(int riga, int colonna) {
        int costoArciere = 50; // Costo fittizio per il test

        // 1. Controllo Logico: C'è già una pianta?
        if (!model.cellaLibera(riga, colonna)) {
            System.out.println("Piazzamento fallito: Cella già occupata!");
            return;
        }

        // 2. Controllo Logico: Abbiamo abbastanza oro?
        if (model.sottraiMonete(costoArciere)) {
            // AGGIORNAMENTO MODEL:
            // Creiamo l'alleato logico e lo mettiamo nella nostra maschera logica
            Arciere nuovoArciereLogico = new Arciere(); // Deve ereditare da Ally
            model.getMaskMatrix()[riga][colonna].setAlly(nuovoArciereLogico);

            // AGGIORNAMENTO VIEW:
            // 1. Aggiorniamo la barra dell'oro visibile in alto
            view.aggiornaMoneteGrafica(model.getMonete());

            // 2. Creiamo l'aspetto visivo e lo posizioniamo sulla griglia grafica
            AllyView arciereVisivo = new AllyView("arciere"); // Caricherà arciere.png
            view.addAllyGraphic(riga, colonna, arciereVisivo);

            System.out.println("Arciere posizionato con successo in [" + riga + "][" + colonna + "]!");
        } else {
            System.out.println("Oro insufficiente! Ti servono " + costoArciere + " monete.");
        }
    }
}