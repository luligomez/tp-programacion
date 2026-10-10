package controller;

import model.Tournament;
import model.match.Match;
import model.match.knockout.*;
import view.knockout.KnockoutView;
import java.util.List;
import java.util.function.Consumer;

public class KnockoutController {

    private final KnockoutView view;
    private final Tournament tournament;
    private final Consumer<Match> onOpenMatch;

    public KnockoutController(KnockoutView view, Tournament tournament, Consumer<Match> onOpenMatch) {
        this.view = view;
        this.tournament = tournament;
        this.onOpenMatch = onOpenMatch;

        view.setOnSimulateListener(this::simulateNextRound);
        view.setOnMatchSelectedListener(onOpenMatch); // nuevo
        updateView();
    }

    // Le pregunta al modelo qué falta jugar
    private KnockoutStep currentStep() {
        if (!tournament.isFirstLegPlayed(KnockoutPhase.QUARTER_FINAL)) return KnockoutStep.QUARTERS_FIRST_LEG;
        if (!tournament.isSecondLegPlayed(KnockoutPhase.QUARTER_FINAL)) return KnockoutStep.QUARTERS_SECOND_LEG;
        if (tournament.hasUnresolvedTies(KnockoutPhase.QUARTER_FINAL)) return KnockoutStep.QUARTERS_PENALTIES;
        if (!tournament.isFirstLegPlayed(KnockoutPhase.SEMI_FINAL)) return KnockoutStep.SEMIS_FIRST_LEG;
        if (!tournament.isSecondLegPlayed(KnockoutPhase.SEMI_FINAL)) return KnockoutStep.SEMIS_SECOND_LEG;
        if (tournament.hasUnresolvedTies(KnockoutPhase.SEMI_FINAL)) return KnockoutStep.SEMIS_PENALTIES;
        if (!tournament.isFinalPlayed()) return KnockoutStep.FINAL;
        if (tournament.hasUnresolvedFinal()) return KnockoutStep.FINAL_PENALTIES;
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
                if(!tournament.hasUnresolvedTies(KnockoutPhase.QUARTER_FINAL))
                    tournament.generateNextKnockoutPhase(KnockoutPhase.QUARTER_FINAL);
            }
            case QUARTERS_PENALTIES -> {
                tournament.resolveTies(KnockoutPhase.QUARTER_FINAL);
                tournament.generateNextKnockoutPhase(KnockoutPhase.QUARTER_FINAL);
            }
            case SEMIS_FIRST_LEG -> tournament.simulateKnockoutFirstLeg(KnockoutPhase.SEMI_FINAL);
            case SEMIS_SECOND_LEG -> {
                tournament.simulateKnockoutSecondLeg(KnockoutPhase.SEMI_FINAL);
                if(!tournament.hasUnresolvedTies(KnockoutPhase.SEMI_FINAL))
                    tournament.generateNextKnockoutPhase(KnockoutPhase.SEMI_FINAL);
            }
            case SEMIS_PENALTIES -> {
                tournament.resolveTies(KnockoutPhase.SEMI_FINAL);
                tournament.generateNextKnockoutPhase(KnockoutPhase.SEMI_FINAL);
            }
            case FINAL -> tournament.simulateFinal();
            case FINAL_PENALTIES -> tournament.resolveFinal();
            case DONE -> { }
        }
        updateView();
    }
}