package service;

import model.team.Team;
import model.match.Formation;
import model.match.Match;
import model.match.PlayerParticipation;
import model.match.incident.*;
import model.match.knockout.*;
import model.person.Position;
import model.person.player.Player;

import java.util.*;

public class MatchSimulator {

    public static void simulatePenaltyShootout(KnockoutMatch knockoutMatch) {

        knockoutMatch.setPenalties(new PenaltyShootout(knockoutMatch.getTeam1(), knockoutMatch.getTeam2()));
        Team team1 = knockoutMatch.getTeam1();
        Team team2 = knockoutMatch.getTeam2();

        List<Player> shootersTeam1 = selectPenaltyOrder(team1, knockoutMatch);
        List<Player> shootersTeam2 = selectPenaltyOrder(team2, knockoutMatch);
        int eligible = Math.min(shootersTeam1.size(), shootersTeam2.size());
        shootersTeam1 = shootersTeam1.subList(0, eligible);
        shootersTeam2 = shootersTeam2.subList(0, eligible);

        Random random = new Random();
        int scoredTeam1 = 0, scoredTeam2 = 0;
        int round = 0;
        boolean winnerDecided = false;

        // Initial shootout of up to 5 penalties per team
        while (round < 5 && !winnerDecided) {
            // 1. Team 1 kicks
            boolean scored1 = simulatePenaltyKick(team1, shootersTeam1.get(round), knockoutMatch, random);
            if (scored1) {
                scoredTeam1++;
            }

            int remainingTeam1 = 4 - round; // Remaining kicks for Team 1
            int remainingTeam2 = 5 - round; // Remaining kicks for Team 2

            // Check whether the difference is mathematically unreachable after Team 1's kick
            if (scoredTeam1 > scoredTeam2 + remainingTeam2 || scoredTeam2 > scoredTeam1 + remainingTeam1) {
                winnerDecided = true;
            } else {
                // 2. Team 2 kicks only if the shootout was not decided on the previous kick
                boolean scored2 = simulatePenaltyKick(team2, shootersTeam2.get(round), knockoutMatch, random);
                if (scored2) {
                    scoredTeam2++;
                }

                remainingTeam2 = 4 - round; // Team 2 now also has (4 - round) kicks left

                // Check whether the difference is unreachable after Team 2's kick
                if (scoredTeam1 > scoredTeam2 + remainingTeam2 || scoredTeam2 > scoredTeam1 + remainingTeam1) {
                    winnerDecided = true;
                }
            }

            round++;
        }

        // Sudden death only if the 5-kick shootout ended in a tie
        while (scoredTeam1 == scoredTeam2) {
            Player shooter1 = shootersTeam1.get(round % shootersTeam1.size());
            Player shooter2 = shootersTeam2.get(round % shootersTeam2.size());

            if (simulatePenaltyKick(team1, shooter1, knockoutMatch, random)) {
                scoredTeam1++;
            }
            if (simulatePenaltyKick(team2, shooter2, knockoutMatch, random)) {
                scoredTeam2++;
            }

            round++;
        }
    }

    private static boolean simulatePenaltyKick(Team team, Player shooter, KnockoutMatch match, Random random) {
        double conversionChance = shooter.getPenaltyConversionChance();
        boolean scored = random.nextDouble() < conversionChance;
        match.getPenalties().addKick(team, shooter, scored);
        return scored;
    }

    private static List<Player> selectPenaltyOrder(Team team, KnockoutMatch match) {
        return match.getPlayersOnPitchAtEnd(team).stream()
                .sorted(Comparator.comparing(Player::getPenaltyConversionChance).reversed())
                .toList();
    }


    public static void simulateFinal(FinalMatch match) {
        simulateMatch(match);
    }

