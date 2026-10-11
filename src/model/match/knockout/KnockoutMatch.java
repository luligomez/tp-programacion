package model.match.knockout;

import model.Team;
import model.match.Formation;
import model.match.Match;
import model.person.Referee;
import model.place.Stadium;
import model.reports.MatchReport;
import model.match.knockout.PenaltyShootout.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class KnockoutMatch extends Match {
    private PenaltyShootout penalties;
    private String winningCriteria;

    public KnockoutMatch(Team team1, Team team2, Referee referee, Formation team1Formation, Formation team2Formation, Stadium stadium) {
        super(team1, team2, referee, team1Formation, team2Formation, stadium);
    }

    public KnockoutMatch(Team team1, Team team2, Referee referee, Stadium stadium) {
        super(team1, team2, referee, stadium);
    }

    public PenaltyShootout getPenalties() {
        return penalties;
    }

    public void setPenalties(PenaltyShootout penaltyShootout) {
        this.penalties = penaltyShootout;
    }

    public void setWinningCriteria(String winningCriteria) {
        this.winningCriteria = winningCriteria;
    }

    public boolean hasPenalties(){
        return penalties!=null;
    }

    public String getWinningCriteria() {
        return winningCriteria;
    }

    public int getPenaltiesScored(Team team) {
        return penalties == null ? 0 : penalties.getGoals(team);
    }

    @Override
    public Optional<MatchReport.PenaltyShootoutReport> toShootoutReport() {
        if (penalties == null) return Optional.empty();

        List<PenaltyKick> kicks1 = new ArrayList<>();
        List<PenaltyKick> kicks2 = new ArrayList<>();
        for (PenaltyKick kick : penalties.getKicks()) {          // ver nota abajo
            PenaltyKick entry = new PenaltyKick(kick.getTeam(), kick.getKicker(), kick.hasScored());
            (isTeam1Player(kick.getKicker()) ? kicks1 : kicks2).add(entry);
        }

        List<MatchReport.PenaltyRound> rounds = new ArrayList<>();
        int total = Math.max(kicks1.size(), kicks2.size());
        for (int i = 0; i < total; i++) {
            rounds.add(new MatchReport.PenaltyRound(i + 1,
                    i < kicks1.size() ? kicks1.get(i) : null,
                    i < kicks2.size() ? kicks2.get(i) : null));
        }

        Team winner = penalties.getWinner();
        return Optional.of(new MatchReport.PenaltyShootoutReport(rounds,
                penalties.getTeam1Goals(), penalties.getTeam2Goals(),
                winner != null ? winner.getName() : "-"));
    }

}
