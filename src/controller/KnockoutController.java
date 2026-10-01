package controller;

import model.FormationCreator;
import model.MatchSimulator;
import model.Tournament;
import model.match.knockout.KnockoutPhase;
import model.match.knockout.KnockoutTie;
import model.match.knockout.SecondLegMatch;
import view.knockout.KnockoutView;

import javax.swing.*;

public class KnockoutController {

    private final KnockoutView view;
    private final Tournament tournament;

    public KnockoutController(KnockoutView view, Tournament tournament) {
        this.view = view;
        this.tournament = tournament;

        initController();
    }

    private void initController() {
        view.setOnSimulateListener(this::simulateNextRound);

        view.setPhaseTitle("Quarter Finals");

        updateView();
    }

    private void updateView() {
        // Consultar el estado real de los partidos en el modelo
        boolean firstLegPlayed = tournament.isFirstLegPlayed(KnockoutPhase.QUARTER_FINAL);
        boolean secondLegPlayed = tournament.isSecondLegPlayed(KnockoutPhase.QUARTER_FINAL);

        if (!firstLegPlayed) {
            // Estado 1: No se jugó la ida
            view.setSimulateButtonText("Simulate Quarter Finals: First Leg ⚽");
            view.showTies(tournament.getKnockoutTies(), false);
        } else if (!secondLegPlayed) {
            // Estado 2: Ya se jugó la ida, falta la vuelta (se muestran los resultados de la ida)
            view.setSimulateButtonText("Simulate Quarter Finals: Second Leg ⚽");
            view.showTies(tournament.getKnockoutTies(), true);
        } else {
            // Estado 3: Serie de cuartos completada
            view.setSimulateButtonText("Quarter Finals completed 🏁");
            view.disableSimulateButton();
            view.showTies(tournament.getKnockoutTies(), true);
        }
    }

    private void simulateNextRound() {
        boolean firstLegPlayed = tournament.isFirstLegPlayed(KnockoutPhase.QUARTER_FINAL);
        boolean secondLegPlayed = tournament.isSecondLegPlayed(KnockoutPhase.QUARTER_FINAL);
        if (!firstLegPlayed) {
            tournament.simulateKnockoutFirstLeg(KnockoutPhase.QUARTER_FINAL);
        } else if (!secondLegPlayed) {
            // Paso 2: simula la vuelta (((los penales los resuelve el simulador solo))
            tournament.simulateKnockoutSecondLeg(KnockoutPhase.QUARTER_FINAL);
        }
        updateView();
    }
}