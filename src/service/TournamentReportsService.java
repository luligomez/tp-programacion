package service;

import model.tournament.Tournament;
import model.match.Match;
import model.person.Coach;
import model.person.Position;
import model.person.Referee;
import model.person.player.Player;
import model.person.player.TournamentStats;
import model.team.Team;
import model.reports.player.PlayerReportData;
import model.reports.player.PlayerReportItem;
import model.reports.referee.RefereeReportData;
import model.reports.referee.RefereeReportItem;
import model.reports.team.TeamReportData;
import model.reports.team.TeamReportItem;
import model.reports.scorer.ScorerReportItem;
import model.reports.fairplay.FairPlayReportItem;

import java.util.ArrayList;
import java.util.List;

public class TournamentReportsService {

    public static PlayerReportData generatePlayersReport(Tournament tournament) {
        List<PlayerReportItem> items = new ArrayList<>();

        for (Team team : tournament.getTeams()) {
            if (team.getPlayers() == null) continue;

            for (Player player : team.getPlayers()) {
                int age = player.getAge();
                TournamentStats playerStats = player.getTournamentStats();

                int matchesPlayed = playerStats.getMatchesPlayed();
                int minutesPlayed = playerStats.getMinutesPlayed();
                int goalsScored = playerStats.getGoals();

                int goalsConceded = 0;
                double goalsConcededPerMatch = 0.0;

                if (player.getPosition() == Position.GOALKEEPER) {
                    goalsConceded = playerStats.getGoalsConceded();
                    if (matchesPlayed > 0) {
                        goalsConcededPerMatch = (double) goalsConceded / matchesPlayed;
                    }
                }

                items.add(new PlayerReportItem(
                        player.getName(),
                        team.getName(),
                        player.getPosition(),
                        age,
                        player.getBirthDate(),
                        player.getDocumentType(),
                        player.getDocumentNumber(),
                        player.getRating(),
                        matchesPlayed,
                        minutesPlayed,
                        goalsScored,
                        goalsConceded,
                        goalsConcededPerMatch,
                        playerStats.getStandaloneYellowCards(),
                        playerStats.getDoubleYellowExpulsions(),
                        playerStats.getDirectRedCards()
                ));
            }
        }

        items.sort((p1, p2) -> p1.getPlayerName().compareToIgnoreCase(p2.getPlayerName()));
        return new PlayerReportData(items);
    }

    public static TeamReportData generateTeamsReportData(Tournament tournament) {
        List<TeamReportItem> items = new ArrayList<>();

        for (Team team : tournament.getTeams()) {

            // 1. Average age of its players
            double avgPlayerAge = 0.0;
            if (team.getPlayers() != null && !team.getPlayers().isEmpty()) {
                double totalAge = 0;
                for (Player player : team.getPlayers()) {
                    totalAge += player.getAge();
                }
                avgPlayerAge = totalAge / team.getPlayers().size();
            }

            // 2. Age and nationality of the coach
            int coachAge = 0;
            String coachNationality = "N/A";
            if (team.getCoach() != null) {
                Coach coach = team.getCoach();
                coachAge = coach.getAge();
                if (coach.getNationality() != null) {
                    coachNationality = coach.getNationality().getName();
                }
            }

            // 3. Goles a favor, en contra, partidos jugados y puntos obtenidos
            int gf = 0;
            int gc = 0;
            int matchesPlayed = 0;
            int pointsObtained = 0;

            for (Match match : tournament.getAllMatches()) {
                if (match != null && match.isPlayed()) {
                    boolean isTeam1 = match.getTeam1().equals(team);
                    boolean isTeam2 = match.getTeam2().equals(team);

                    // If the team played this match (either as team1 or team2)
                    if (isTeam1 || isTeam2) {
                        matchesPlayed++;

                        // Add goals depending on which side they played
                        if (isTeam1) {
                            gf += match.getTeam1Goals();
                            gc += match.getTeam2Goals();
                        } else {
                            gf += match.getTeam2Goals();
                            gc += match.getTeam1Goals();
                        }

                        // Unified points calculation (done only once)
                        if (isTeam1) {
                            pointsObtained += pointsOf(
                                    match.getTeam1Goals(),
                                    match.getTeam2Goals()
                            );
                        } else {
                            pointsObtained += pointsOf(
                                    match.getTeam2Goals(),
                                    match.getTeam1Goals()
                            );
                        }
                    }
                }
            }

            // 4. Efectividad (% de puntos obtenidos sobre puntos posibles)
            double effectiveness = 0.0;
            if (matchesPlayed > 0) {
                int maxPossiblePoints = matchesPlayed * 3;
                effectiveness = (pointsObtained * 100.0) / maxPossiblePoints;
            }

            items.add(new TeamReportItem(
                    team.getName(),
                    avgPlayerAge,
                    coachAge,
                    coachNationality,
                    gf,
                    gc,
                    effectiveness
            ));
        }

        // 5. Sort alphabetically by team name
        items.sort((t1, t2) -> t1.getTeamName().compareToIgnoreCase(t2.getTeamName()));

        return new TeamReportData(items);
    }

