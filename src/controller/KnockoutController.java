package controller;

import model.Tournament;
import model.match.knockout.*;
import view.knockout.KnockoutView;
import java.util.List;

public class KnockoutController {

    private final KnockoutView view;
    private final Tournament tournament;

    public KnockoutController(KnockoutView view, Tournament tournament) {
        this.view = view;
        this.tournament = tournament;

        view.setOnSimulateListener(this::simulateNextRound);
        updateView();
    }

    // Le pregunta al modelo qué falta jugar
    private KnockoutStep currentStep() {
        if (!tournament.isFirstLegPlayed(KnockoutPhase.QUARTER_FINAL)) return KnockoutStep.QUARTERS_FIRST_LEG;
        if (!tournament.isSecondLegPlayed(KnockoutPhase.QUARTER_FINAL)) return KnockoutStep.QUARTERS_SECOND_LEG;
        if (!tournament.isFirstLegPlayed(KnockoutPhase.SEMI_FINAL)) return KnockoutStep.SEMIS_FIRST_LEG;
        if (!tournament.isSecondLegPlayed(KnockoutPhase.SEMI_FINAL)) return KnockoutStep.SEMIS_SECOND_LEG;
        if (!tournament.isFinalPlayed()) return KnockoutStep.FINAL;
        return KnockoutStep.DONE;
    }

    private void updateView() {
        List<KnockoutTie> quarters = tournament.getKnockoutTies(KnockoutPhase.QUARTER_FINAL);
        List<KnockoutTie> semis = tournament.getKnockoutTies(KnockoutPhase.SEMI_FINAL);
        FinalMatch finalMatch = tournament.hasFinal() ? tournament.getFinalMatch() : null;

        view.showBracket(quarters, semis, finalMatch);
        view.setStep(currentStep());
    }

    private void simulateNextRound() {
        switch (currentStep()) {
            case QUARTERS_FIRST_LEG -> tournament.simulateKnockoutFirstLeg(KnockoutPhase.QUARTER_FINAL);
            case QUARTERS_SECOND_LEG -> {
                tournament.simulateKnockoutSecondLeg(KnockoutPhase.QUARTER_FINAL);
                tournament.generateNextKnockoutPhase(KnockoutPhase.QUARTER_FINAL);
            }
            case SEMIS_FIRST_LEG -> tournament.simulateKnockoutFirstLeg(KnockoutPhase.SEMI_FINAL);
            case SEMIS_SECOND_LEG -> {
                tournament.simulateKnockoutSecondLeg(KnockoutPhase.SEMI_FINAL);
                tournament.generateNextKnockoutPhase(KnockoutPhase.SEMI_FINAL);
            }
            case FINAL -> tournament.simulateFinal();
            case DONE -> { }
        }
        updateView();
    }
}