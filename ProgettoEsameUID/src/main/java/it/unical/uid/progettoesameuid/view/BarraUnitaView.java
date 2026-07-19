package it.unical.uid.progettoesameuid.view;

import it.unical.uid.progettoesameuid.controller.GiocoController;
import it.unical.uid.progettoesameuid.model.Arciere;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class BarraUnitaView extends HBox {

    private Button bottoneArciere;
    private Button bottoneCavaliere;

    public BarraUnitaView() {
        // Impostiamo lo stile di base della barra (spaziature e allineamento)
        this.setSpacing(15);
        this.setAlignment(Pos.CENTER);
        this.setStyle("-fx-background-color: rgba(62, 39, 35, 0.8); -fx-padding: 10; -fx-background-radius: 10;");

        // Inizializziamo i bottoni delle carte
        bottoneArciere = new Button("Arciere\n(50 Oro)");
        bottoneCavaliere = new Button("Cavaliere\n(100 Oro)");

        // Qui il tuo collega potrà sbizzarrirsi con la grafica (es. inserire icone PNG al posto del testo)
        bottoneArciere.setPrefSize(80, 60);
        bottoneCavaliere.setPrefSize(80, 60);

        // Aggiungiamo i bottoni alla barra
        this.getChildren().addAll(bottoneArciere, bottoneCavaliere);
    }

    // Getter per consentire al Controller di agganciare i click
    public Button getBottoneArciere() { return bottoneArciere; }
    public Button getBottoneCavaliere() { return bottoneCavaliere; }

    // Questo metodo permette al controller di passare la logica senza toccare i bottoni
    public void collegaAlController(GiocoController controller) {
        bottoneArciere.setOnAction(e ->
                controller.selezionaUnita("ARCIERE", 50, Arciere::new)
        );

       // bottoneCavaliere.setOnAction(e ->
                //controller.selezionaUnita("CAVALIERE", 100, Cavaliere::new)
        //);
    }

    public BarraUnitaView getBarraUnitaView() {
        return this;
    }
}
