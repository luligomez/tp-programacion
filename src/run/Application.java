package run;

import DAO.CityDAO;
import DAO.StadiumDAO;
import controller.MainWindowController;
import model.tournament.Tournament;
import service.FileReader;
import service.TournamentRepository;
import view.mainwindow.MainWindowView;
import org.json.simple.parser.ParseException;
import javax.swing.*;
import java.io.IOException;
import java.sql.SQLException;

public class Application {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // 1. Set the UI look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                // 2. INITIALIZE DATABASE (create tables and insert sample data if empty)
                initializeDatabase();

                // 3. Load the tournament (repository or JSON)
                TournamentRepository repository = new TournamentRepository();
                Tournament tournament = loadOrFetchTournament(repository);

                // 4. Assign stadiums from the DB
                tournament.setStadiums(new StadiumDAO().getAllStadiums());
                tournament.syncUsedStadiums();

                // 5. Start the view and controller
                MainWindowView windowView = new MainWindowView();
                MainWindowController mainController = new MainWindowController(windowView, tournament, repository);

                windowView.setExtendedState(JFrame.MAXIMIZED_BOTH);
                windowView.setVisible(true);

            } catch (SQLException | IllegalStateException | ReflectiveOperationException | UnsupportedLookAndFeelException e){
                JOptionPane.showMessageDialog(null,
                        "Critical error while opening app: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static Tournament loadOrFetchTournament(TournamentRepository repository) {
        if (repository.exists()) {
            try {
                return repository.load();
            } catch (ClassNotFoundException | IOException e) {
                // If the saved file fails or is from an old version of the class, fall back to the JSON
                return loadTournamentFromJson();
            }
        }
        return loadTournamentFromJson();
    }
    private static Tournament loadTournamentFromJson() {
        try {
            return FileReader.fileReader("torneo.json");
        } catch (IOException | ParseException e) {
            throw new IllegalStateException("Could not load torneo.json: " + e.getMessage(), e);
        }
    }

    private static void initializeDatabase() throws SQLException {
        CityDAO cityDAO = new CityDAO();
        StadiumDAO stadiumDAO = new StadiumDAO();

        // 1. Create tables if dont exist
        cityDAO.createTable();
        stadiumDAO.createTable();

        // 2. Populate only if empty
        if (cityDAO.isEmpty()) {
            cityDAO.insertSampleData();
        }
        if (stadiumDAO.isEmpty()) {
            stadiumDAO.insertSampleData();
        }
    }
}