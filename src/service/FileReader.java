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

public class FileReader {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private record PersonData(String name, LocalDate birthDate, String documentType, String documentNumber) {}

    private static PersonData readPerson(JSONObject parent) {
        JSONObject person = (JSONObject) parent.get("persona");
        return new PersonData(
                (String) person.get("nombre"),
                LocalDate.parse((String) person.get("fechaNacimiento"), DATE_FORMAT),
                (String) person.get("tipoDocumento"),
                person.get("nroDocumento").toString());
    }

    public static Tournament fileReader(String file) throws Exception{
        Tournament tournament = new Tournament();
        Map<String, Country> countries = new HashMap<>();
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(new java.io.FileReader(file));
        JSONObject tournamentJ = (JSONObject) jsonObject.get("torneo");
        JSONObject teamsObj = (JSONObject) tournamentJ.get("equipos");
        JSONArray teams = (JSONArray)  teamsObj.get("equipo");

        for (Object team: teams){
            JSONObject e = (JSONObject) team;
            String nameT = (String) e.get("nombre");
            String countryName = (String) e.get("pais");
            Country country = countries.computeIfAbsent(
                    countryName, Country::new
            );
            int rankingPosition = ((Long) e.get("ranking")).intValue();

            JSONObject teamP = (JSONObject) e.get("plantel");
            JSONObject playersObj = (JSONObject) teamP.get("jugadores");
            JSONArray players = (JSONArray) playersObj.get("jugador");

            List<Player> playerList = new ArrayList<>();
            for (Object p : players) {
                JSONObject j = (JSONObject) p;
                String position = (String) j.get("posicion");
                Position positionEnum = mapPosition(position);
                PersonData person = readPerson(j);
                JSONObject attributes = (JSONObject) j.get("caracteristicas");
                JSONObject stats = (JSONObject) j.get("estadisticas");

                Player player;
                int matchesPlayed = ((Long) stats.get("partidosJugados")).intValue();
                int expulsions = ((Long) stats.get("expulsiones")).intValue();

                if (positionEnum == Position.GOALKEEPER) {
                    int reflexes = ((Long) attributes.get("reflejos")).intValue();
                    int aerialGame = ((Long) attributes.get("juegoAereo")).intValue();
                    int positioning = ((Long) attributes.get("ubicacion")).intValue();
                    int rushingOut = ((Long) attributes.get("achique")).intValue();
                    int handling = ((Long) attributes.get("seguridadManos")).intValue();
                    int footwork = ((Long) attributes.get("juegoPies")).intValue();

                    int goalsReceived = ((Long) stats.get("golesRecibidos")).intValue();
                    int penaltiesReceived = ((Long) stats.get("penalesRecibidos")).intValue();
                    int penaltiesSaved = ((Long) stats.get("penalesAtajados")).intValue();

                    GoalkeeperAttributes goalkeeperAttributes = new GoalkeeperAttributes(reflexes, aerialGame, positioning,
                            rushingOut, handling, footwork);
                    GoalkeeperCareerStats goalkeeperCareerStats = new GoalkeeperCareerStats(matchesPlayed, expulsions,goalsReceived,
                            penaltiesReceived, penaltiesSaved);
                    player = new Goalkeeper(person.name(), person.birthDate(), person.documentType(), person.documentNumber(),
                            positionEnum, goalkeeperAttributes, goalkeeperCareerStats);
                } else {
                    int tackling = ((Long) attributes.get("capacidadQuite")).intValue();
                    int speed = ((Long) attributes.get("velocidad")).intValue();
                    int skill = ((Long) attributes.get("habilidad")).intValue();
                    int heading = ((Long) attributes.get("cabezazo")).intValue();
                    int finishing = ((Long) attributes.get("definicion")).intValue();
                    int shotPower = ((Long) attributes.get("potenciaDisparo")).intValue();
                    int gameVision = ((Long) attributes.get("visionDeJuego")).intValue();
                    int physicalResistance = ((Long) attributes.get("resistenciaFisica")).intValue();

                    int goals = ((Long) stats.get("goles")).intValue();
                    int penalties = ((Long) stats.get("penales")).intValue();
                    int penaltiesScored = ((Long) stats.get("penalesConvertidos")).intValue();
                    int assists = ((Long) stats.get("pasesGol")).intValue();
                    FieldPlayerAttributes playerAttributes = new FieldPlayerAttributes(tackling,speed,skill,heading,finishing,shotPower,gameVision,physicalResistance);
                    FieldPlayerCareerStats playerCareerStats = new FieldPlayerCareerStats(matchesPlayed, expulsions, goals, penalties, penaltiesScored, assists);
                    player = new FieldPlayer(person.name(), person.birthDate(), person.documentType(), person.documentNumber(),
                            positionEnum, playerAttributes, playerCareerStats);
                }

                playerList.add(player);
            }

            JSONObject dtObj = (JSONObject) teamP.get("dt");
            PersonData coachPerson = readPerson(dtObj);
            String dtCountryName = (String) dtObj.get("pais");
            Country dtCountry = countries.computeIfAbsent(
                    dtCountryName, Country::new
            );
            int titles = ((Long) dtObj.get("titulosObtenidos")).intValue();
            Coach coach = new Coach(coachPerson.name(), coachPerson.birthDate(), coachPerson.documentType(),
                    coachPerson.documentNumber(), dtCountry, titles);
            if (playerList.size() != 18) {
                throw new IllegalArgumentException(
                        "El equipo " + nameT + " tiene " + playerList.size() +
                                " jugadores. Debe tener exactamente 18."
                );
            }

            Team cTeam = new Team(nameT, country, rankingPosition, playerList, coach);
            tournament.addTeam(cTeam);
        }


        JSONObject refereesObj = (JSONObject) tournamentJ.get("arbitros");
        JSONArray referees = (JSONArray) refereesObj.get("arbitro");

        for (Object r : referees) {
            JSONObject refObj = (JSONObject) r;
            PersonData refPerson = readPerson(refObj);
            String refCountryName = (String) refObj.get("pais");
            Country refCountry = countries.computeIfAbsent(
                    refCountryName, Country::new
            );
            int yearsAsReferee = ((Long) refObj.get("aniosReferato")).intValue();
            Referee referee = new Referee(refPerson.name(), refPerson.birthDate(), refPerson.documentType(),
                    refPerson.documentNumber(), refCountry, yearsAsReferee);
            tournament.addReferee(referee);

        }
        return tournament;
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
