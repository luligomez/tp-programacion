package service;

import model.team.Team;
import model.tournament.zone.Zone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DrawService {

    public static List<List<Team>> getPots(List<Team> teams) {
        List<Team> sortedTeams = new ArrayList<>(teams);
        sortedTeams.sort(Comparator.comparingInt(Team::getRankingPosition));

        List<List<Team>> pots = new ArrayList<>();
        pots.add(new ArrayList<>(sortedTeams.subList(0, 4)));
        pots.add(new ArrayList<>(sortedTeams.subList(4, 8)));
        pots.add(new ArrayList<>(sortedTeams.subList(8, 12)));
        pots.add(new ArrayList<>(sortedTeams.subList(12, 16)));
        return pots;
    }

    // Ahora recibe los equipos y devuelve la lista de zonas armadas
    public static ArrayList<Zone> zoneDraw(List<Team> teams) {
        ArrayList<Zone> newZones = new ArrayList<>();

        // Pasamos los equipos al méto do getPots
        List<List<Team>> pots = getPots(teams);

        // Mezclamos cada bombo
        for (List<Team> pot : pots) {
            Collections.shuffle(pot);
        }

        // Repartimos un equipo de cada bombo a cada zona
        for (int i = 0; i < 4; i++) {
            Zone currentZone = new Zone();
            currentZone.addTeam(pots.get(0).get(i));
            currentZone.addTeam(pots.get(1).get(i));
            currentZone.addTeam(pots.get(2).get(i));
            currentZone.addTeam(pots.get(3).get(i));

            newZones.add(currentZone);
        }

        // Retornamos las zonas ya sorteadas
        return newZones;
    }
}