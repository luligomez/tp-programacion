package run;

import DAO.CityDAO;
import DAO.StadiumDAO;
import controller.MainWindowController;
import model.tournament.Tournament;
import service.FileReader;
import view.mainwindow.MainWindowView;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class Application {
    public static void main( String[] args )
    {
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            SwingUtilities.invokeLater(() -> {
                // 1\. Cargar el torneo inicial
                Tournament tournament = null;
                try {
                    tournament = FileReader.fileReader("torneo.json");
                    loadStadiums(tournament);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                MainWindowView windowView = new MainWindowView();
                MainWindowController mainController = new MainWindowController(windowView, tournament);
                windowView.setExtendedState(JFrame.MAXIMIZED_BOTH);
                windowView.setVisible(true);
            });
        });
    }

    private static void loadStadiums(Tournament tournament) throws SQLException {
        CityDAO cityDAO = new CityDAO();
        StadiumDAO stadiumDAO = new StadiumDAO();

        // Cities first: stadium has a foreign key to city
        cityDAO.createTable();
        stadiumDAO.createTable();
        cityDAO.insertSampleData();
        stadiumDAO.insertSampleData();

        tournament.setStadiums(stadiumDAO.getAllStadiums());
    }

}
