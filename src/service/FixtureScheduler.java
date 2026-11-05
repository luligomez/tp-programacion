package service;

import model.match.Match;
import model.match.knockout.KnockoutTie;
import model.tournament.zone.Zone;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class FixtureScheduler {

    // Fixed time slots for the 4 matches of each day
    private static final LocalTime[] DAILY_TIME_SLOTS = {
            LocalTime.of(14, 0),  // 1st match: 14:00
            LocalTime.of(16, 30), // 2nd match: 16:30
            LocalTime.of(19, 0),  // 3rd match: 19:00
            LocalTime.of(21, 30)  // 4th match: 21:30
    };

    public static void scheduleZoneStage(List<Zone> Zones, LocalDate tournamentStartDate, int totalMatchdays) {
        LocalDate currentDay = tournamentStartDate;

        for (int matchday = 1; matchday <= totalMatchdays; matchday++) {
            LocalDate matchdayStartDay = currentDay;

            // Process the groups in pairs (Day 1: Groups A and B, Day 2: Groups C and D...)
            for (int i = 0; i < Zones.size(); i += 2) {
                Zone zone1 = Zones.get(i);
                Zone zone2 = (i + 1 < Zones.size()) ? Zones.get(i + 1) : null;

                // Get the matches of this matchday for both groups
                List<Match> matchesToday = zone1.getMatchesForMatchday(matchday);
                if (zone2 != null) {
                    matchesToday.addAll(zone2.getMatchesForMatchday(matchday));
                }

                // Assign the 4 time slots of the current day
                for (int slot = 0; slot < matchesToday.size() && slot < DAILY_TIME_SLOTS.length; slot++) {
                    Match match = matchesToday.get(slot);
                    LocalDateTime matchDateTime = LocalDateTime.of(currentDay, DAILY_TIME_SLOTS[slot]);
                    match.setDateTime(matchDateTime);
                }

                // Advance 1 day for the next 2 groups (C and D)
                currentDay = currentDay.plusDays(1);
            }

            // Matchday 2 (or 3) starts 3 days after the start of the previous matchday
            currentDay = matchdayStartDay.plusDays(3);
        }
    }
    private static final LocalTime[] KNOCKOUT_TIME_SLOTS = {
            LocalTime.of(17, 0), // 1st match of the day
            LocalTime.of(20, 0)  // 2nd match of the day
    };
    
    public static void scheduleQuarterFinals(List<KnockoutTie> quarterTies, LocalDate startDate) {
        LocalDate currentDay = startDate.plusDays(3);

        // --- FIRST LEGS ---
        // Ties 0 and 1 are played on Day 1 (17:00 and 20:00)
        quarterTies.get(0).getFirstLeg().setDateTime(LocalDateTime.of(currentDay, KNOCKOUT_TIME_SLOTS[0]));
        quarterTies.get(1).getFirstLeg().setDateTime(LocalDateTime.of(currentDay, KNOCKOUT_TIME_SLOTS[1]));

        // Ties 2 and 3 are played on Day 2
        currentDay = currentDay.plusDays(1);
        quarterTies.get(2).getFirstLeg().setDateTime(LocalDateTime.of(currentDay, KNOCKOUT_TIME_SLOTS[0]));
        quarterTies.get(3).getFirstLeg().setDateTime(LocalDateTime.of(currentDay, KNOCKOUT_TIME_SLOTS[1]));

        // --- REST (4 DAYS) ---
        currentDay = currentDay.plusDays(4);

        // --- SECOND LEGS ---
        // Ties 0 and 1 on Day 1 of the second legs
        quarterTies.get(0).getSecondLeg().setDateTime(LocalDateTime.of(currentDay, KNOCKOUT_TIME_SLOTS[0]));
        quarterTies.get(1).getSecondLeg().setDateTime(LocalDateTime.of(currentDay, KNOCKOUT_TIME_SLOTS[1]));

        // Ties 2 and 3 on Day 2 of the second legs
        currentDay = currentDay.plusDays(1);
        quarterTies.get(2).getSecondLeg().setDateTime(LocalDateTime.of(currentDay, KNOCKOUT_TIME_SLOTS[0]));
        quarterTies.get(3).getSecondLeg().setDateTime(LocalDateTime.of(currentDay, KNOCKOUT_TIME_SLOTS[1]));

    }

    public static void scheduleSemiFinals(List<KnockoutTie> semiTies, LocalDate startDate) {
        LocalDate currentDay = startDate.plusDays(3);

        // --- FIRST LEG (1 match per day at 20:00) ---
        semiTies.get(0).getFirstLeg().setDateTime(LocalDateTime.of(currentDay, KNOCKOUT_TIME_SLOTS[1]));

        currentDay = currentDay.plusDays(1);
        semiTies.get(1).getFirstLeg().setDateTime(LocalDateTime.of(currentDay, KNOCKOUT_TIME_SLOTS[1]));

        // --- REST (4 DAYS) ---
        currentDay = currentDay.plusDays(4);

        // --- SECOND LEG ---
        semiTies.get(0).getSecondLeg().setDateTime(LocalDateTime.of(currentDay, KNOCKOUT_TIME_SLOTS[1]));

        currentDay = currentDay.plusDays(1);
        semiTies.get(1).getSecondLeg().setDateTime(LocalDateTime.of(currentDay, KNOCKOUT_TIME_SLOTS[1]));
    }
    
    public static void scheduleFinal(Match finalMatch, LocalDate startDate) {
        LocalDate finalDate = startDate.plusDays(5);
        LocalTime finalTime = LocalTime.of(19, 0); // 19:00

        finalMatch.setDateTime(LocalDateTime.of(finalDate, finalTime));
    }

}