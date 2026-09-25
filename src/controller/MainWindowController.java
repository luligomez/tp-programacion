package controller;

import model.Tournament;
import model.FileReader; // Tu cargador de JSON o reset del modelo
import view.mainwindow.MainWindowView;
import view.mainwindow.SidebarItem;
import view.zonestage.ZoneStageView;


import javax.swing.*;
import java.io.File;

public class MainWindowController {

    private MainWindowView view;
    private Tournament tournament;

    public MainWindowController(MainWindowView view, Tournament tournament) {
        this.view = view;
        this.tournament = tournament;

        initController();
    }

    private void initController() {
        // Suscribimos las acciones de la vista a los métodos de este controlador
        view.setOnSidebarSelectListener(this::onSidebarItemSelected);
        view.setOnResetListener(this::onResetTournament);

        // Cargar la pantalla inicial según el estado del torneo
        showInitialScreen();
    }

    private void showInitialScreen() {
        // Evaluamos si el torneo ya sorteó las zonas o si debe arrancar en la pantalla de sorteo
        JPanel initialScreen;
        if (tournament.getZones() == null || tournament.getZones().isEmpty()) {
            initialScreen = new ZoneStageView(tournament);
        } else {
            initialScreen = new ZoneStageView(tournament);
        }

        view.showScreen(initialScreen, SidebarItem.GROUPS);
    }

    private void onSidebarItemSelected(SidebarItem item) {
        // El controlador decide qué panel instanciar según la opción del menú
        JPanel screen = switch (item) {
            case GROUPS -> {
                if (tournament.getZones() == null || tournament.getZones().isEmpty()) {
                    yield new ZoneStageView(tournament);
                } else {
                    yield new ZoneStageView(tournament);
                }
            }
            case KNOCKOUT -> {
                if (tournament.getZones() == null || tournament.getZones().isEmpty()) {
                    JOptionPane.showMessageDialog(view, "Please perform the group draw first!",
                            "Draw Pending", JOptionPane.WARNING_MESSAGE);
                    yield null;
                }
                yield new ZoneStageView(tournament); // new KnockoutStageView(tournament); // Ajustar cuando esté creada
            }
            case TEAMS -> new ZoneStageView(tournament); //new TeamsListView(tournament); //TODO crear views
            case REFEREES -> new ZoneStageView(tournament); //new RefereesListView(tournament);
            case PLAYERS -> new ZoneStageView(tournament); //new PlayersListView(tournament);
            case RANKINGS -> new ZoneStageView(tournament); //new RankingsView(tournament);
            case CREDENTIALS -> new ZoneStageView(tournament); //new CredentialsView(tournament);
            case ADMIN -> new ZoneStageView(tournament); //new AdminView(tournament);
        };

        if (screen != null) {
            view.showScreen(screen, item);
        }
    }

    private void onResetTournament() {
        int confirm = JOptionPane.showConfirmDialog(
                view,
                "¿Are you sure? The current tournament will be lost.",
                "Reset tournament",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // 1. Recargamos el torneo desde el JSON en el modelo
                this.tournament = FileReader.fileReader("torneo.json");

                // 2. Volvemos a la pantalla inicial
                showInitialScreen();

                JOptionPane.showMessageDialog(view, "Tournament successfully reset!",
                        "Reset", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Error resetting tournament: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
