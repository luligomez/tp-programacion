package model;

import model.exception.InvalidFormationException;
import model.match.Formation;
import model.match.GroupStageMatch;
import model.match.Match;
import model.match.PlayerParticipation;
import model.match.incident.*;
import model.person.Position;
import model.person.player.FieldPlayer.FieldPlayer;
import model.zone.Zone;
import model.person.player.Player;

import java.util.*;

public class MatchSimulator {

    //simular X fecha de la fase de grupos, de cada zona (fecha 1, 2, o 3)
    public static void simulateMatchday(Tournament tournament, int matchday) {
        for (Zone zone : tournament.getZones()) {
            GroupStageMatch match1;
            GroupStageMatch match2;

            switch (matchday) {
                case 1:
                    match1 = zone.getGroupStageMatches().get(0);
                    match2 = zone.getGroupStageMatches().get(5);
                    break;
                case 2:
                    match1 = zone.getGroupStageMatches().get(1);
                    match2 = zone.getGroupStageMatches().get(4);
                    break;
                case 3:
                    match1 = zone.getGroupStageMatches().get(2);
                    match2 = zone.getGroupStageMatches().get(3);
                    break;
                default:
                    throw new RuntimeException("Invalid matchday.");
            }

            // Simulamos y registramos
            simulateMatch(match1);
            zone.registerMatchResult(match1);

            simulateMatch(match2);
            zone.registerMatchResult(match2);
        }
    }

    public static void simulateQuarterFinals(Tournament tournament) {

    }

    public static void simulateSemifinals(Tournament tournament) {
        // ...
    }

    public static void simulateFinal(Tournament tournament) {

    }

    public static void simulateMatch(Match match) {

        // Si ya se simuló, salimos
        if (match.isPlayed()) {
            System.out.println("El partido entre " + match.getTeam1().getName() + //TODO agregar exception
                    " y " + match.getTeam2().getName() + " ya fue simulado.");
            return;
        }

        Team team1 = match.getTeam1();
        Team team2 = match.getTeam2();
        Formation form1 = match.getTeam1Formation();
        Formation form2 = match.getTeam2Formation();
        Random random = new Random();

        // Jugadores en cancha (activos)
        List<Player> activeTeam1 = new ArrayList<>(form1.getStarters());
        List<Player> activeTeam2 = new ArrayList<>(form2.getStarters());

        // Suplentes en el banco
        List<Player> benchTeam1 = new ArrayList<>(form1.getSubstitutes());
        List<Player> benchTeam2 = new ArrayList<>(form2.getSubstitutes());

        Map<Player, Integer> yellowCardsMap = new HashMap<>();
        int subsTeam1 = 0, subsTeam2 = 0;
        int goalsTeam1 = 0, goalsTeam2 = 0;


        // BUCLE MINUTO A MINUTO
        for (int minute = 1; minute <= 90; minute+=1) {

            // A. Recalcular poderes de los jugadores en cancha
            double attack1 = calculateAttackPower(team1, activeTeam1);
            double defense1 = calculateDefensePower(team1, activeTeam1);
            double attack2 = calculateAttackPower(team2, activeTeam2);
            double defense2 = calculateDefensePower(team2, activeTeam2);

            // B. Sustituciones (Minutos 45 a 85)
            if (minute >= 45 && minute <= 85) {
                if (subsTeam1 < 5 && random.nextDouble() < 0.03) {
                    if (trySubstitution(match, team1, activeTeam1, benchTeam1, minute, random)) {
                        subsTeam1++;
                    }
                }
                if (subsTeam2 < 5 && random.nextDouble() < 0.03) {
                    if (trySubstitution(match, team2, activeTeam2, benchTeam2, minute, random)) {
                        subsTeam2++;
                    }
                }
            }

            // C. Tarjetas y Expulsiones
            simulateCardsForMinute(match, team1, activeTeam1, minute, yellowCardsMap, random);
            simulateCardsForMinute(match, team2, activeTeam2, minute, yellowCardsMap, random);

            // D. Oportunidades de Gol
            if (checkGoalOccurred(attack1, defense2, random)) {
                goalsTeam1++;
                Player currentGk2 = getCurrentGoalkeeper(activeTeam2);
                registerGoalIncident(match, team1, activeTeam1, activeTeam2, currentGk2, minute, random);
            }

            if (checkGoalOccurred(attack2, defense1, random)) {
                goalsTeam2++;
                Player currentGk1 = getCurrentGoalkeeper(activeTeam1);
                registerGoalIncident(match, team2, activeTeam2, activeTeam1, currentGk1, minute, random);
            }
        }

        match.setResult(goalsTeam1, goalsTeam2);
        match.registerPlayerStatistics();
    }

