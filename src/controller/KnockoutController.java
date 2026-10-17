package controller;

import model.tournament.Tournament;
import model.match.Match;
import model.tournament.knockout.KnockoutPhase;
import model.tournament.knockout.KnockoutStage;
import view.knockout.KnockoutView;

import java.util.function.Consumer;

public class KnockoutController {

    private final KnockoutView view;
    private final Tournament tournament;
    private final Consumer<Match> onOpenMatch;
    private final KnockoutStage knockout;
    private final Runnable onProgress;


    public KnockoutController(Tournament tournament, Consumer<Match> onOpenMatch, Runnable onProgress) {
        this.view = new KnockoutView();
        this.tournament = tournament;
        this.onOpenMatch = onOpenMatch;
        this.knockout = tournament.getKnockoutStage();
        this.onProgress = onProgress;
        view.setOnSimulateListener(this::simulateNextRound);
        view.setOnMatchSelectedListener(onOpenMatch);
        updateView();
    }

    public KnockoutView getView() {
        return view;
    }

    private void updateView() {
        view.showBracket(knockout.getTies(KnockoutPhase.QUARTER_FINAL),
                knockout.getTies(KnockoutPhase.SEMI_FINAL),
                knockout.getFinalMatch());
        view.setStep(knockout.currentStep());
    }

    private void simulateNextRound() {
        KnockoutPhase qf = KnockoutPhase.QUARTER_FINAL, sf = KnockoutPhase.SEMI_FINAL;
        switch (knockout.currentStep()) {
            case QUARTERS_FIRST_LEG -> knockout.simulateFirstLegs(qf);
            case QUARTERS_SECOND_LEG -> {
                knockout.simulateSecondLegs(qf);
                tournament.advanceKnockoutIfDecided(qf);
            }
            case QUARTERS_PENALTIES -> {
                knockout.resolveTies(qf);
                tournament.advanceKnockoutIfDecided(qf);
            }
            case SEMIS_FIRST_LEG -> knockout.simulateFirstLegs(sf);
            case SEMIS_SECOND_LEG -> {
                knockout.simulateSecondLegs(sf);
                tournament.advanceKnockoutIfDecided(sf);
            }
            case SEMIS_PENALTIES -> {
                knockout.resolveTies(sf);
                tournament.advanceKnockoutIfDecided(sf);
            }
            case FINAL -> knockout.simulateFinal();
            case FINAL_PENALTIES -> knockout.resolveFinal();
            case DONE -> { }
        }
        onProgress.run();
        updateView();
    }
}