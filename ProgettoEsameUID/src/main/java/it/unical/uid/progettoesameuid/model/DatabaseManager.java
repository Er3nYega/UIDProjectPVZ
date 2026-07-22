package it.unical.uid.progettoesameuid.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:gioco_pvz.db";

    public DatabaseManager() {
        creaTabelleSeNonEsistono();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void creaTabelleSeNonEsistono() {
        String sqlPartita = """
            CREATE TABLE IF NOT EXISTS partita (
                slot_id INTEGER PRIMARY KEY,
                data_salvataggio TEXT NOT NULL,
                ondata INTEGER NOT NULL,
                monete INTEGER NOT NULL,
                nemici_uccisi INTEGER NOT NULL,
                alleati_piazzati INTEGER NOT NULL,
                monete_spese INTEGER NOT NULL
            );
        """;

        String sqlAlleati = """
            CREATE TABLE IF NOT EXISTS alleati_piazzati (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                slot_id INTEGER NOT NULL,
                riga INTEGER NOT NULL,
                colonna INTEGER NOT NULL,
                tipo_alleato TEXT NOT NULL,
                hp_rimanenti INTEGER NOT NULL
            );
        """;

        String sqlNemici = """
            CREATE TABLE IF NOT EXISTS nemici_attivi (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                slot_id INTEGER NOT NULL,
                riga INTEGER NOT NULL,
                tipo_nemico TEXT NOT NULL,
                pos_x REAL NOT NULL,
                pos_y REAL NOT NULL,
                hp_rimanenti INTEGER NOT NULL
            );
        """;

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // 🎯 Controllo di sicurezza: se manca la colonna data_salvataggio, ricreiamo le tabelle
            boolean haDataSalvataggio = false;
            try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(partita);")) {
                while (rs.next()) {
                    if ("data_salvataggio".equals(rs.getString("name"))) {
                        haDataSalvataggio = true;
                        break;
                    }
                }
            } catch (SQLException ignored) {}

            if (!haDataSalvataggio) {
                stmt.executeUpdate("DROP TABLE IF EXISTS partita;");
                stmt.executeUpdate("DROP TABLE IF EXISTS alleati_piazzati;");
                stmt.executeUpdate("DROP TABLE IF EXISTS nemici_attivi;");
                System.out.println("🔄 Aggiornamento schema DB: colonna 'data_salvataggio' aggiunta!");
            }

            stmt.execute(sqlPartita);
            stmt.execute(sqlAlleati);
            stmt.execute(sqlNemici);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean salvaPartita(GameStateDTO dto) {
        if (dto.dataSalvataggio == null) {
            java.time.LocalDateTime ora = java.time.LocalDateTime.now();
            java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            dto.dataSalvataggio = ora.format(fmt);
        }

        String queryPartita = """
            INSERT INTO partita (slot_id, data_salvataggio, ondata, monete, nemici_uccisi, alleati_piazzati, monete_spese)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(slot_id) DO UPDATE SET
                data_salvataggio = excluded.data_salvataggio,
                ondata = excluded.ondata,
                monete = excluded.monete,
                nemici_uccisi = excluded.nemici_uccisi,
                alleati_piazzati = excluded.alleati_piazzati,
                monete_spese = excluded.monete_spese;
        """;

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(queryPartita)) {
                pstmt.setInt(1, dto.slotId);
                pstmt.setString(2, dto.dataSalvataggio);
                pstmt.setInt(3, dto.ondataAttuale);
                pstmt.setInt(4, dto.monete);
                pstmt.setInt(5, dto.nemiciUccisi);
                pstmt.setInt(6, dto.alleatiPiazzati);
                pstmt.setInt(7, dto.moneteSpese);
                pstmt.executeUpdate();
            }

            try (PreparedStatement delA = conn.prepareStatement("DELETE FROM alleati_piazzati WHERE slot_id = ?;");
                 PreparedStatement delN = conn.prepareStatement("DELETE FROM nemici_attivi WHERE slot_id = ?;")) {
                delA.setInt(1, dto.slotId);
                delA.executeUpdate();
                delN.setInt(1, dto.slotId);
                delN.executeUpdate();
            }

            String insAlleato = "INSERT INTO alleati_piazzati (slot_id, riga, colonna, tipo_alleato, hp_rimanenti) VALUES (?, ?, ?, ?, ?);";
            try (PreparedStatement pstmt = conn.prepareStatement(insAlleato)) {
                for (var cella : dto.griglia) {
                    pstmt.setInt(1, dto.slotId);
                    pstmt.setInt(2, cella.riga);
                    pstmt.setInt(3, cella.colonna);
                    pstmt.setString(4, cella.tipoAlleato);
                    pstmt.setInt(5, cella.hpRimanenti);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            String insNemico = "INSERT INTO nemici_attivi (slot_id, riga, tipo_nemico, pos_x, pos_y, hp_rimanenti) VALUES (?, ?, ?, ?, ?, ?);";
            try (PreparedStatement pstmt = conn.prepareStatement(insNemico)) {
                for (var nemico : dto.nemici) {
                    pstmt.setInt(1, dto.slotId);
                    pstmt.setInt(2, nemico.riga);
                    pstmt.setString(3, nemico.tipoNemico);
                    pstmt.setDouble(4, nemico.posX);
                    pstmt.setDouble(5, nemico.posY);
                    pstmt.setInt(6, nemico.hpRimanenti);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public GameStateDTO caricaPartita(int slotId) {
        String queryPartita = "SELECT * FROM partita WHERE slot_id = ?;";
        GameStateDTO dto = new GameStateDTO();
        dto.slotId = slotId;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(queryPartita)) {
            pstmt.setInt(1, slotId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                dto.dataSalvataggio = rs.getString("data_salvataggio");
                dto.ondataAttuale = rs.getInt("ondata");
                dto.monete = rs.getInt("monete");
                dto.nemiciUccisi = rs.getInt("nemici_uccisi");
                dto.alleatiPiazzati = rs.getInt("alleati_piazzati");
                dto.moneteSpese = rs.getInt("monete_spese");
            } else {
                return null;
            }

            try (PreparedStatement pA = conn.prepareStatement("SELECT * FROM alleati_piazzati WHERE slot_id = ?;")) {
                pA.setInt(1, slotId);
                ResultSet rsA = pA.executeQuery();
                while (rsA.next()) {
                    var c = new GameStateDTO.CellDataDTO();
                    c.riga = rsA.getInt("riga");
                    c.colonna = rsA.getInt("colonna");
                    c.tipoAlleato = rsA.getString("tipo_alleato");
                    c.hpRimanenti = rsA.getInt("hp_rimanenti");
                    dto.griglia.add(c);
                }
            }

            try (PreparedStatement pN = conn.prepareStatement("SELECT * FROM nemici_attivi WHERE slot_id = ?;")) {
                pN.setInt(1, slotId);
                ResultSet rsN = pN.executeQuery();
                while (rsN.next()) {
                    var n = new GameStateDTO.EnemyDataDTO();
                    n.riga = rsN.getInt("riga");
                    n.tipoNemico = rsN.getString("tipo_nemico");
                    n.posX = rsN.getDouble("pos_x");
                    n.posY = rsN.getDouble("pos_y");
                    n.hpRimanenti = rsN.getInt("hp_rimanenti");
                    dto.nemici.add(n);
                }
            }

            return dto;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int trovaPrimoSlotLibero() {
        String query = "SELECT slot_id FROM partita;";
        List<Integer> slotOccupati = new ArrayList<>();
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) slotOccupati.add(rs.getInt("slot_id"));
            for (int slot = 1; slot <= 3; slot++) {
                if (!slotOccupati.contains(slot)) return slot;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    /**
     * Elimina completamente un salvataggio dal DB (usato al Game Over)
     */
    public boolean eliminaSlot(int slotId) {
        String queryPartita = "DELETE FROM partita WHERE slot_id = ?;";
        String queryAlleati = "DELETE FROM alleati_piazzati WHERE slot_id = ?;";
        String queryNemici = "DELETE FROM nemici_attivi WHERE slot_id = ?;";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false); // Inizio transazione

            try (PreparedStatement stmtPartita = conn.prepareStatement(queryPartita);
                 PreparedStatement stmtAlleati = conn.prepareStatement(queryAlleati);
                 PreparedStatement stmtNemici = conn.prepareStatement(queryNemici)) {

                // 1. Elimina dalla tabella principale
                stmtPartita.setInt(1, slotId);
                stmtPartita.executeUpdate();

                // 2. Elimina le entità salvate per quel determinato slot
                stmtAlleati.setInt(1, slotId);
                stmtAlleati.executeUpdate();

                stmtNemici.setInt(1, slotId);
                stmtNemici.executeUpdate();

                conn.commit();
                System.out.println("🗑️ Slot " + slotId + " eliminato con successo dal DB!");
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}