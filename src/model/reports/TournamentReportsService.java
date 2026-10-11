package model.reports;

import model.Tournament;
import model.match.Match;
import model.person.Coach;
import model.person.Position;
import model.person.Referee;
import model.person.player.Player;
import model.person.player.TournamentStats;
import model.Team;
import model.reports.player.PlayerReportData;
import model.reports.player.PlayerReportItem;
import model.reports.referee.RefereeReportData;
import model.reports.referee.RefereeReportItem;
import model.reports.team.TeamReportData;
import model.reports.team.TeamReportItem;

import java.util.ArrayList;
import java.util.List;

public class TournamentReportsService {

    public static PlayerReportData generatePlayersReport(Tournament tournament) {
        List<PlayerReportItem> items = new ArrayList<>();

        for (Team team : tournament.getTeams()) {
            if (team.getPlayers() == null) continue;

            for (Player player : team.getPlayers()) {
                int age = 0;
                if (player.getBirthDate() != null) {
                    age = java.time.Period.between(player.getBirthDate(), java.time.LocalDate.now()).getYears();
                }
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
                        matchesPlayed,
                        minutesPlayed,
                        goalsScored,
                        goalsConceded,
                        goalsConcededPerMatch
                ));
            }
        }

        items.sort((p1, p2) -> p1.getPlayerName().compareToIgnoreCase(p2.getPlayerName()));
        return new PlayerReportData(items);
    }

    public static TeamReportData generateTeamsReportData(Tournament tournament) {
        List<TeamReportItem> items = new ArrayList<>();

        for (Team team : tournament.getTeams()) {

            // 1. Edad promedio de sus jugadores
            double avgPlayerAge = 0.0;
            if (team.getPlayers() != null && !team.getPlayers().isEmpty()) {
                double totalAge = 0;
                for (Player player : team.getPlayers()) {
                    if (player.getBirthDate() != null) {
                        totalAge += java.time.Period.between(player.getBirthDate(), java.time.LocalDate.now()).getYears();
                    }
                }
                avgPlayerAge = totalAge / team.getPlayers().size();
            }

            // 2. Edad y Nacionalidad del DT
            int coachAge = 0;
            String coachNationality = "N/A";
            if (team.getCoach() != null) {
                Coach coach = team.getCoach();
                if (coach.getBirthDate() != null) {
                    coachAge = java.time.Period.between(coach.getBirthDate(), java.time.LocalDate.now()).getYears();
                }
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
                    if (match.getTeam1().equals(team)) {
                        matchesPlayed++;
                        gf += match.getTeam1Goals();
                        gc += match.getTeam2Goals();
                        if (match.getWinner() != null && match.getWinner().equals(team)) pointsObtained += 3;
                        else if (match.getWinner() == null) pointsObtained += 1;
                    } else if (match.getTeam2().equals(team)) {
                        matchesPlayed++;
                        gf += match.getTeam2Goals();
                        gc += match.getTeam1Goals();
                        if (match.getWinner() != null && match.getWinner().equals(team)) pointsObtained += 3;
                        else if (match.getWinner() == null) pointsObtained += 1;
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

        // 5. Ordenar alfabéticamente por nombre del equipo
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

}