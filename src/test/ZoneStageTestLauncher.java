package test;

import model.StadiumLoader;
import model.Team;
import model.Tournament;
import view.zonestage.ZoneStageView;

import javax.swing.*;

import static model.FileReader.fileReader;
import static model.StadiumLoader.initDatabase;

public class ZoneStageTestLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Tournament tournament = buildDummyTournament(); // datos de prueba

            JFrame frame = new JFrame("Test - Zone Stage");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 600);
            frame.add(new ZoneStageView(tournament));
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static Tournament buildDummyTournament() {
        Tournament tournament = new Tournament();
        // acá agregás 16 equipos de prueba con distinto ranking, para que getPots() tenga con qué trabajar
        // ajustá esto al constructor real de Team que tengas
        try {
            tournament = fileReader("torneo.json");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        StadiumLoader loader = new StadiumLoader();
        initDatabase();
        tournament.addAllStadiums(loader.loadStadiums());

        return tournament;
    }
}