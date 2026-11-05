package service;
import model.team.Team;
import model.tournament.Tournament;
import model.person.*;
import model.person.player.*;
import model.person.player.FieldPlayer.FieldPlayer;
import model.person.player.FieldPlayer.FieldPlayerAttributes;
import model.person.player.FieldPlayer.FieldPlayerCareerStats;
import model.person.player.Goalkeeper.Goalkeeper;
import model.person.player.Goalkeeper.GoalkeeperAttributes;
import model.person.player.Goalkeeper.GoalkeeperCareerStats;
import model.place.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.parser.ParseException;
import java.io.IOException;

public class FileReader {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private record PersonData(String name, LocalDate birthDate, String documentType, String documentNumber) {}

    public static Tournament fileReader(String file) throws IOException, ParseException {
        Tournament tournament = new Tournament();
        Map<String, Country> countries = new HashMap<>();

        JSONObject tournamentJson = readTournamentJson(file);

        JSONObject teamsObj = (JSONObject) tournamentJson.get("equipos");
        for (Object team : (JSONArray) teamsObj.get("equipo")) {
            tournament.addTeam(readTeam((JSONObject) team, countries));
        }

        JSONObject refereesObj = (JSONObject) tournamentJson.get("arbitros");
        for (Object referee : (JSONArray) refereesObj.get("arbitro")) {
            tournament.addReferee(readReferee((JSONObject) referee, countries));
        }
        return tournament;
    }

    private static JSONObject readTournamentJson(String file) throws IOException, ParseException {
        try (java.io.FileReader reader = new java.io.FileReader(file)) {
            JSONObject root = (JSONObject) new JSONParser().parse(reader);
            return (JSONObject) root.get("torneo");
        }
    }

    // ---------- teams ----------

    private static Team readTeam(JSONObject teamJson, Map<String, Country> countries) {
        String name = (String) teamJson.get("nombre");
        Country country = countryNamed((String) teamJson.get("pais"), countries);
        int rankingPosition = readInt(teamJson, "ranking");

        JSONObject squad = (JSONObject) teamJson.get("plantel");
        JSONObject playersObj = (JSONObject) squad.get("jugadores");

        List<Player> players = new ArrayList<>();
        for (Object player : (JSONArray) playersObj.get("jugador")) {
            players.add(readPlayer((JSONObject) player));
        }

        Coach coach = readCoach((JSONObject) squad.get("dt"), countries);

        // The Team constructor validates the squad (18 players and its composition)
        return new Team(name, country, rankingPosition, players, coach);
    }

    private static Coach readCoach(JSONObject coachJson, Map<String, Country> countries) {
        PersonData person = readPerson(coachJson);
        Country country = countryNamed((String) coachJson.get("pais"), countries);
        int titles = readInt(coachJson, "titulosObtenidos");
        return new Coach(person.name(), person.birthDate(), person.documentType(),
                person.documentNumber(), country, titles);
    }

    // ---------- players ----------

    private static Player readPlayer(JSONObject playerJson) {
        Position position = mapPosition((String) playerJson.get("posicion"));
        PersonData person = readPerson(playerJson);
        JSONObject attributes = (JSONObject) playerJson.get("caracteristicas");
        JSONObject stats = (JSONObject) playerJson.get("estadisticas");

        if (position == Position.GOALKEEPER) {
            return readGoalkeeper(person, position, attributes, stats);
        }
        return readFieldPlayer(person, position, attributes, stats);
    }

    private static Goalkeeper readGoalkeeper(PersonData person, Position position,
                                             JSONObject attributes, JSONObject stats) {
        GoalkeeperAttributes goalkeeperAttributes = new GoalkeeperAttributes(
                readInt(attributes, "reflejos"),
                readInt(attributes, "juegoAereo"),
                readInt(attributes, "ubicacion"),
                readInt(attributes, "achique"),
                readInt(attributes, "seguridadManos"),
                readInt(attributes, "juegoPies"));
        GoalkeeperCareerStats careerStats = new GoalkeeperCareerStats(
                readInt(stats, "partidosJugados"),
                readInt(stats, "expulsiones"),
                readInt(stats, "golesRecibidos"),
                readInt(stats, "penalesRecibidos"),
                readInt(stats, "penalesAtajados"));
        return new Goalkeeper(person.name(), person.birthDate(), person.documentType(), person.documentNumber(),
                position, goalkeeperAttributes, careerStats);
    }

    private static FieldPlayer readFieldPlayer(PersonData person, Position position,
                                               JSONObject attributes, JSONObject stats) {
        FieldPlayerAttributes playerAttributes = new FieldPlayerAttributes(
                readInt(attributes, "capacidadQuite"),
                readInt(attributes, "velocidad"),
                readInt(attributes, "habilidad"),
                readInt(attributes, "cabezazo"),
                readInt(attributes, "definicion"),
                readInt(attributes, "potenciaDisparo"),
                readInt(attributes, "visionDeJuego"),
                readInt(attributes, "resistenciaFisica"));
        FieldPlayerCareerStats careerStats = new FieldPlayerCareerStats(
                readInt(stats, "partidosJugados"),
                readInt(stats, "expulsiones"),
                readInt(stats, "goles"),
                readInt(stats, "penales"),
                readInt(stats, "penalesConvertidos"),
                readInt(stats, "pasesGol"));
        return new FieldPlayer(person.name(), person.birthDate(), person.documentType(), person.documentNumber(),
                position, playerAttributes, careerStats);
    }

    // ---------- referees ----------

    private static Referee readReferee(JSONObject refereeJson, Map<String, Country> countries) {
        PersonData person = readPerson(refereeJson);
        Country country = countryNamed((String) refereeJson.get("pais"), countries);
        int yearsAsReferee = readInt(refereeJson, "aniosReferato");
        return new Referee(person.name(), person.birthDate(), person.documentType(),
                person.documentNumber(), country, yearsAsReferee);
    }

    // ---------- shared helpers ----------

    private static PersonData readPerson(JSONObject parent) {
        JSONObject person = (JSONObject) parent.get("persona");
        return new PersonData(
                (String) person.get("nombre"),
                LocalDate.parse((String) person.get("fechaNacimiento"), DATE_FORMAT),
                (String) person.get("tipoDocumento"),
                person.get("nroDocumento").toString());
    }

    private static int readInt(JSONObject object, String key) {
        return ((Long) object.get(key)).intValue();
    }

    // One Country object per country name, shared by every team, coach and referee
    private static Country countryNamed(String name, Map<String, Country> countries) {
        return countries.computeIfAbsent(name, Country::new);
    }

    private static Position mapPosition(String positionStr) {
        return switch (positionStr.toLowerCase()) {
            case "arquero" -> Position.GOALKEEPER;
            case "defensor" -> Position.DEFENDER;
            case "mediocampista" -> Position.MIDFIELDER;
            case "delantero" -> Position.FORWARD;
            default -> throw new IllegalArgumentException("Invalid position: " + positionStr);
        };
    }
}