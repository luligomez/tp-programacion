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
    private KnockoutPhase viewedPhase = KnockoutPhase.QUARTER_FINAL;
    private int viewedStep = 0;

    public KnockoutController(KnockoutView view, Tournament tournament) {
        this.view = view;
        this.tournament = tournament;
        initController();
    }

    private void initController() {
        view.setOnSimulateListener(this::simulateNextRound);
        view.setOnStepSelectedListener(this::onStepSelected);
        updateView();
    }

    private KnockoutPhase phaseOf(int step) {
        return (step == 0) ? KnockoutPhase.QUARTER_FINAL : KnockoutPhase.SEMI_FINAL;
    }

    private void onStepSelected(int index) {
        viewedStep = index;
        updateView();
    }

    private void updateView() {
        int lastUnlocked = 0;
        if (tournament.hasKnockoutPhase(KnockoutPhase.SEMI_FINAL)) lastUnlocked = 1;
        if (tournament.hasFinal()) lastUnlocked = 2;
        view.updateStepBar(viewedStep, lastUnlocked);

        if (viewedStep == 2) {
            view.setPhaseTitle("Final");
            boolean played = tournament.isFinalPlayed();
            view.showFinal(tournament.getFinalMatch(), played);

            if (!played) {
                view.enableSimulateButton();
                view.setSimulateButtonText("Simulate Final ⚽");
            } else {
                view.setSimulateButtonText("Final completed 🏁");
                view.disableSimulateButton();
            }
            return;
        }

        KnockoutPhase phase = phaseOf(viewedStep);
        String phaseName = (viewedStep == 0) ? "Quarter Finals" : "Semi Finals";
        view.setPhaseTitle(phaseName);

        boolean firstLegPlayed = tournament.isFirstLegPlayed(phase);
        boolean secondLegPlayed = tournament.isSecondLegPlayed(phase);

        view.showTies(tournament.getKnockoutTies(phase), firstLegPlayed);

        if (!firstLegPlayed) {
            view.enableSimulateButton();
            view.setSimulateButtonText("Simulate " + phaseName + ": First Leg ⚽");
        } else if (!secondLegPlayed) {
            view.enableSimulateButton();
            view.setSimulateButtonText("Simulate " + phaseName + ": Second Leg ⚽");
        } else {
            view.setSimulateButtonText(phaseName + " completed 🏁");
            view.disableSimulateButton();
        }
    }

    private void simulateNextRound() {
        if (viewedStep == 2) {
            if (!tournament.isFinalPlayed()) {
                tournament.simulateFinal();
            }
            updateView();
            return;
        }

        KnockoutPhase phase = phaseOf(viewedStep);
        boolean firstLegPlayed = tournament.isFirstLegPlayed(phase);
        boolean secondLegPlayed = tournament.isSecondLegPlayed(phase);

        if (!firstLegPlayed) {
            tournament.simulateKnockoutFirstLeg(phase);
        } else if (!secondLegPlayed) {
            tournament.simulateKnockoutSecondLeg(phase);
            tournament.generateNextKnockoutPhase(phase);
        }
        updateView();
    }
}