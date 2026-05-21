package it.unical.uid.demo1.view;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class GrigliaView {
    private GridPane gridPane;
    private final int RIGHE = 5;
    private final int COLONNE = 9;
    private final int DIM_CELL = 90;

    public GrigliaView(){
        gridPane = new GridPane();
        initializeGrid();
    }

    public void initializeGrid(){
        for(int i = 0; i < RIGHE; i++){
            for(int j = 0; j < COLONNE; j++){
                StackPane gCell = new StackPane();
                gCell.setPrefSize(DIM_CELL, DIM_CELL);
                //crea un bordo leggero e background trasparente
                gCell.setStyle("-fx-border-color: rgba(0,0,0, 0.3); -fx-background-color: Green;");
                gCell.setUserData(new int[]{i, j}); //per recuperare le coordinate al click
                gridPane.add(gCell, j, i);
            }
        }
    }
    public GridPane getGridPane() {
        return gridPane;
    }

}
