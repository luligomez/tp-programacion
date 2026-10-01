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
    private boolean firstLegPlayed=false;
    private boolean secondLegPlayed = false;

    public KnockoutController(KnockoutView view, Tournament tournament) {
        this.view = view;
        this.tournament = tournament;

        initController();
    }

    private void initController() {
        view.setOnSimulateListener(this::simulateNextRound);

        // Los cuartos se generan solo la primera vez
        if (tournament.getKnockoutTies().isEmpty()) {
            tournament.generateQuarterFinals();
        }

        view.setPhaseTitle("Quarter Finals");
        view.setSimulateButtonText("Simulate Quarter Finals: First Leg ⚽");
        view.showTies(tournament.getKnockoutTies(), false);
    }

    private void assignSecondLegFormations() {
        for (KnockoutTie tie : tournament.getKnockoutTies()) {
            if (tie.getPhase() == KnockoutPhase.QUARTER_FINAL) {
                SecondLegMatch secondLeg = tie.getSecondLeg();
                secondLeg.setTeam1Formation(FormationCreator.createAutomaticFormation(secondLeg.getTeam1()));
                secondLeg.setTeam2Formation(FormationCreator.createAutomaticFormation(secondLeg.getTeam2()));
            }
        }
    }

    private void simulateNextRound() {
        if (!firstLegPlayed) {
            MatchSimulator.simulateFirstLeg(tournament, KnockoutPhase.QUARTER_FINAL);
            firstLegPlayed = true;
            view.showTies(tournament.getKnockoutTies(), true);
            view.setSimulateButtonText("Simulate Quarter Finals: Second Leg ⚽");
        } else if (!secondLegPlayed) {
            // Paso 2: simula la vuelta (((los penales los resuelve el simulador solo))
            assignSecondLegFormations(); //rearmar la formacion antes de la vuelta
            MatchSimulator.simulateSecondLeg(tournament, KnockoutPhase.QUARTER_FINAL);
            secondLegPlayed = true;
            view.showTies(tournament.getKnockoutTies(), true);
            view.setSimulateButtonText("Quarter Finals completed 🏁");
            view.disableSimulateButton();
        }
    }
}