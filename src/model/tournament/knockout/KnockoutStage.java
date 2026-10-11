package model.tournament.knockout;

import model.team.Team;
import model.match.Match;
import model.match.knockout.FinalMatch;
import model.match.knockout.KnockoutTie;
import model.match.knockout.SecondLegMatch;
import model.person.Referee;
import model.place.Stadium;
import model.tournament.zone.Zone;
import service.FixtureScheduler;
import service.FormationCreator;
import service.KnockoutService;
import service.MatchSimulator;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class KnockoutStage implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final ArrayList<KnockoutTie> ties = new ArrayList<>();
    private FinalMatch finalMatch;

    // ---------- consultas ----------

    public List<KnockoutTie> getTies(KnockoutPhase phase) {
        return ties.stream().filter(t -> t.getPhase() == phase).collect(Collectors.toList());
    }

    public FinalMatch getFinalMatch() { return finalMatch; }   // null si todavía no existe

    public boolean hasFinal() { return finalMatch != null; }

    public boolean isFinalPlayed() { return hasFinal() && finalMatch.isPlayed(); }

    public boolean hasUnresolvedFinal() { return hasFinal() && finalMatch.getWinner() == null; }

    // "!isEmpty()": allMatch devuelve true en una lista vacía, y una fase que no existe no está jugada
    public boolean isFirstLegPlayed(KnockoutPhase phase) {
        List<KnockoutTie> phaseTies = getTies(phase);
        return !phaseTies.isEmpty() && phaseTies.stream().allMatch(t -> t.getFirstLeg().isPlayed());
    }

    public boolean isSecondLegPlayed(KnockoutPhase phase) {
        List<KnockoutTie> phaseTies = getTies(phase);
        return !phaseTies.isEmpty() && phaseTies.stream().allMatch(t -> t.getSecondLeg().isPlayed());
    }

    public boolean hasUnresolvedTies(KnockoutPhase phase) {
        return getTies(phase).stream().anyMatch(t -> t.getWinner() == null);
    }

    public ArrayList<Team> getWinners(KnockoutPhase phase) {
        return getTies(phase).stream()
                .map(KnockoutTie::getWinner)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Match> getMatches() {
        List<Match> matches = new ArrayList<>();
        for (KnockoutTie tie : ties) {
            matches.add(tie.getFirstLeg());
            matches.add(tie.getSecondLeg());
        }
        if (finalMatch != null) matches.add(finalMatch);
        return matches;
    }

    // Qué falta jugar
    public KnockoutStep currentStep() {
        KnockoutPhase qf = KnockoutPhase.QUARTER_FINAL, sf = KnockoutPhase.SEMI_FINAL;
        if (!isFirstLegPlayed(qf)) return KnockoutStep.QUARTERS_FIRST_LEG;
        if (!isSecondLegPlayed(qf)) return KnockoutStep.QUARTERS_SECOND_LEG;
        if (hasUnresolvedTies(qf)) return KnockoutStep.QUARTERS_PENALTIES;
        if (!isFirstLegPlayed(sf)) return KnockoutStep.SEMIS_FIRST_LEG;
        if (!isSecondLegPlayed(sf)) return KnockoutStep.SEMIS_SECOND_LEG;
        if (hasUnresolvedTies(sf)) return KnockoutStep.SEMIS_PENALTIES;
        if (!isFinalPlayed()) return KnockoutStep.FINAL;
        if (hasUnresolvedFinal()) return KnockoutStep.FINAL_PENALTIES;
        return KnockoutStep.DONE;
    }

    // ---------- simulación ----------

    public void simulateFirstLegs(KnockoutPhase phase) {
        getTies(phase).forEach(tie -> MatchSimulator.simulateMatch(tie.getFirstLeg()));
        assignSecondLegFormations(phase);
    }

    public void simulateSecondLegs(KnockoutPhase phase) {
        getTies(phase).forEach(tie -> MatchSimulator.simulateMatch(tie.getSecondLeg()));
    }

    public void resolveTies(KnockoutPhase phase) {
        getTies(phase).stream()
                .filter(tie -> tie.getWinner() == null)
                .forEach(tie -> MatchSimulator.simulatePenaltyShootout(tie.getSecondLeg()));
    }

    public void simulateFinal() { MatchSimulator.simulateFinal(finalMatch); }

    public void resolveFinal() { MatchSimulator.simulatePenaltyShootout(finalMatch); }

    private void assignSecondLegFormations(KnockoutPhase phase) {
        for (KnockoutTie tie : getTies(phase)) {
            SecondLegMatch secondLeg = tie.getSecondLeg();
            secondLeg.setTeam1Formation(FormationCreator.createAutomaticFormation(secondLeg.getTeam1()));
            secondLeg.setTeam2Formation(FormationCreator.createAutomaticFormation(secondLeg.getTeam2()));
        }
    }

    // ---------- generación (árbitros y estadios llegan por parámetro porque son del torneo) ----------

    public void generateQuarterFinals(ArrayList<Zone> zones, ArrayList<Referee> referees,
                                      ArrayList<Stadium> stadiums, LocalDate after) {
        ties.clear();
        ties.addAll(KnockoutService.generateQuarterFinals(zones, referees, stadiums));
        FixtureScheduler.scheduleQuarterFinals(getTies(KnockoutPhase.QUARTER_FINAL), after);
    }

    public void generateNextPhase(KnockoutPhase finished, ArrayList<Referee> referees,
                                  ArrayList<Stadium> stadiums, LocalDate after) {
        switch (finished) {
            case QUARTER_FINAL -> {
                ties.addAll(KnockoutService.generateSemiFinals(getWinners(KnockoutPhase.QUARTER_FINAL), referees, stadiums));
                FixtureScheduler.scheduleSemiFinals(getTies(KnockoutPhase.SEMI_FINAL), after);
            }
            case SEMI_FINAL -> {
                finalMatch = KnockoutService.generateFinal(getWinners(KnockoutPhase.SEMI_FINAL), referees, stadiums);
                FixtureScheduler.scheduleFinal(finalMatch, after);
            }
        }
    }
}
