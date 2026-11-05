package model.match;

import model.match.incident.Incident;
import model.person.player.Player;
import model.place.Stadium;
import model.team.Team;
import model.person.Referee;
import model.reports.match.MatchReport.*;
import java.util.Collections;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class Match implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private LocalDateTime dateTime;
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
    private boolean playerStatisticsRegistered = false;

    public Match(Team team1, Team team2, Referee referee,
                 Formation team1Formation, Formation team2Formation, Stadium stadium) {
        this.team1 = team1;
        this.team2 = team2;
        this.referee = referee;
        this.incidents = new ArrayList<>();
        this.team1Goals = -1; // (not played yet)
        this.team2Goals = -1;
        this.team1Formation = team1Formation;
        this.team2Formation = team2Formation;
        this.stadium = stadium;
    }

    // Secondary constructor (calls the main one passing null for formations)
    public Match(Team team1, Team team2, Referee referee, Stadium stadium) {
        this(team1, team2, referee, null, null, stadium);
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
    public LocalDateTime getDateTime() {
        return dateTime;
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

    public List<Incident> getIncidents() {
        return Collections.unmodifiableList(incidents);
    }

    public void addIncident(Incident incident) {
        if (incident == null) return;

        // It is stored in the match's chronological history
        incidents.add(incident);
        incident.registerStats();
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

        // Avoid registering the stats of the same match twice
        if (playerStatisticsRegistered) {
            return;
        }

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

        // Mark the stats as registered
        playerStatisticsRegistered = true;
    }

    public ArrayList<PlayerParticipation> getPlayerParticipations() {
        return playerParticipations;
    }

    public PlayerParticipation getParticipationFor(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }
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

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isTeam1Player(Player player) {
        return team1.getPlayers().contains(player);
    }

    public List<Player> getPlayersOnPitchAtEnd(Team team) {
        Formation formation = (team == team1) ? team1Formation : team2Formation;
        return Stream.concat(formation.getStarters().stream(), formation.getSubstitutes().stream())
                .filter(p -> isOnPitchAtEnd(p, formation.getStarters().contains(p)))
                .toList();
    }

    private boolean isOnPitchAtEnd(Player player, boolean starter) {
        List<MarkType> marks = incidents.stream()
                .map(incident -> incident.markFor(player))
                .flatMap(Optional::stream)
                .map(PlayerMark::type)
                .toList();
        boolean cameOn = marks.contains(MarkType.SUB_IN);
        boolean left = marks.contains(MarkType.SUB_OUT) || marks.contains(MarkType.RED);
        return (starter || cameOn) && !left;
    }

    public Optional<PenaltyShootoutReport> toShootoutReport() {
        return Optional.empty();
    }
}