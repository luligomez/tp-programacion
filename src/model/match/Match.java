package model.match;

import model.match.incident.Expulsion;
import model.match.incident.Incident;
import model.match.incident.Goal;
import model.match.incident.YellowCard;
import model.person.player.Player;
import model.place.Stadium;
import model.Team;
import model.person.Referee;
import model.match.incident.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

public abstract class Match implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private LocalDate date;
    private Team team1;
    private Team team2;
    private Formation team1Formation;
    private Formation team2Formation;
    private Referee referee;
    private ArrayList<Incident> incidents;
    private int team1Goals;
    private int team2Goals;
    private ArrayList<PlayerParticipation> playerParticipations = new ArrayList<>();
    private Stadium stadium;

    public Match(LocalDate date, Team team1, Team team2, Referee referee,
                 Formation team1Formation, Formation team2Formation, Stadium stadium) {

        this.date = date;
        this.team1 = team1;
        this.team2 = team2;
        this.referee = referee;
        this.incidents = new ArrayList<>();
        this.team1Goals = -1; //(no jugado aun)
        this.team2Goals = -1;
        this.team1Formation = team1Formation;
        this.team2Formation = team2Formation;
        this.stadium = stadium;
    }

    public void initializePlayerParticipation(Formation team1Formation, Formation team2Formation){
        addFormationParticipations(team1Formation);
        addFormationParticipations(team2Formation);
    }

    private void addFormationParticipations(Formation formation) {

        for (Player player : formation.getStarters()) {
            playerParticipations.add(new PlayerParticipation(player, true, 0, 90));
        }
        for (Player player : formation.getSubstitutes()) {
            playerParticipations.add(new PlayerParticipation(player, false, -1, -1));

        }
    }
    public LocalDate getDate() {
        return date;
    }

    public Team getTeam1() {
        return team1;
    }

    public Team getTeam2() {
        return team2;
    }

    public Referee getReferee() {
        return referee;
    }

    public ArrayList<Incident> getIncidents() {
        return incidents;
    }

    public void addIncident(Incident incident) {
        if (incident == null) return;

        // Se guarda en el historial cronológico del partido
        incidents.add(incident);

        // 1. GOLES
        if (incident instanceof Goal goal) {
            if (!goal.isOwnGoal() && goal.getScorer() != null) {
                goal.getScorer()
                        .getTournamentStats()
                        .registerGoal(goal.isPenalty());
            }
        }
        // 2. AMARILLAS
        else if (incident instanceof YellowCard yc) {
            if (yc.getPlayer() != null) {
                yc.getPlayer()
                        .getTournamentStats()
                        .registerStandaloneYellow();
            }
        }
        // 3. EXPULSIONES
        else if (incident instanceof Expulsion exp) {
            if (exp.getPlayer() != null) {
                if (exp.isDoubleYellow()) {
                    exp.getPlayer().getTournamentStats().revertStandaloneYellow();
                    exp.getPlayer().getTournamentStats().registerDoubleYellowExpulsion();
                } else {
                    exp.getPlayer().getTournamentStats().registerDirectRed();
                }
            }
        }
    }

    public int getTeam1Goals() {
        return team1Goals;
    }

    public int getTeam2Goals() {
        return team2Goals;
    }

    public void setResult(int team1Goals, int team2Goals) {
        this.team1Goals = team1Goals;
        this.team2Goals = team2Goals;
    }

    public Stadium getStadium() {
        return stadium;
    }

    public Formation getTeam1Formation() {
        return team1Formation;
    }

    public Formation getTeam2Formation() {
        return team2Formation;
    }

    public void registerPlayerStatistics() {

        for (PlayerParticipation participation : playerParticipations) {

            Player player = participation.getPlayer();

            int minutes = 0;

            if (participation.getMinuteIn() >= 0 &&
                    participation.getMinuteOut() >= 0) {

                minutes = participation.getMinuteOut()
                        - participation.getMinuteIn();
            }

            if (minutes > 0) {
                player.getTournamentStats()
                        .registerMatchPlayed(minutes);
            }
        }
    }

    public ArrayList<PlayerParticipation> getPlayerParticipations() {
        return playerParticipations;
    }

    public PlayerParticipation getParticipationFor(Player player) {
        if (player == null) return null;//TODO exception

        for (PlayerParticipation pp : playerParticipations) {
            if (pp.getPlayer().equals(player)) {
                return pp;
            }
        }
        return null;
    }

    public boolean isPlayed() {
        return this.team1Goals != -1;
    }

    public abstract Team getWinner();

    public void setTeam1Formation(Formation team1Formation) {
        this.team1Formation = team1Formation;
    }

    public void setTeam2Formation(Formation team2Formation) {
        this.team2Formation = team2Formation;
    }
}