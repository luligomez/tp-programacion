package controller;

import DAO.CityDAO;
import DAO.StadiumDAO;
import service.FileReader;
import model.tournament.Tournament;
import model.tournament.TournamentState;
import model.match.Match;
import service.TournamentRepository;
import view.mainwindow.MainWindowView;
import view.mainwindow.SidebarItem;

import javax.swing.*;

import java.awt.*;
import java.io.IOException;

import static model.tournament.TournamentState.*;

public class MainWindowController {

    private final MainWindowView view;
    private Tournament tournament;
    private final TournamentRepository repository;

    public MainWindowController(MainWindowView view, Tournament tournament, TournamentRepository repository) {
        this.view = view;
        this.tournament = tournament;
        this.repository = repository;
        initController();
    }

    private void initController() {
        // 1. Escuchamos los eventos que emite la ventana principal
        view.setOnSidebarSelectListener(this::onSidebarItemSelected);
        view.setOnResetListener(this::onResetTournament);

        // 2. Cargamos la pantalla inicial (Fase de Grupos por defecto)
        navigateToGroups();
    }

    private void onSidebarItemSelected(SidebarItem item) {
        switch (item) {
            case GROUPS -> navigateToGroups();
            case KNOCKOUT -> navigateToKnockout();
            case TEAMS -> navigateToTeams();
            case PLAYERS -> navigateToPlayers();
            case REFEREES -> navigateToReferees();
            case ADMIN -> navigateToAdmin();
            case RANKINGS -> navigateToRankings();
            case CREDENTIALS -> {
                JOptionPane.showMessageDialog(view, "Screen in development: " + item);
            }
        }
    }

    private void navigateToGroups() {
        // PASO 1: Crear el controlador hijo (él solito se vincula con la vista y el torneo)
        ZoneStageController zoneStageController = new ZoneStageController(tournament,
                match -> showMatchDetail(match, this::navigateToGroups, SidebarItem.GROUPS),
                this::saveTournament);
        // PASO 2: Indicarle a la ventana principal que muestre el panel de la vista hija
        view.showScreen(zoneStageController.getView(), SidebarItem.GROUPS);
    }

    private void onResetTournament() {
        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Are you sure? The current tournament will be lost.",
                "Reset tournament",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // 1. Recargamos el modelo desde el JSON
                this.tournament = FileReader.fileReader("torneo.json");
                repository.delete();
                this.tournament.setStadiums(new StadiumDAO().getAllStadiums());
                // 2. Volvemos a cargar la pantalla de grupos limpia con el nuevo torneo
                navigateToGroups();

                JOptionPane.showMessageDialog(
                        view,
                        "Tournament successfully reset!",
                        "Reset Complete",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        view,
                        "Error loading tournament file: " + e.getMessage(),
                        "Error Resetting Tournament",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void navigateToKnockout(){
        TournamentState state = tournament.getState();
        if (state == NOT_DRAWN || state == DRAWN_UNCONFIRMED || state == GROUP_STAGE) {
            JOptionPane.showMessageDialog(
                    view,
                    "You must complete the group stage before accessing the knockout stage.",
                    "Group stage not finished",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        KnockoutController knockoutController = new KnockoutController(tournament,
                match -> showMatchDetail(match, this::navigateToKnockout, SidebarItem.KNOCKOUT),
                this::saveTournament);
        view.showScreen(knockoutController.getView(), SidebarItem.KNOCKOUT);
    }

    private void navigateToTeams() {
        TeamsController teamsController = new TeamsController(tournament);
        view.showScreen(teamsController.getView(), SidebarItem.TEAMS);
    }

    private void navigateToPlayers() {
        PlayersController playersController = new PlayersController(tournament);
        view.showScreen(playersController.getView(), SidebarItem.PLAYERS);
    }

    private void navigateToReferees() {
        RefereesController refereesController = new RefereesController(tournament);
        view.showScreen(refereesController.getView(), SidebarItem.REFEREES);
    }

    private void navigateToRankings() {
        ScorerRankingController scorerRankingController = new ScorerRankingController(tournament);
        FairPlayRankingController fairPlayRankingController = new FairPlayRankingController(tournament);

        // Create tabs for both rankings
        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Top Scorers", scorerRankingController.getView());
        tabs.addTab("Fair Play", fairPlayRankingController.getView());

        // Apply the existing tab style
        styleTabs(tabs);

        // Display rankings in the main window
        JPanel rankingsPanel = new JPanel(new BorderLayout());
        rankingsPanel.add(tabs, BorderLayout.CENTER);

        view.showScreen(rankingsPanel, SidebarItem.RANKINGS);
    }

    private void navigateToAdmin() {
        CitiesController citiesController = new CitiesController(new CityDAO());
        StadiumsController stadiumsController = new StadiumsController(new StadiumDAO(), new CityDAO());
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Cities", citiesController.getView());
        tabs.addTab("Stadiums", stadiumsController.getView());
        styleTabs(tabs);
        tabs.addChangeListener(e -> stadiumsController.loadCities());
        JPanel adminPanel = new JPanel(new BorderLayout());
        adminPanel.add(tabs, BorderLayout.CENTER);
        view.showScreen(adminPanel, SidebarItem.ADMIN);
    }

    private void styleTabs(JTabbedPane tabs) {
        tabs.setFont(new Font("SansSerif", Font.BOLD, 16));
        for (int i = 0; i < tabs.getTabCount(); i++) {
            JLabel label = new JLabel(tabs.getTitleAt(i));
            label.setFont(new Font("SansSerif", Font.BOLD, 16));
            label.setBorder(BorderFactory.createEmptyBorder(8, 30, 8, 30));
            tabs.setTabComponentAt(i, label);
        }
    }

    private void showMatchDetail(Match match, Runnable onBack, SidebarItem item) {
        MatchDetailController matchDetailController = new MatchDetailController(match, onBack);
        view.showScreen(matchDetailController.getView(), item);
    }

    private void saveTournament() {
        try {
            repository.save(tournament);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(view, "Could not save progress: " + e.getMessage(),
                    "Save error", JOptionPane.WARNING_MESSAGE);
        }
    }


}