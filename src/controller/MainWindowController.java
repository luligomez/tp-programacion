package controller;

import DAO.CityDAO;
import DAO.StadiumDAO;
import service.FileReader;
import model.tournament.Tournament;
import model.tournament.TournamentState;
import model.match.Match;
import service.TournamentRepository;
import service.TournamentReportsService;
import view.*;
import view.knockout.KnockoutView;
import view.mainwindow.MainWindowView;
import view.mainwindow.SidebarItem;
import view.match.MatchDetailView;
import view.zonestage.ZoneStageView;

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
        // PASO 1: Crear la vista hija
        ZoneStageView zoneStageView = new ZoneStageView();

        // PASO 2: Crear el controlador hijo (él solito se vincula con la vista y el torneo)
        new ZoneStageController(zoneStageView, tournament,
                match -> showMatchDetail(match, this::navigateToGroups, SidebarItem.GROUPS),
                this::saveTournament);
        // PASO 3: Indicarle a la ventana principal que muestre el panel de la vista hija
        view.showScreen(zoneStageView, SidebarItem.GROUPS);
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
        KnockoutView knockoutView = new KnockoutView();
        new KnockoutController(knockoutView, tournament,
                match -> showMatchDetail(match, this::navigateToKnockout, SidebarItem.KNOCKOUT),
                this::saveTournament);
        view.showScreen(knockoutView, SidebarItem.KNOCKOUT);
    }

    private void navigateToTeams() {
        TeamsView teamsView = new TeamsView();
        new TeamsController(teamsView, tournament);
        view.showScreen(teamsView, SidebarItem.TEAMS);
    }

    private void navigateToPlayers() {
        PlayersView playersView = new PlayersView();
        new PlayersController(playersView, tournament);
        view.showScreen(playersView, SidebarItem.PLAYERS);
    }

    private void navigateToReferees() {
        RefereesView refereesView = new RefereesView();
        new RefereesController(refereesView, tournament);
        view.showScreen(refereesView, SidebarItem.REFEREES);
    }

    private void navigateToRankings() {

        // Create scorer ranking view
        ScorerRankingView scorerRankingView = new ScorerRankingView(
                TournamentReportsService.generateScorersRanking(tournament)
        );

        // Create Fair Play ranking view
        FairPlayRankingView fairPlayRankingView = new FairPlayRankingView(
                TournamentReportsService.generateFairPlayRanking(tournament)
        );

        // Create tabs for both rankings
        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Top Scorers", scorerRankingView);
        tabs.addTab("Fair Play", fairPlayRankingView);

        // Apply the existing tab style
        styleTabs(tabs);

        // Display rankings in the main window
        JPanel rankingsPanel = new JPanel(new BorderLayout());
        rankingsPanel.add(tabs, BorderLayout.CENTER);

        view.showScreen(rankingsPanel, SidebarItem.RANKINGS);
    }

    private void navigateToAdmin() {
        CitiesView citiesView = new CitiesView();
        new CitiesController(citiesView, new CityDAO());
        StadiumsView stadiumsView = new StadiumsView();
        StadiumsController stadiumsController =
                new StadiumsController(stadiumsView, new StadiumDAO(), new CityDAO());
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Cities", citiesView);
        tabs.addTab("Stadiums", stadiumsView);
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
        MatchDetailView detailView = new MatchDetailView();
        new MatchDetailController(detailView, match, onBack);
        view.showScreen(detailView, item);
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