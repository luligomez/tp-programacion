package controller;

import DAO.StadiumDAO;
import model.FileReader;
import model.Tournament;
import model.TournamentState;
import view.PlayersView;
import view.RefereesView;
import view.TeamsView;
import view.knockout.KnockoutView;
import view.mainwindow.MainWindowView;
import view.mainwindow.SidebarItem;
import view.zonestage.ZoneStageView;

import javax.swing.*;

public class MainWindowController {

    private final MainWindowView view;
    private Tournament tournament;

    public MainWindowController(MainWindowView view, Tournament tournament) {
        this.view = view;
        this.tournament = tournament;

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
            case RANKINGS, CREDENTIALS, ADMIN -> {
                JOptionPane.showMessageDialog(view, "Screen in development: " + item);
            }
        }
    }

    private void navigateToGroups() {
        // PASO 1: Crear la vista hija
        ZoneStageView zoneStageView = new ZoneStageView();

        // PASO 2: Crear el controlador hijo (él solito se vincula con la vista y el torneo)
        new ZoneStageController(zoneStageView, tournament);

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
        if (!tournament.getState().equals(TournamentState.KNOCKOUT_STAGE)) {
            JOptionPane.showMessageDialog(
                    view,
                    "You must complete the group stage before accessing the knockout stage.",
                    "Group stage not finished",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        KnockoutView knockoutView = new KnockoutView();
        new KnockoutController(knockoutView, tournament);
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
}