    public static void simulateMatch(Match match) {

        // If it was already simulated, exit
        if (match.isPlayed()) {
            System.out.println("El partido entre " + match.getTeam1().getName() + //TODO agregar exception
                    " y " + match.getTeam2().getName() + " ya fue simulado.");
            return;
        }

        Team team1 = match.getTeam1();
        Team team2 = match.getTeam2();

        Formation form1 = match.getTeam1Formation();
        Formation form2 = match.getTeam2Formation();
        match.initializePlayerParticipation(form1, form2);

        Random random = new Random();
        boolean isGkAlreadyExpelled1 = false;
        boolean isGkAlreadyExpelled2 = false;

        // Players on the pitch (active)
        List<Player> activeTeam1 = new ArrayList<>(form1.getStarters());
        List<Player> activeTeam2 = new ArrayList<>(form2.getStarters());

        // Substitutes on the bench
        List<Player> benchTeam1 = new ArrayList<>(form1.getSubstitutes());
        List<Player> benchTeam2 = new ArrayList<>(form2.getSubstitutes());

        // Substitutes who came on
        Set<Player> cameOnTeam1 = new HashSet<>();
        Set<Player> cameOnTeam2 = new HashSet<>();

        Map<Player, Integer> yellowCardsMap = new HashMap<>();
        int subsTeam1 = 0, subsTeam2 = 0;
        int goalsTeam1 = 0, goalsTeam2 = 0;


        // MINUTE-BY-MINUTE LOOP
        for (int minute = 1; minute <= 90; minute+=1) {

            // A. Recalculate the power of the players on the pitch
            double attack1 = calculateAttackPower(team1, activeTeam1);
            double defense1 = calculateDefensePower(team1, activeTeam1);
            double attack2 = calculateAttackPower(team2, activeTeam2);
            double defense2 = calculateDefensePower(team2, activeTeam2);

            // B. Substitutions (minutes 45 to 85)
            if (minute >= 45 && minute <= 85) {
                if (subsTeam1 < 5 && random.nextDouble() < 0.08) {
                    if (trySubstitution(match, activeTeam1, benchTeam1, cameOnTeam1, goalsTeam1, goalsTeam2, minute, random)) {
                        subsTeam1++;
                    }
                }
                if (subsTeam2 < 5 && random.nextDouble() < 0.08) {
                    if (trySubstitution(match, activeTeam2, benchTeam2, cameOnTeam2, goalsTeam2, goalsTeam1, minute, random)) {
                        subsTeam2++;
                    }
                }
            }

            // C. Cards and sending-offs
            isGkAlreadyExpelled1=simulateCardsForMinute(match, activeTeam1, benchTeam1, minute, yellowCardsMap, isGkAlreadyExpelled1, random);
            isGkAlreadyExpelled2=simulateCardsForMinute(match, activeTeam2, benchTeam2, minute, yellowCardsMap, isGkAlreadyExpelled2, random);

            // D. Goal chances
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

    // SUBSTITUTION AND PARTICIPATION HANDLING
    private static boolean trySubstitution(Match match, List<Player> activePlayers, List<Player> bench,
    Set<Player> cameOn, int teamGoals, int opponentGoals,
    int minute, Random random) {

        List<Player> fieldBench = bench.stream()
                .filter(p -> p.getPosition() != Position.GOALKEEPER)
            .toList();
        List<Player> fieldActive = activePlayers.stream()
                .filter(p -> p.getPosition() != Position.GOALKEEPER && !cameOn.contains(p))
            .toList();

        if (fieldBench.isEmpty() || fieldActive.isEmpty()) return false;

        Player playerOut = null;
        Player playerIn = null;

        int scoreDiff = teamGoals - opponentGoals;

        // IF LOSING -> Needs to attack (bring on forward/midfielder, take off defender/midfielder)
        if (scoreDiff < 0) {
            playerIn = findRandomByPositions(fieldBench, Position.FORWARD, Position.MIDFIELDER, random);
            if (playerIn != null) {
                playerOut = findRandomByPositions(fieldActive, Position.DEFENDER, Position.MIDFIELDER, random);
            }
        }
        else if (scoreDiff > 0) { // IF WINNING -> Close out the match (bring on defender/midfielder, take off forward)
            playerIn = findRandomByPositions(fieldBench, Position.DEFENDER, Position.MIDFIELDER, random);
            if (playerIn != null) {
                playerOut = findRandomByPosition(fieldActive, Position.FORWARD, random);
            }
        }

        // ROUTINE CHANGE / POSITION BY POSITION (if tied or no earlier combination was found)
        if (playerOut == null || playerIn == null) {
            playerOut = getLowestRatingPlayer(fieldActive);
            if (playerOut != null) {
                playerIn = findRandomByPosition(fieldBench, playerOut.getPosition(), random);
            }
        }

        // TACTICAL RANGE VALIDATION:
        if (playerOut != null && playerIn != null && !isValidTacticalFormation(fieldActive, playerOut, playerIn)) {
            // Force a position-for-position change so the formation is not broken
            playerIn = findRandomByPosition(fieldBench, playerOut.getPosition(), random);
        }

        // If there is still no valid match, cancel the substitution this minute
        if (playerIn == null || playerOut == null || !isValidTacticalFormation(fieldActive, playerOut, playerIn)) {
            return false;
        }

        // --- Execute the substitution ---
        bench.remove(playerIn);
        activePlayers.remove(playerOut);
        activePlayers.add(playerIn);
        cameOn.add(playerIn);

        PlayerParticipation outPart = match.getParticipationFor(playerOut);
        if (outPart != null) outPart.setMinuteOut(minute);

        PlayerParticipation inPart = match.getParticipationFor(playerIn);
        if (inPart != null) {
            inPart.setMinuteIn(minute);
            inPart.setMinuteOut(90);
        }

        match.addIncident(new Substitution(minute, playerOut, playerIn));
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
                .filter(p -> p.getPosition() != Position.GOALKEEPER) // The goalkeeper is not substituted
                .min(Comparator.comparingDouble(Player::getRating))
                .orElse(null);
    }

    private static Player getHighestRatingPlayer(List<Player> bench) {
        return bench.stream()
                .max(Comparator.comparingDouble(Player::getRating))
                .orElse(null);
    }

    // SENDING-OFF AND PARTICIPATION HANDLING
    private static boolean simulateCardsForMinute(Match match, List<Player> activePlayers, List<Player> benchPlayers,
                                               int minute, Map<Player, Integer> yellowCardsMap, boolean isGkAlreadyExpelled, Random random) {
        if (activePlayers.isEmpty()) return false;

        // Count how many players the team already has sent off 
        long expulsionsCount = yellowCardsMap.keySet().stream() .filter(p -> !activePlayers.contains(p) && yellowCardsMap.get(p) >= 2) .count();

        // If the team already suffered 2 sending-offs, strongly reduce the chance of more red/yellow cards 
        double cardChance = 0.015; // 1.5% base chance per minute
        if (expulsionsCount >= 2) { 
            cardChance = 0.002; // Reduced to 0.2% to avoid unplayable matches
            }

        // Chance of a foul with a card this minute (e.g. 1.5%)
        if (random.nextDouble() < cardChance) {
            Player player = selectWeightedFouler(activePlayers, yellowCardsMap, isGkAlreadyExpelled, random);
            if (player == null) return isGkAlreadyExpelled;

            boolean isDirectRed = random.nextDouble() < 0.03;
            boolean isDoubleYellow = false;

            if (!isDirectRed) {
                // It is a yellow (1st or 2nd)
                int currentYellows = yellowCardsMap.getOrDefault(player, 0) + 1;
                yellowCardsMap.put(player, currentYellows);

                // Register the yellow card received in the play
                match.addIncident(new YellowCard(minute, player));

                if (currentYellows == 2) {
                    isDoubleYellow = true;
                }
            }

            // If it is a sending-off
            if (isDirectRed || isDoubleYellow) {
                match.addIncident(new Expulsion(minute, player, isDoubleYellow));

                if (player.getPosition() == Position.GOALKEEPER) {
                    isGkAlreadyExpelled = true;
                    handleGoalkeeperExpulsion(match, activePlayers, benchPlayers, minute);
                }

                // Remove the player from the pitch and update their participation
                activePlayers.remove(player);
                PlayerParticipation pp = match.getParticipationFor(player);
                if (pp != null) {
                    pp.setMinuteOut(minute);
                }
            }
        }

        return isGkAlreadyExpelled;
    }

    // HELPERS
    private static boolean checkGoalOccurred(double attack, double opposingDefense, Random random) {
        double advantage = attack - opposingDefense;
        double minuteChance = Math.max(0.002, Math.min(0.06, 0.018 + (advantage * 0.0008))); // 0.2% - 6% goal/min
        return random.nextDouble() < minuteChance;
    }

    private static Player selectWeightedScorer(List<Player> activePlayers, Random random) {
        if (activePlayers.isEmpty()) return null;

        double totalWeight = 0.0;
        Map<Player, Double> weights = new HashMap<>();

        // Calculate the weight of each player according to position and Finishing
        for (Player p : activePlayers) {
            double weight = 0.0;
            weight = p.getGoalScoringWeight();

            weights.put(p, weight);
            totalWeight += weight;
        }

        if (totalWeight <= 0) return activePlayers.getFirst(); //TODO exception?

        // 2. Roulette / weighted draw
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
                case DEFENDER -> weight = 40.0;
                case MIDFIELDER -> weight = 35.0;
                case FORWARD -> weight = 20.0;
                // If the goalkeeper was ALREADY sent off, the substitute commits no fouls
                case GOALKEEPER -> weight = isGkAlreadyExpelled ? 0.0 : 2.0;
                default -> weight = 10.0;
            }

            // CAUTION REDUCTION: if the player already has 1 yellow, they play more carefully (weight reduced by 75%)
            if (yellowCardsMap.getOrDefault(p, 0) == 1) {
                weight *= 0.25;
            }

            weights.put(p, weight);
            totalWeight += weight;
        }

        // Roulette / weighted draw
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
            // The author is chosen among the DEFENDERS or GOALKEEPER of the opposing team
            scorer = selectOwnGoalScorer(activeDefendingTeam, random);
        } else {
            scorer = selectWeightedScorer(activeScoringTeam, random);
        }

