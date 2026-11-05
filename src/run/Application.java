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
                // 1. Establecer el diseño de la interfaz
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                // 2. 🗄️ INICIALIZAR BASE DE DATOS (Crear tablas e insertar sample data si está vacía)
                initializeDatabase();

                // 3. Cargar el Torneo (Repositorio o JSON)
                TournamentRepository repository = new TournamentRepository();
                Tournament tournament = loadOrFetchTournament(repository);

                // 4. Asignar estadios desde la BD
                tournament.setStadiums(new StadiumDAO().getAllStadiums());
                tournament.syncUsedStadiums();

                // 5. Iniciar Vista y Controlador
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
                // Si el archivo guardado falla o es de una versión vieja de la clase, cae al JSON
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

        // 1. Crear tablas si no existen
        cityDAO.createTable();
        stadiumDAO.createTable();

        // 2. Poblar solo si están vacías
        if (cityDAO.isEmpty()) {
            cityDAO.insertSampleData();
        }
        if (stadiumDAO.isEmpty()) {
            stadiumDAO.insertSampleData();
        }
    }
}