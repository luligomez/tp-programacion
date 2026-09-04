package model;

import model.match.GroupStageMatch;
import model.match.Match;
import model.person.Referee;
import model.place.Stadium;
import model.zone.Zone;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public class Tournament implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final int GROUPS = 4;
    private static final int TEAMS = 16;
    public static final int TEAMS_PER_GROUP = 4;
    private static final int QUALIFIED_PER_GROUP = 2;
    private ArrayList<Team> teams = new ArrayList<>();
    private ArrayList<Zone> zones = new ArrayList<>();
    private ArrayList<Match> matches = new ArrayList<>();
    private ArrayList<Referee> referees = new ArrayList<>();
    private transient ArrayList<Stadium> stadiums = new ArrayList<>(); //no se guardan los estadios, se consultan nuevamente de la bbdd


    public Tournament(){}

    public ArrayList<Team> getTeams() {
        return teams;
    }

    public ArrayList<Zone> getZones() {
        return zones;
    }

    public ArrayList<Match> getMatches() {
        return matches;
    }

    public ArrayList<Referee> getReferees() {
        return referees;
    }

    public ArrayList<Stadium> getStadiums() {
        return stadiums;
    }

    public void addZone(Zone zone) {
        if (zones.size() < GROUPS) {
            zones.add(zone);
        }
    }

    public void addTeam(Team team) {
        if (teams.size() < TEAMS) {
            teams.add(team);
        }
    }

    public void addMatch(Match match) {
        matches.add(match);
    }

    public void addReferee(Referee referee) {
        referees.add(referee);
    }

    public void addStadium(Stadium stadium){
        stadiums.add(stadium);
    }

    public void zoneDraw(){
        this.zones.clear();
        // ordenamos equipos por ranking
        this.teams.sort(Comparator.comparingInt(Team::getRankingPosition));

        // creamos los 4 bombos
        List<Team> pot1 = new ArrayList<>(this.teams.subList(0, 4));
        List<Team> pot2 = new ArrayList<>(this.teams.subList(4, 8));
        List<Team> pot3 = new ArrayList<>(this.teams.subList(8, 12));
        List<Team> pot4 = new ArrayList<>(this.teams.subList(12, 16));

        // mezclamos cada bombo
        Collections.shuffle(pot1);
        Collections.shuffle(pot2);
        Collections.shuffle(pot3);
        Collections.shuffle(pot4);

        // 5. Repartimos un equipo de cada bombo a cada zona
        for (int i = 0; i < 4; i++) {
            this.zones.add(new Zone());
            Zone zonaActual = this.zones.get(i);
            zonaActual.addTeam(pot1.get(i));
            zonaActual.addTeam(pot2.get(i));
            zonaActual.addTeam(pot3.get(i));
            zonaActual.addTeam(pot4.get(i));
        }
    }

    public void generateGroupStageMatches() {
        for (Zone zone : zones) {
            zone.generateMatches(referees, stadiums);
        }
    }

    public void syncUsedStadiums() { //actualizamos el booleano used al abrir el programa, luego de cargar los estadios desde la bbdd.
        this.matches.stream()
                // Filtramos los partidos que NO son de fase de grupos
                .filter(match -> !(match instanceof GroupStageMatch))

                // transformamos el flujo de "Partidos" a un flujo de "Estadios"
                .map(Match::getStadium)

                // descartamos los nulos
                .filter(Objects::nonNull)

                // 4. Por cada estadio usado encontrado, lo buscamos en la lista viva
                .forEach(usedStadium -> {
                    this.stadiums.stream()
                            .filter(stadium -> stadium.getName().equals(usedStadium.getName()))
                            .findFirst()
                            .ifPresent(stadium -> stadium.setUsed(true));
                });
    }


}
