package it.unical.uid.progettoesameuid.controller;
import it.unical.uid.progettoesameuid.view.MenuView;
import javafx.application.Platform;

import java.sql.SQLOutput;

public class MenuController {
    private Runnable onStartGame;
    private MenuView view;
    public MenuController(MenuView view, Runnable onStartGame)
    {
        this.view = view;
        this.onStartGame = onStartGame;

        menuActions();
    }

    private void menuActions(){
        view.getButtonPlay().setOnAction(event -> {
            System.out.println("Il controller Menu dice: Avvia Gioco");
            onStartGame.run();
        });

        view.getButtonExit().setOnAction(event -> {
            System.out.println("Il controller Menu dice: Esci dal gioco");
            Platform.exit();
        });

        view.getButtonSettings().setOnAction(event -> {
            System.out.println("Il controller Menu dice: Aprendo Impostazioni");
        });
    }

}
