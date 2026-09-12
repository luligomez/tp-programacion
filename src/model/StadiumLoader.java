package model;

import model.place.City;
import model.place.Stadium;
import java.sql.*;
import java.util.ArrayList;

import static org.postgresql.util.ClassLoaderStrategy.DRIVER;

public class StadiumLoader {

    static String DRIVER = "org.postgresql.Driver";
    static String URL = "jdbc:postgresql://localhost:5432/tournament_db";
    static String USER = "postgres";
    static String PASSWORD = "1234";

    public ArrayList<Stadium> loadStadiums() {
        ArrayList<Stadium> stadiums = new ArrayList<>();
        try {
            Class.forName(DRIVER);
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);

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
            System.err.println("No existe clase: " + DRIVER);
        } catch(SQLException se) {
            System.err.println("SQLException: " + se);
        }

        return stadiums;
    }

    private static void insertSampleData(Statement statement) throws SQLException {
        String insertCities = "INSERT INTO city (name, country) VALUES " + "('Buenos Aires', 'Argentina'), " + "('Rio de Janeiro', 'Brasil'), " + "('Montevideo', 'Uruguay');";
        statement.executeUpdate(insertCities);
        String insertStadiums = "INSERT INTO stadium (name, capacity, city_id) VALUES " + "('Estadio Monumental', 84500, 1), " + "('Estadio Maracaná', 78838, 2), " + "('Estadio Centenario', 60000, 3);";
        statement.executeUpdate(insertStadiums);
        System.out.println("✅ Base de datos vacía detectada. Se crearon datos de prueba iniciales.");
    }

    public static void initDatabase() {
        try {
            Class.forName(DRIVER);
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                 Statement statement = connection.createStatement()) {
                // 1\. Crear tabla 'city' si no existe
                String createCityTable = "CREATE TABLE IF NOT EXISTS city (" + "id SERIAL PRIMARY KEY, " + "name VARCHAR(100) NOT NULL, " + "country VARCHAR(100) NOT NULL" + ");";
                statement.execute(createCityTable);
                // 2\. Crear tabla 'stadium' si no existe
                String createStadiumTable = "CREATE TABLE IF NOT EXISTS stadium (" + "id SERIAL PRIMARY KEY, " + "name VARCHAR(100) NOT NULL, " + "capacity INT NOT NULL, " + "city_id INT REFERENCES city(id) ON DELETE CASCADE" + ");";
                statement.execute(createStadiumTable);
                // 3\. Cargar datos por defecto si la tabla está vacía
                ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM stadium;");
                if (rs.next() && rs.getInt(1) == 0) {
                    insertSampleData(statement);
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("No se encontró el driver JDBC: " + DRIVER);
        } catch (SQLException e) {
            System.err.println("Error al inicializar la Base de Datos: " + e.getMessage());
        }
    }
}