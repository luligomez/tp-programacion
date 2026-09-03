package model;
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

public class FileReader {

    public static Tournament fileReader(String file) throws Exception{
        Tournament tournament = new Tournament();

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(new java.io.FileReader(file));

        JSONObject tournamentJ = (JSONObject) jsonObject.get("torneo");
        JSONObject teamsObj = (JSONObject) tournamentJ.get("equipos");
        JSONArray teams = (JSONArray)  teamsObj.get("equipo");


        for (Object team: teams){
            JSONObject e = (JSONObject) team;
            String nameT = (String) e.get("nombre");
            String countryName = (String) e.get("pais");
            Country country = new Country(countryName);
            int rankingPosition = ((Long) e.get("ranking")).intValue();

            JSONObject teamP = (JSONObject) e.get("plantel");
            JSONObject playersObj = (JSONObject) teamP.get("jugadores");
            JSONArray players = (JSONArray) playersObj.get("jugador");

            List<Player> playerList = new ArrayList<>();
            for (Object p : players) {
                JSONObject j = (JSONObject) p;
                String position = (String) j.get("posicion");
                Position positionEnum = mapPosition(position);

                JSONObject person = (JSONObject) j.get("persona");

                String documentType = (String) person.get("tipoDocumento");
                String documentNumber = person.get("nroDocumento").toString();
                String name = (String) person.get("nombre");
                LocalDate birthDate = LocalDate.parse((String) person.get("fechaNacimiento"), DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                JSONObject attributes = (JSONObject) j.get("caracteristicas");
                JSONObject stats = (JSONObject) j.get("estadisticas");

                Player player;
                int matchesPlayed = ((Long) stats.get("partidosJugados")).intValue();
                int expulsions = ((Long) stats.get("expulsiones")).intValue();

                if (positionEnum == Position.GOALKEEPER) {
                    //crear goalKeeper
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
                    player = new Goalkeeper(name, birthDate, documentType, documentNumber, positionEnum,
                            goalkeeperAttributes, goalkeeperCareerStats);

                } else {
                    //crear fieldPlayer

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
                    player = new FieldPlayer(name, birthDate, documentType, documentNumber,
                            positionEnum, playerAttributes, playerCareerStats);
                }

                playerList.add(player);
            }
            //crear dt
            //se repite el codigo de persona, sacar en un metodo? que devolveria?
            JSONObject dtObj = (JSONObject) teamP.get("dt");
            JSONObject dtPerson = (JSONObject) dtObj.get("persona");
            String dtDocumentType = (String) dtPerson.get("tipoDocumento");
            String dtDocumentNumber = dtPerson.get("nroDocumento").toString();
            String dtName = (String) dtPerson.get("nombre");
            LocalDate dtBirthDate = LocalDate.parse((String) dtPerson.get("fechaNacimiento"), DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            String dtCountryName = (String) dtObj.get("pais");
            Country dtCountry = new Country(dtCountryName);  //
            int titles = ((Long) dtObj.get("titulosObtenidos")).intValue();

            Coach coach = new Coach(dtName, dtBirthDate, dtDocumentType, dtDocumentNumber,
                    dtCountry, titles);

            Team JTeam = new Team(nameT, country, rankingPosition, playerList, coach);
            tournament.addTeam(JTeam);
        }

        //leer referee
        //se repite el codigo de persona otra vez, sacar en un metodo? que devolveria?
        JSONObject refereesObj = (JSONObject) tournamentJ.get("arbitros");
        JSONArray referees = (JSONArray) refereesObj.get("arbitro");

        for (Object r : referees) {
            JSONObject refObj = (JSONObject) r;
            JSONObject refPerson = (JSONObject) refObj.get("persona");
            String refDocumentType = (String) refPerson.get("tipoDocumento");
            String refDocumentNumber = refPerson.get("nroDocumento").toString();
            String refName = (String) refPerson.get("nombre");
            LocalDate refBirthDate = LocalDate.parse((String) refPerson.get("fechaNacimiento"), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String refCountryName = (String) refObj.get("pais");
            Country refCountry = new Country(refCountryName);
            int yearsAsReferee = ((Long) refObj.get("aniosReferato")).intValue();

            Referee referee = new Referee(refName, refBirthDate, refDocumentType,
                    refDocumentNumber, refCountry, yearsAsReferee);
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
