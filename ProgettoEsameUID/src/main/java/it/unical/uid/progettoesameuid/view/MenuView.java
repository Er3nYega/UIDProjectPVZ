package it.unical.uid.progettoesameuid.view;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class MenuView extends VBox{
    private Button buttonPlay;
    private Button buttonSettings;
    private Button buttonExit;

    public MenuView() {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(10);

        Text title = new Text("MVZ");
        title.setFont(new Font("Arial", 40));

        buttonPlay = new Button("Play");
        buttonSettings = new Button("Settings");
        buttonExit = new Button("Exit");

        buttonPlay.setPrefSize(200, 50);
        buttonSettings.setPrefSize(200, 50);
        buttonExit.setPrefSize(200, 50);

        this.getChildren().addAll(title,buttonPlay,buttonSettings,buttonExit);
    }

    public Button getButtonPlay() {return buttonPlay;}
    public Button getButtonSettings() {return buttonSettings;}
    public Button getButtonExit() {return buttonExit;}
}
