package model;
import model.person.*;
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
            String name = (String) e.get("nombre");
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
                String firstName = (String) person.get("nombre");
                LocalDate birthDate = LocalDate.parse((String) person.get("fechaNacimiento"), DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                JSONObject attributes = (JSONObject) j.get("caracteristicas");
                JSONObject stats = (JSONObject) j.get("estadisticas");

                Player player;
                int matchesPlayed = ((Long) stats.get("partidosJugados")).intValue();
                int expulsions = ((Long) stats.get("expulsiones")).intValue();
                int rating = 0; //como se calcula el rating????

                if (positionEnum == Position.GOALKEEPER) {
                    //crear goalKeeper
                    int reflexes = ((Long) attributes.get("reflejos")).intValue();
                    int aerialGame = ((Long) attributes.get("juegoAereo")).intValue();
                    int positioning = ((Long) attributes.get("ubicacion")).intValue();
                    int closingDown = ((Long) attributes.get("achique")).intValue();
                    int handling = ((Long) attributes.get("seguridadManos")).intValue();
                    int footwork = ((Long) attributes.get("juegoPies")).intValue();

                    int goalsReceived = ((Long) stats.get("golesRecibidos")).intValue();
                    int penaltiesReceived = ((Long) stats.get("penalesRecibidos")).intValue();
                    int penaltiesSaved = ((Long) stats.get("penalesAtajados")).intValue();

                    player = new Goalkeeper(firstName, "", birthDate, documentType, documentNumber,
                            rating, matchesPlayed, expulsions, reflexes, aerialGame, positioning,
                            closingDown, handling, footwork, goalsReceived,
                            penaltiesReceived, penaltiesSaved);

                    //como se divide el lastName?? como se si tiene mas de un nombre o apellido???

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

                    player = new FieldPlayer(firstName, "", birthDate, documentType, documentNumber,
                            positionEnum, rating, matchesPlayed, expulsions,
                            tackling, speed, skill, heading, finishing,
                            shotPower, gameVision, physicalResistance,
                            goals, penalties, penaltiesScored, assists);
                }

                playerList.add(player);
            }
            //crear dt
            //se repite el codigo de persona, sacar en un metodo? que devolveria?
            JSONObject dtObj = (JSONObject) teamP.get("dt");
            JSONObject dtPerson = (JSONObject) dtObj.get("persona");
            String dtDocumentType = (String) dtPerson.get("tipoDocumento");
            String dtDocumentNumber = dtPerson.get("nroDocumento").toString();
            String dtFirstName = (String) dtPerson.get("nombre");
            LocalDate dtBirthDate = LocalDate.parse((String) dtPerson.get("fechaNacimiento"), DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            String dtCountryName = (String) dtObj.get("pais");
            Country dtCountry = new Country(dtCountryName);  //
            int titles = ((Long) dtObj.get("titulosObtenidos")).intValue();

            Coach coach = new Coach(dtFirstName, "", dtBirthDate, dtDocumentType, dtDocumentNumber,
                    dtCountry, titles);

            Team JTeam = new Team(name, country, rankingPosition, playerList, coach);
            tournament.addTeam(JTeam);
        }

        return tournament;
    }

    private static Position mapPosition(String positionStr) {
        switch(positionStr.toLowerCase()) {
            case "arquero": return Position.GOALKEEPER;
            case "defensor": return Position.DEFENDER;
            case "mediocampista": return Position.MIDFIELDER;
            case "delantero": return Position.FORWARD;
            default: return Position.FORWARD;
        }
    }
}