    public static RefereeReportData generateRefereesReportData(Tournament tournament) {
        List<RefereeReportItem> items = new ArrayList<>();
        List<Referee> referees = tournament.getReferees();
        int matchesOfficiated, totalYears=0, yrsExp;
        for(Referee ref : referees){
            yrsExp = ref.getYearsOfExperience();
            totalYears+= yrsExp;
            matchesOfficiated = (int) tournament.getAllMatches().stream().filter(match -> match != null && match.isPlayed() && match.getReferee() != null && match.getReferee().equals(ref)).count();
            items.add(new RefereeReportItem(ref.getName(), ref.getNationality().getName(), matchesOfficiated, yrsExp));
        }
        items.sort((r1, r2) -> Integer.compare(r2.getMatchesOfficiated(), r1.getMatchesOfficiated()));
        double avgYears = referees.isEmpty() ? 0.0 : (double) totalYears /referees.size();
        return new RefereeReportData(items,avgYears);
    }

    // SCORER RANKING

    public static List<ScorerReportItem> generateScorersRanking(Tournament tournament) {

        List<ScorerReportItem> scorers = new ArrayList<>();

        for (Team team : tournament.getTeams()) {

            if (team.getPlayers() == null) {
                continue;
            }

            for (Player player : team.getPlayers()) {

                TournamentStats stats = player.getTournamentStats();

                if (stats != null && stats.getGoals() > 0) {
                    scorers.add(new ScorerReportItem(
                            player.getName(),
                            team.getName(),
                            stats.getGoals(),
                            stats.getPenaltyGoals()
                    ));
                }
            }
        }

        scorers.sort((a, b) -> {
            int result = Integer.compare(b.getGoals(), a.getGoals());

            if (result != 0) {
                return result;
            }

            return a.getPlayerName().compareToIgnoreCase(b.getPlayerName());
        });

        return scorers;
    }
    // FAIR PLAY RANKING

    public static List<FairPlayReportItem> generateFairPlayRanking(Tournament tournament) {

        List<FairPlayReportItem> ranking = new ArrayList<>();

        for (Team team : tournament.getTeams()) {

            int yellowCards = 0;
            int doubleYellowExpulsions = 0;
            int directRedCards = 0;

            if (team.getPlayers() == null) {
                continue;
            }

            for (Player player : team.getPlayers()) {

                TournamentStats stats = player.getTournamentStats();

                if (stats != null) {
                    yellowCards += stats.getStandaloneYellowCards();
                    doubleYellowExpulsions += stats.getDoubleYellowExpulsions();
                    directRedCards += stats.getDirectRedCards();
                }
            }

            ranking.add(new FairPlayReportItem(
                    team.getName(),
                    yellowCards,
                    doubleYellowExpulsions,
                    directRedCards
            ));
        }

        // Sort teams by penalty points, lowest first
        ranking.sort((a, b) -> {
            int result = Integer.compare(
                    a.getPenaltyPoints(),
                    b.getPenaltyPoints()
            );

            if (result != 0) {
                return result;
            }

            return a.getTeamName().compareToIgnoreCase(b.getTeamName());
        });

        return ranking;
    }

    private static int pointsOf(int goalsFor, int goalsAgainst) {
        if (goalsFor > goalsAgainst) {
            return 3;
        }
        if (goalsFor == goalsAgainst) {
            return 1;
        }
        return 0;
    }
}