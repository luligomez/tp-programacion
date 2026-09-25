package model;

import model.match.Formation;
import model.match.Match;
import model.match.PlayerParticipation;
import model.match.incident.*;
import model.person.Position;
import model.person.player.FieldPlayer.FieldPlayer;
import model.person.player.Player;

import java.util.*;

public class MatchSimulator {

    //simular X fecha de la fase de grupos, de cada zona (fecha 1, 2, o 3)
    public static void simulateMatchday(Tournament tournament, int matchday) {
        tournament.getZones().forEach(zone -> {
            zone.getGroupStageMatches().stream()
                    .filter(match -> match.getMATCHDAY() == matchday && !match.isPlayed())
                    .forEach(match -> {
                        simulateMatch(match);
                        zone.registerMatchResult(match);
                    });
        });
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
        match.setTeam1Formation(FormationCreator.createAutomaticFormation(team1));
        match.setTeam2Formation(FormationCreator.createAutomaticFormation(team2));

        Formation form1 = match.getTeam1Formation();
        Formation form2 = match.getTeam2Formation();
        match.initializePlayerParticipation(form1, form2);

        Random random = new Random();
        boolean isGkAlreadyExpelled1 = false;
        boolean isGkAlreadyExpelled2 = false;

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
                    if (trySubstitution(match, activeTeam1, benchTeam1, goalsTeam1, goalsTeam2, minute, random)) {
                        subsTeam1++;
                    }
                }
                if (subsTeam2 < 5 && random.nextDouble() < 0.03) {
                    if (trySubstitution(match, activeTeam2, benchTeam2, goalsTeam2, goalsTeam1, minute, random)) {
                        subsTeam2++;
                    }
                }
            }

            // C. Tarjetas y Expulsiones
            isGkAlreadyExpelled1=simulateCardsForMinute(match, activeTeam1, benchTeam1, minute, yellowCardsMap, isGkAlreadyExpelled1, random);
            isGkAlreadyExpelled2=simulateCardsForMinute(match, activeTeam2, benchTeam2, minute, yellowCardsMap, isGkAlreadyExpelled2, random);

            // D. Oportunidades de Gol
            if (checkGoalOccurred(attack1, defense2, random)) {
                goalsTeam1++;
                Player currentGk2 = getCurrentGoalkeeper(activeTeam2);
                registerGoalIncident(match, activeTeam1, activeTeam2, currentGk2, minute, random);
            }

            if (checkGoalOccurred(attack2, defense1, random)) {
                goalsTeam2++;
                Player currentGk1 = getCurrentGoalkeeper(activeTeam1);
                registerGoalIncident(match, activeTeam2, activeTeam1, currentGk1, minute, random);
            }
        }

        match.setResult(goalsTeam1, goalsTeam2);
        match.registerPlayerStatistics();
    }

    // MANEJO DE SUSTITUCIONES Y PARTICIPACIÓN
    private static boolean trySubstitution(Match match , List<Player> activePlayers, List<Player> bench, int teamGoals, int opponentGoals, int minute, Random random) {
        if (activePlayers.isEmpty() || bench.isEmpty()) return false;

        Player playerOut = null;
        Player playerIn = null;
        int scoreDiff = teamGoals - opponentGoals;

        // SI VA PERDIENDO:
        if (scoreDiff < 0) {
            // Busca sacar un Defensor de la cancha
            playerOut = findRandomByPosition(activePlayers, Position.DEFENDER, random);
            // Y busca meter un Delantero del banco
            playerIn = findRandomByPositions(bench, Position.FORWARD, Position.MIDFIELDER, random);
        }
        // SI VA GANANDO:
        else if (scoreDiff > 0) {
            // Busca sacar un Delantero de la cancha
            playerOut = findRandomByPosition(activePlayers, Position.FORWARD, random);
            // Y busca meter un Defensor del banco
            playerIn = findRandomByPositions(bench, Position.DEFENDER, Position.MIDFIELDER, random);
        }

        // C) SI VAN EMPATANDO
        if (playerOut == null || playerIn == null) {
            // Elegimos al jugador de cancha de menor rating
            playerOut = getLowestRatingPlayer(activePlayers);

            if (playerOut != null) {
                // Buscamos en el banco alguien de su misma posición
                playerIn = findRandomByPosition(bench, playerOut.getPosition(), random);
            }
        }

        // Si aún así no hay coincidencia exacta de posición en el banco, metemos al mejor suplente disponible
        if (playerIn == null) {
            playerIn = getHighestRatingPlayer(bench);
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

    private static Player findRandomByPosition(List<Player> bench, Position pos, Random random) {
        List<Player> candidates = bench.stream()
                .filter(p -> p.getPosition() == pos)
                .toList();

        if (candidates.isEmpty()) return null;
        return candidates.get(random.nextInt(candidates.size()));
    }

    private static Player findRandomByPositions(List<Player> bench, Position pos1, Position pos2, Random random) {
        List<Player> candidates = bench.stream()
                .filter(p -> p.getPosition() == pos1 || p.getPosition() == pos2)
                .toList();

        if (candidates.isEmpty()) return null;
        return candidates.get(random.nextInt(candidates.size()));
    }

    private static Player getLowestRatingPlayer(List<Player> players) {
        return players.stream()
                .filter(p -> p.getPosition() != Position.GOALKEEPER) // El arquero no se cambia
                .min(Comparator.comparingDouble(Player::getRating))
                .orElse(null);
    }

    private static Player getHighestRatingPlayer(List<Player> bench) {
        return bench.stream()
                .max(Comparator.comparingDouble(Player::getRating))
                .orElse(null);
    }

    // MANEJO DE EXPULSIONES Y PARTICIPACIÓN
    private static boolean simulateCardsForMinute(Match match, List<Player> activePlayers, List<Player> benchPlayers,
                                               int minute, Map<Player, Integer> yellowCardsMap, boolean isGkAlreadyExpelled, Random random) {
        if (activePlayers.isEmpty()) return false;

        // Chance de que ocurra una falta con tarjeta en este minuto (ej: 1.5%)
        if (random.nextDouble() < 0.015) {
            Player player = selectWeightedFouler(activePlayers, yellowCardsMap, isGkAlreadyExpelled, random);

            boolean isDirectRed = random.nextDouble() < 0.05;
            boolean isDoubleYellow = false;

            if (!isDirectRed) {
                // Es amarilla (1ª o 2ª)
                int currentYellows = yellowCardsMap.getOrDefault(player, 0) + 1;
                yellowCardsMap.put(player, currentYellows);

                // Registramos la tarjeta amarilla recibida en la jugada
                YellowCard yellowCard = new YellowCard(minute, player);
                match.addIncident(yellowCard);

                if (currentYellows == 2) {
                    isDoubleYellow = true;
                }
            }

            // Si es expulsion
            if (isDirectRed || isDoubleYellow) {
                Expulsion expulsion = new Expulsion(minute, player, isDoubleYellow);
                match.addIncident(expulsion);

                if (player!=null && player.getPosition() == Position.GOALKEEPER) {
                    isGkAlreadyExpelled = true;
                    handleGoalkeeperExpulsion(match, activePlayers, benchPlayers, minute);
                }

                // Retiramos al jugador de la cancha y actualizamos su participación
                activePlayers.remove(player);
                PlayerParticipation pp = match.getParticipationFor(player);
                if (pp != null) {
                    pp.setMinuteOut(minute);
                }
            }
        }

        return isGkAlreadyExpelled;
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

            weight = switch (p.getPosition()) {
                case FORWARD -> 60.0 + finishing;
                case MIDFIELDER -> 20.0 + (finishing * 0.5);
                case DEFENDER -> 3.0 + (finishing * 0.1);
                case GOALKEEPER -> 0.0001;
            };

            weights.put(p, weight);
            totalWeight += weight;
        }

        if (totalWeight <= 0) return activePlayers.getFirst(); //TODO exception?

        // 2. Ruleta / Sorteo Ponderado
        double randomValue = random.nextDouble() * totalWeight; // [0.0, totalWeight)
        double cumulative = 0.0;

        for (Player p : activePlayers) {
            cumulative += weights.getOrDefault(p, 0.0);
            if (randomValue <= cumulative) {
                return p;
            }
        }

        return activePlayers.getFirst(); //TODO exception?
    }

    private static Player selectWeightedFouler(List<Player> activePlayers, Map<Player, Integer> yellowCardsMap, boolean isGkAlreadyExpelled, Random random) {
        if (activePlayers.isEmpty()) return null;

        double totalWeight = 0.0;
        Map<Player, Double> weights = new HashMap<>();

        for (Player p : activePlayers) {
            double weight;

            switch (p.getPosition()) {
                case DEFENDER -> weight = 50.0;
                case MIDFIELDER -> weight = 35.0;
                case FORWARD -> weight = 12.0;
                case GOALKEEPER -> {
                    // Si YA expulsaron al arquero, el suplente no hace faltas
                    if (isGkAlreadyExpelled) {
                        weight = 0.0;
                    } else {
                        weight = 3.0; // peso habitual
                    }
                }
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

        return activePlayers.getLast();
    }

    private static void registerGoalIncident(Match match , List<Player> activeScoringTeam, List<Player> activeDefendingTeam, Player opposingGoalkeeper, int minute, Random random) {
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

        double attackSum = 0;
        double weightSum = 0;

        for (Player p : activePlayers) {
            double rating = p.getRating();
            double weight = switch (p.getPosition()) {
                case FORWARD -> 1.0;
                case MIDFIELDER -> 0.6;
                case DEFENDER -> 0.15;
                case GOALKEEPER -> 0.0;
            };

            attackSum += rating * weight;
            weightSum += weight; // Acumulamos los pesos aplicados
        }

        // Si no hay jugadores de ataque en cancha, evitamos dividir por cero
        double avgPower = (weightSum > 0) ? (attackSum / weightSum) : 50.0;
        double basePower = avgPower + calculateTeamBonus(team);

        // Factor por expulsiones (menos jugadores en cancha = menos poder)
        double numericalFactor = activePlayers.size() / 11.0;

        return basePower * numericalFactor;
    }

    private static double calculateDefensePower(Team team, List<Player> activePlayers) {
        if (activePlayers.isEmpty()) return 50.0;

        double defenseSum = 0;
        double weightSum = 0;

        for (Player p : activePlayers) {
            double rating = p.getRating();
            double weight = switch (p.getPosition()) {
                case GOALKEEPER -> 1.2; // El arquero tiene un gran impacto defensivo
                case DEFENDER -> 1.0;   // 100% de aporte
                case MIDFIELDER -> 0.5; // 50% de aporte (marca en el medio)
                case FORWARD -> 0.05;   // 5% de aporte (presión alta)
            };

            defenseSum += rating * weight;
            weightSum += weight; // Acumulamos los pesos aplicados
        }

        // Si no hay jugadores de defensa en cancha, evitamos dividir por cero
        double avgPower = (weightSum > 0) ? (defenseSum / weightSum) : 50.0;
        double basePower = avgPower + calculateTeamBonus(team);

        // Factor por expulsiones (menos jugadores en cancha = menos poder general)
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
        return activePlayers.isEmpty() ? null : activePlayers.getFirst();
    }

    private static Player findGoalkeeper(List<Player> players) {
        for (Player p : players) {
            if (p.getPosition() == Position.GOALKEEPER) return p;
        }
        return players.getFirst();
    }

    private static void handleGoalkeeperExpulsion(Match match, List<Player> activePlayers,
                                                  List<Player> benchPlayers, int minute) {
        // 1. Buscamos un arquero suplente en el banco
        Player benchGK = findGoalkeeper(benchPlayers);

        if (benchGK != null) {
            // sacamos a un fieldplayer
            Player fieldPlayerToSacrifice = getLowestRatingPlayer(activePlayers);

            if (fieldPlayerToSacrifice != null) {
                // A. Retiramos de la cancha al jugador de campo
                activePlayers.remove(fieldPlayerToSacrifice);
                PlayerParticipation ppSacrifice = match.getParticipationFor(fieldPlayerToSacrifice);
                if (ppSacrifice != null) {
                    ppSacrifice.setMinuteOut(minute);
                }

                // B. Ingresamos al arquero suplente
                activePlayers.add(benchGK);
                benchPlayers.remove(benchGK);
                PlayerParticipation ppGK = match.getParticipationFor(benchGK);
                if (ppGK != null) {
                    ppGK.setMinuteIn(minute);
                }

                // C. Registramos la incidencia de sustitución
                Substitution sub = new Substitution(minute, fieldPlayerToSacrifice, benchGK);
                match.addIncident(sub);

                System.out.println("Min " + minute + "' | 🔄 CAMBIO OBLIGADO: Sale "
                        + fieldPlayerToSacrifice.getName() + " ➔ Entra el arquero suplente " + benchGK.getName());
            }
        }
    }
}
