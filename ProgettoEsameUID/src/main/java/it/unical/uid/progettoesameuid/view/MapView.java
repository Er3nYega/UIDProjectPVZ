package it.unical.uid.progettoesameuid.view;


import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class MapView extends BorderPane { // Usiamo BorderPane per organizzare lo schermo

    private GridPane grigliaMappa;
    private Text testoMonete;

    public MapView() {
        // 1. Barra superiore per le risorse (Monete/Sole)
        HBox barraSuperiore = new HBox();
        barraSuperiore.setStyle("-fx-background-color: #3e2723; -fx-padding: 10;"); // Sfondo marrone legno
        testoMonete = new Text("Monete d'Oro: 150");
        testoMonete.setStyle("-fx-fill: gold; -fx-font-size: 20px; -fx-font-weight: bold;");
        barraSuperiore.getChildren().add(testoMonete);

        // Posizioniamo la barra in alto
        this.setTop(barraSuperiore);

        // 2. Creazione della mappa tramite GridPane
        grigliaMappa = new GridPane();
        grigliaMappa.setAlignment(Pos.CENTER_LEFT);
        grigliaMappa.setGridLinesVisible(true); // Rende visibili le linee della griglia (utilissimo per i test!)

        // Creiamo una griglia tipica di PvZ: 5 righe per 9 colonne
        for (int riga = 0; riga < 5; riga++) {
            for (int colonna = 0; colonna < 9; colonna++) {
                // Creiamo una cella visiva (un pannello vuoto con dimensioni fisse)
                javafx.scene.layout.StackPane cellaVisiva = new javafx.scene.layout.StackPane();
                cellaVisiva.setPrefSize(90, 90); // Ogni cella è un quadrato di 90x90 pixel

                // Coloriamo le celle alterne stile scacchiera (erba chiara / erba scura medievale)
                if ((riga + colonna) % 2 == 0) {
                    cellaVisiva.setStyle("-fx-background-color: #4caf50;"); // Verde chiaro
                } else {
                    cellaVisiva.setStyle("-fx-background-color: #388e3c;"); // Verde scuro
                }

                // Aggiungiamo la cella alla griglia (colonna, riga)
                grigliaMappa.add(cellaVisiva, colonna, riga);
            }
        }

        // Posizioniamo la griglia al centro dello schermo
        this.setCenter(grigliaMappa);
    }

    public void aggiornaMoneteGrafica(int monete){
        testoMonete.setText("Monete d'Oro: " + monete);
    }

    public void addAllyGraphic(int rows, int columns, AllyView allyView){
        for (Node node : grigliaMappa.getChildren()) {
            Integer r = GridPane.getRowIndex(node);
            Integer c = GridPane.getColumnIndex(node);

            int rowNode = (r == null) ? 0 : r;
            int colNode = (c == null) ? 0 : c;
            if (rowNode == rows && colNode == columns) {
                if (node instanceof javafx.scene.layout.StackPane) {
                    ((javafx.scene.layout.StackPane) node).getChildren().add(allyView);
                }
                break;
            }
        }
    }

    public GridPane getGrigliaMappa() {
        return grigliaMappa;
    }
}
