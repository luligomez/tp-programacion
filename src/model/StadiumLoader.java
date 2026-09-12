package model;

import model.place.City;
import model.place.Stadium;
import java.sql.*;
import java.util.ArrayList;

public class StadiumLoader {

    public ArrayList<Stadium> loadStadiums() {
        ArrayList<Stadium> stadiums = new ArrayList<>();

        String driver = "org.postgresql.Driver";
        try {
            Class.forName(driver);
            String url = "jdbc:postgresql://localhost:5432/tournament_db";
            String user = "postgres";
            String password = "1234";
            Connection connection = DriverManager.getConnection(url, user, password);

            // Crear SENTENCIA ??????
            Statement statement = connection.createStatement();
            String query = "SELECT s.id, s.name, s.capacity, c.name as city_name, c.country " +
                    "FROM stadium s JOIN city c ON s.city_id = c.id";

            ResultSet results = statement.executeQuery(query);

            while(results.next()) {
                String name = results.getString("name");
                int capacity = results.getInt("capacity");
                String cityName = results.getString("city_name");
                String country = results.getString("country");

                City city = new City(cityName, country);
                Stadium stadium = new Stadium(name, capacity, city);
                stadiums.add(stadium);
            }

            results.close();
            statement.close();
            connection.close();

        } catch(ClassNotFoundException cnfe) {
            System.err.println("No existe clase: " + driver);
        } catch(SQLException se) {
            System.err.println("SQLException: " + se);
        }

        return stadiums;
    }
}