    // MANEJO DE SUSTITUCIONES Y PARTICIPACIÓN
    private static boolean trySubstitution(Match match, Team team, List<Player> activePlayers, List<Player> bench, int minute, Random random) {
        if (activePlayers.isEmpty() || bench.isEmpty()) return false;

        Player playerOut = activePlayers.get(random.nextInt(activePlayers.size()));
        Player playerIn;

        if (playerOut.getPosition() == Position.GOALKEEPER) {
            Player subGk = findGoalkeeper(bench);
            playerIn = (subGk != null) ? subGk : bench.get(random.nextInt(bench.size()));
        } else {
            playerIn = bench.get(random.nextInt(bench.size()));
        }

        bench.remove(playerIn);
        activePlayers.remove(playerOut);
        activePlayers.add(playerIn);

        // 1. Actualizar al que sale
        PlayerParticipation outPart = match.getParticipationFor(playerOut);
        if (outPart != null) {
            outPart.setMinuteOut(minute);
        }

        // 2. Actualizar al suplente que entra (arrancó en -1, -1)
        PlayerParticipation inPart = match.getParticipationFor(playerIn);
        if (inPart != null) {
            inPart.setMinuteIn(minute);
            inPart.setMinuteOut(90);
        }

        // 3. Registrar incidencia
        Substitution substitution = new Substitution(minute, playerOut, playerIn);
        match.addIncident(substitution);

        return true;
    }

    // MANEJO DE EXPULSIONES Y PARTICIPACIÓN
    private static void simulateCardsForMinute(Match match, Team team, List<Player> activePlayers,
                                               int minute, Map<Player, Integer> yellowCardsMap, Random random) {
        if (activePlayers.isEmpty()) return;

        // Chance de que ocurra una falta con tarjeta en este minuto (ej: 2.5%)
        if (random.nextDouble() < 0.015) {
            Player player = selectWeightedFouler(activePlayers, yellowCardsMap, random);

            // 1. Evaluar si es ROJA DIRECTA (ej: 5% de las tarjetas son rojas directas)
            boolean isDirectRed = random.nextDouble() < 0.05;

            if (isDirectRed) {
                Expulsion expulsion = new Expulsion(minute, player, false); // false = roja directa
                match.addIncident(expulsion);

                // El jugador es retirado de la cancha
                activePlayers.remove(player);

                PlayerParticipation pp = match.getParticipationFor(player);
                if (pp != null) {
                    pp.setMinuteOut(minute);
                }
                return;
            }

            // 2. Si no fue roja directa, es AMARILLA
            int currentYellows = yellowCardsMap.getOrDefault(player, 0) + 1;
            yellowCardsMap.put(player, currentYellows);

            if (currentYellows == 1) {
                // Primera tarjeta amarilla
                YellowCard yellowCard = new YellowCard(minute, player);
                match.addIncident(yellowCard);

            } else if (currentYellows == 2) {
                // Segunda amarilla -> DOBLE AMARILLA Y EXPULSIÓN
                YellowCard yellowCard = new YellowCard(minute, player);
                match.addIncident(yellowCard);

                Expulsion expulsion = new Expulsion(minute, player, true); // true = doble amarilla
                match.addIncident(expulsion);

                // El jugador es retirado de la cancha
                activePlayers.remove(player);

                PlayerParticipation pp = match.getParticipationFor(player);
                if (pp != null) {
                    pp.setMinuteOut(minute);
                }
            }
        }
    }

    // AUXILIARES
    private static boolean checkGoalOccurred(double attack, double opposingDefense, Random random) {
        double advantage = attack - opposingDefense;
        double minuteChance = Math.max(0.002, Math.min(0.06, 0.018 + (advantage * 0.0008))); //0,2% - 6% goal/min
        return random.nextDouble() < minuteChance;
    }

    private static Player selectWeightedScorer(List<Player> activePlayers, Random random) {
        if (activePlayers.isEmpty()) return null;

        double totalWeight = 0.0;
        Map<Player, Double> weights = new HashMap<>();

        // Calcular el peso para cada jugador según su posición y Finishing
        for (Player p : activePlayers) {
            double weight = 0.0;

            // Obtener el Finishing si es un FieldPlayer
            int finishing = 0;
            if (p instanceof FieldPlayer) {
                finishing = ((FieldPlayer) p).getATTRIBUTES().getFINISHING();
            }

            switch (p.getPosition()) {
                case FORWARD:
                    weight = 60.0 + finishing;
                    break;
                case MIDFIELDER:
                    weight = 20.0 + (finishing * 0.5);
                    break;
                case DEFENDER:
                    weight = 3.0 + (finishing * 0.1);
                    break;
                case GOALKEEPER:
                    weight = 0.0001;
                    break;
            }

            weights.put(p, weight);
            totalWeight += weight;
        }

        if (totalWeight <= 0) return activePlayers.get(0); //TODO exception?

        // 2. Ruleta / Sorteo Ponderado
        double randomValue = random.nextDouble() * totalWeight; // [0.0, totalWeight)
        double cumulative = 0.0;

        for (Player p : activePlayers) {
            cumulative += weights.getOrDefault(p, 0.0);
            if (randomValue <= cumulative) {
                return p;
            }
        }

        return activePlayers.get(0); //TODO exception?
    }

