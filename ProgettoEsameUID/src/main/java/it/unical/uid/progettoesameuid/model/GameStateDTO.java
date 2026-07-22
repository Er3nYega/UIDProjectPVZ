package it.unical.uid.progettoesameuid.model;

import java.util.ArrayList;
import java.util.List;

public class GameStateDTO {
    // Dentro GameStateDTO.java
    public int slotId = 1;
    public String dataSalvataggio;
    public int monete;
    public int ondataAttuale;
    public int nemiciUccisi;
    public int alleatiPiazzati;
    public int moneteSpese;

    public List<CellDataDTO> griglia = new ArrayList<>();
    public List<EnemyDataDTO> nemici = new ArrayList<>();

    public static class CellDataDTO {
        public int riga;
        public int colonna;
        public String tipoAlleato;
        public int hpRimanenti;
    }

    public static class EnemyDataDTO {
        public int riga;
        public String tipoNemico;
        public double posX;
        public double posY;
        public int hpRimanenti;
    }
}