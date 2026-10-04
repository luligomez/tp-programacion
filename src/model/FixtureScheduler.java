package model;

import model.match.Match;
import model.match.knockout.KnockoutTie;
import model.zone.Zone;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class FixtureScheduler {

    // Franjas horarias fijas para los 4 partidos de cada día
    private static final LocalTime[] DAILY_TIME_SLOTS = {
            LocalTime.of(14, 0),  // 1er partido: 14:00 hs
            LocalTime.of(16, 30), // 2do partido: 16:30 hs
            LocalTime.of(19, 0),  // 3er partido: 19:00 hs
            LocalTime.of(21, 30)  // 4to partido: 21:30 hs
    };

    public static void scheduleZoneStage(List<Zone> Zones, LocalDate tournamentStartDate) {
        LocalDate currentDay = tournamentStartDate;

        // Suponiendo 3 Fechas/Jornadas en fase de grupos (3 partidos por equipo)
        int totalMatchdays = 3;

        for (int matchday = 1; matchday <= totalMatchdays; matchday++) {
            LocalDate matchdayStartDay = currentDay;

            // Procesamos los grupos de a pares (Día 1: Grupos A y B, Día 2: Grupos C y D...)
            for (int i = 0; i < Zones.size(); i += 2) {
                Zone Zone1 = Zones.get(i);
                Zone Zone2 = (i + 1 < Zones.size()) ? Zones.get(i + 1) : null;

                // Obtenemos los partidos correspondientes a esta Fecha/Jornada para ambos grupos
                List<Match> matchesToday = Zone1.getMatchesForMatchday(matchday);
                if (Zone2 != null) {
                    matchesToday.addAll(Zone2.getMatchesForMatchday(matchday));
                }

                // Asignamos los 4 slots de hora del día actual
                for (int slot = 0; slot < matchesToday.size() && slot < DAILY_TIME_SLOTS.length; slot++) {
                    Match match = matchesToday.get(slot);
                    LocalDateTime matchDateTime = LocalDateTime.of(currentDay, DAILY_TIME_SLOTS[slot]);
                    match.setDateTime(matchDateTime);
                }

                // Avanzamos 1 día para los siguientes 2 grupos (C y D)
                currentDay = currentDay.plusDays(1);
            }

            // La Fecha 2 (o Fecha 3) arranca 3 días después del inicio de la fecha anterior
            currentDay = matchdayStartDay.plusDays(3);
        }
    }
    private static final LocalTime[] KNOCKOUT_TIME_SLOTS = {
            LocalTime.of(17, 0), // 1er partido del día
            LocalTime.of(20, 0)  // 2do partido del día
    };
    
    public static void scheduleQuarterFinals(List<KnockoutTie> quarterTies, LocalDate startDate) {
        LocalDate currentDay = startDate;

        // --- PARTIDOS DE IDA ---
        // Serie 0 y 1 van el Día 1 (17:00 y 20:00 hs)
        quarterTies.get(0).getFirstLeg().setDateTime(LocalDateTime.of(currentDay, KNOCKOUT_TIME_SLOTS[0]));
        quarterTies.get(1).getFirstLeg().setDateTime(LocalDateTime.of(currentDay, KNOCKOUT_TIME_SLOTS[1]));

        // Serie 2 y 3 van el Día 2
        currentDay = currentDay.plusDays(1);
        quarterTies.get(2).getFirstLeg().setDateTime(LocalDateTime.of(currentDay, KNOCKOUT_TIME_SLOTS[0]));
        quarterTies.get(3).getFirstLeg().setDateTime(LocalDateTime.of(currentDay, KNOCKOUT_TIME_SLOTS[1]));

        // --- DESCANSO (4 DÍAS) ---
        currentDay = currentDay.plusDays(4);

        // --- PARTIDOS DE VUELTA ---
        // Serie 0 y 1 en el Día 1 de Vuelta
        quarterTies.get(0).getSecondLeg().setDateTime(LocalDateTime.of(currentDay, KNOCKOUT_TIME_SLOTS[0]));
        quarterTies.get(1).getSecondLeg().setDateTime(LocalDateTime.of(currentDay, KNOCKOUT_TIME_SLOTS[1]));

        // Serie 2 y 3 en el Día 2 de Vuelta
        currentDay = currentDay.plusDays(1);
        quarterTies.get(2).getSecondLeg().setDateTime(LocalDateTime.of(currentDay, KNOCKOUT_TIME_SLOTS[0]));
        quarterTies.get(3).getSecondLeg().setDateTime(LocalDateTime.of(currentDay, KNOCKOUT_TIME_SLOTS[1]));

    }

    public static void scheduleSemiFinals(List<KnockoutTie> semiTies, LocalDate startDate) {
        LocalDate currentDay = startDate.plusDays(3);

        // --- IDA (1 partido por día a las 20:00 hs) ---
        semiTies.get(0).getFirstLeg().setDateTime(LocalDateTime.of(currentDay, KNOCKOUT_TIME_SLOTS[1]));

        currentDay = currentDay.plusDays(1);
        semiTies.get(1).getFirstLeg().setDateTime(LocalDateTime.of(currentDay, KNOCKOUT_TIME_SLOTS[1]));

        // --- DESCANSO (4 DÍAS) ---
        currentDay = currentDay.plusDays(4);

        // --- VUELTA ---
        semiTies.get(0).getSecondLeg().setDateTime(LocalDateTime.of(currentDay, KNOCKOUT_TIME_SLOTS[1]));

        currentDay = currentDay.plusDays(1);
        semiTies.get(1).getSecondLeg().setDateTime(LocalDateTime.of(currentDay, KNOCKOUT_TIME_SLOTS[1]));
    }
    
    public static void scheduleFinal(Match finalMatch, LocalDate startDate) {
        LocalDate finalDate = startDate.plusDays(5);
        LocalTime finalTime = LocalTime.of(19, 0); // 19:00 hs

        finalMatch.setDateTime(LocalDateTime.of(finalDate, finalTime));
    }

}