    private static Player selectWeightedFouler(List<Player> activePlayers, Map<Player, Integer> yellowCardsMap, Random random) {
        if (activePlayers.isEmpty()) return null;

        double totalWeight = 0.0;
        Map<Player, Double> weights = new HashMap<>();

        for (Player p : activePlayers) {
            double weight;

            switch (p.getPosition()) {
                case DEFENDER -> weight = 50.0;
                case MIDFIELDER -> weight = 35.0;
                case FORWARD -> weight = 12.0;
                case GOALKEEPER -> weight = 3.0;
                default -> weight = 10.0;
            }

            // REDUCCIÓN POR PRUDENCIA: Si ya tiene 1 amarilla, juega con más cuidado (se reduce su peso un 40%)
            if (yellowCardsMap.getOrDefault(p, 0) == 1) {
                weight *= 0.6;
            }

            weights.put(p, weight);
            totalWeight += weight;
        }

        // Ruleta / Sorteo Ponderado
        double randomValue = random.nextDouble() * totalWeight;
        double cumulative = 0.0;

        for (Player p : activePlayers) {
            cumulative += weights.getOrDefault(p, 0.0);
            if (randomValue <= cumulative) {
                return p;
            }
        }

        return activePlayers.get(activePlayers.size() - 1);
    }

    private static void registerGoalIncident(Match match, Team scoringTeam, List<Player> activeScoringTeam, List<Player> activeDefendingTeam, Player opposingGoalkeeper, int minute, Random random) {
        if (activeScoringTeam.isEmpty()) return;

        boolean isPenalty = random.nextDouble() < 0.12;
        boolean isOwnGoal = !isPenalty && (random.nextDouble() < 0.02);

        Player scorer;

        if (isOwnGoal) {
            // El autor se elige entre los DEFENDORES o ARQUERO del equipo rival
            scorer = selectOwnGoalScorer(activeDefendingTeam, random);
        } else {
            scorer = selectWeightedScorer(activeScoringTeam, random);
        }

        if (scorer == null) return;

        if (isPenalty) {
            PenaltyTaken penaltyTaken = new PenaltyTaken(minute, scorer, true);
            match.addIncident(penaltyTaken);
        }

        // Se registra la incidencia indicando isOwnGoal = true
        Goal goal = new Goal(minute, scorer, isPenalty, isOwnGoal, opposingGoalkeeper);
        match.addIncident(goal);
    }

    private static Player selectOwnGoalScorer(List<Player> defendingPlayers, Random random) {
        if (defendingPlayers.isEmpty()) return null;

        // Filtrar defensores y arqueros
        List<Player> candidates = defendingPlayers.stream()
                .filter(p -> p.getPosition() == Position.DEFENDER || p.getPosition() == Position.GOALKEEPER)
                .toList();

        if (candidates.isEmpty()) {
            candidates = defendingPlayers;
        }

        return candidates.get(random.nextInt(candidates.size()));
    }

    private static double calculateAttackPower(Team team, List<Player> activePlayers) {
        if (activePlayers.isEmpty()) return 50.0;
        double sum = 0;
        int count = 0;
        for (Player p : activePlayers) {
            if (p.getPosition() == Position.FORWARD || p.getPosition() == Position.MIDFIELDER) {
                sum += p.getRating();
                count++;
            }
        }
        double avg = (count > 0) ? (sum / count) : 60.0;

        double basePower = avg + calculateTeamBonus(team);

        // (el poder depende de los jugadores en cancha)
        double numericalFactor = activePlayers.size() / 11.0;

        return basePower * numericalFactor;
    }

    private static double calculateDefensePower(Team team, List<Player> activePlayers) {
        if (activePlayers.isEmpty()) return 50.0;
        double sum = 0;
        int count = 0;
        for (Player p : activePlayers) {
            if (p.getPosition() == Position.DEFENDER || p.getPosition() == Position.GOALKEEPER) {
                sum += p.getRating();
                count++;
            }
        }
        double avg = (count > 0) ? (sum / count) : 60.0;
        // (promedio + bonus del equipo)
        double basePower = avg + calculateTeamBonus(team);

        // (el poder depende de los jugadores en cancha)
        double numericalFactor = activePlayers.size() / 11.0;

        return basePower * numericalFactor;
    }

    private static double calculateTeamBonus(Team team) {
        if (team == null) return 0.0;
        double rankingBonus = Math.max(0, (100 - team.getRankingPosition()) * 0.1);
        double coachBonus = (team.getCoach() != null) ? team.getCoach().getTitlesWon() * 0.5 : 0.0;
        return rankingBonus + coachBonus;
    }

    private static Player getCurrentGoalkeeper(List<Player> activePlayers) {
        for (Player p : activePlayers) {
            if (p.getPosition() == Position.GOALKEEPER) return p;
        }
        return activePlayers.isEmpty() ? null : activePlayers.get(0);
    }

    private static Player findGoalkeeper(List<Player> players) {
        for (Player p : players) {
            if (p.getPosition() == Position.GOALKEEPER) return p;
        }
        return null;
    }
}