        if (scorer == null) return;

        if (isPenalty) {
            PenaltyTaken penaltyTaken = new PenaltyTaken(minute, scorer, true);
            match.addIncident(penaltyTaken);
        }

        // The incident is registered with isOwnGoal = true
        Goal goal = new Goal(minute, scorer, isPenalty, isOwnGoal, opposingGoalkeeper);
        match.addIncident(goal);
    }

    private static Player selectOwnGoalScorer(List<Player> defendingPlayers, Random random) {
        if (defendingPlayers.isEmpty()) return null;

        // Filter defenders and goalkeepers
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
            weightSum += weight; // Accumulate the applied weights
        }

        // If there are no attackers on the pitch, avoid dividing by zero
        double avgPower = (weightSum > 0) ? (attackSum / weightSum) : 50.0;
        double basePower = avgPower + calculateTeamBonus(team);

        // Sending-off factor (fewer players on the pitch = less power)
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
                case GOALKEEPER -> 1.2; // The goalkeeper has a large defensive impact
                case DEFENDER -> 1.0;   // 100% contribution
                case MIDFIELDER -> 0.5; // 50% contribution (marks in midfield)
                case FORWARD -> 0.05;   // 5% contribution (high press)
            };

            defenseSum += rating * weight;
            weightSum += weight; // Accumulate the applied weights
        }

        // If there are no defenders on the pitch, avoid dividing by zero
        double avgPower = (weightSum > 0) ? (defenseSum / weightSum) : 50.0;
        double basePower = avgPower + calculateTeamBonus(team);

        // Sending-off factor (fewer players on the pitch = less overall power)
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
        // 1. Look for a substitute goalkeeper on the bench
        Player benchGK = findGoalkeeper(benchPlayers);

        if (benchGK != null) {
            // take off a field player
            Player fieldPlayerToSacrifice = getLowestRatingPlayer(activePlayers);

            if (fieldPlayerToSacrifice != null) {
                // A. Remove the field player from the pitch
                activePlayers.remove(fieldPlayerToSacrifice);
                PlayerParticipation ppSacrifice = match.getParticipationFor(fieldPlayerToSacrifice);
                if (ppSacrifice != null) {
                    ppSacrifice.setMinuteOut(minute);
                }

                // B. Bring on the substitute goalkeeper
                activePlayers.add(benchGK);
                benchPlayers.remove(benchGK);
                PlayerParticipation ppGK = match.getParticipationFor(benchGK);
                if (ppGK != null) {
                    ppGK.setMinuteIn(minute);
                }

                // C. Register the substitution incident
                Substitution sub = new Substitution(minute, fieldPlayerToSacrifice, benchGK);
                match.addIncident(sub);
            }
        }
    }
    
    private static boolean isValidTacticalFormation(List<Player> activePlayers, Player playerOut, Player playerIn) {
        // If the incoming and outgoing players share a position, the tactical scheme does not change
        if (playerOut.getPosition() == playerIn.getPosition()) {
            return true;
        }

        // 1. Count how many players there currently are in each position
        long def = activePlayers.stream().filter(p -> p.getPosition() == Position.DEFENDER).count();
        long mid = activePlayers.stream().filter(p -> p.getPosition() == Position.MIDFIELDER).count();
        long fwd = activePlayers.stream().filter(p -> p.getPosition() == Position.FORWARD).count();

        // 2. Subtract the outgoing player
        switch (playerOut.getPosition()) {
            case DEFENDER -> def--;
            case MIDFIELDER -> mid--;
            case FORWARD -> fwd--;
        }

        // 3. Add the incoming player
        switch (playerIn.getPosition()) {
            case DEFENDER -> def++;
            case MIDFIELDER -> mid++;
            case FORWARD -> fwd++;
        }

        // 4. Validate allowed tactical ranges (3 to 5 DEF, 3 to 6 MID, 1 to 4 FWD)
        boolean defValid = def >= 3 && def <= 5;
        boolean midValid = mid >= 3 && mid <= 6;
        boolean fwdValid = fwd >= 1 && fwd <= 4;

        return defValid && midValid && fwdValid;
    }

}
