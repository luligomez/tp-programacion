package controller;

import controller.ZoneStageController;
import model.Tournament;
import view.mainwindow.MainWindowView;
import view.mainwindow.SidebarItem;
import view.zonestage.ZoneStageView;

import javax.swing.*;

public class MainWindowController {

    private final MainWindowView view;
    private Tournament tournament;

    // Guardamos la referencia al controlador activo de la pantalla actual (opcional)
    private ZoneStageController currentZoneStageController;

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
            case KNOCKOUT -> {
                // Cuando tengas KnockoutView y KnockoutController:
                // KnockoutView knockoutView = new KnockoutView();
                // new KnockoutController(knockoutView, tournament);
                // view.showScreen(knockoutView, item);
                JOptionPane.showMessageDialog(view, "Knockout Stage in development");
            }
            case TEAMS, REFEREES, PLAYERS, RANKINGS, CREDENTIALS, ADMIN -> {
                JOptionPane.showMessageDialog(view, "Screen in development: " + item);
            }
        }
    }

    private void navigateToGroups() {
        // PASO 1: Crear la vista hija
        ZoneStageView zoneStageView = new ZoneStageView();

        // PASO 2: Crear el controlador hijo (él solito se vincula con la vista y el torneo)
        this.currentZoneStageController = new ZoneStageController(zoneStageView, tournament);

        // PASO 3: Indicarle a la ventana principal que muestre el panel de la vista hija
        view.showScreen(zoneStageView, SidebarItem.GROUPS);
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
            // Acá reseteás el modelo de torneo o lo recargás desde JSON
            // tournament.reset();

            // Volvemos a cargar la pantalla de grupos limpia
            navigateToGroups();
        }
    }
}