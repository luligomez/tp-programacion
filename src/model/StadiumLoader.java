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

    public static ArrayList<Stadium> loadStadiums() {
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
        // 1. Inserción de Ciudades (IDs asignados del 1 al 17)
        String insertCities = "INSERT INTO city (name, country) VALUES " +
                "('Buenos Aires', 'Argentina'), " +          // ID: 1
                "('Córdoba', 'Argentina'), " +               // ID: 2
                "('Rosario', 'Argentina'), " +               // ID: 3
                "('Mendoza', 'Argentina'), " +               // ID: 4
                "('Rio de Janeiro', 'Brasil'), " +           // ID: 5
                "('São Paulo', 'Brasil'), " +                // ID: 6
                "('Belo Horizonte', 'Brasil'), " +           // ID: 7
                "('Porto Alegre', 'Brasil'), " +             // ID: 8
                "('Montevideo', 'Uruguay'), " +              // ID: 9
                "('Santiago', 'Chile'), " +                  // ID: 10
                "('Lima', 'Perú'), " +                       // ID: 11
                "('Bogotá', 'Colombia'), " +                 // ID: 12
                "('Medellín', 'Colombia'), " +               // ID: 13
                "('Quito', 'Ecuador'), " +                   // ID: 14
                "('Guayaquil', 'Ecuador'), " +               // ID: 15
                "('Asunción', 'Paraguay'), " +               // ID: 16
                "('La Paz', 'Bolivia');";                    // ID: 17
        statement.executeUpdate(insertCities);

        // 2. Inserción de Estadios (29 estadios vinculados a sus respectivos city_id)
        String insertStadiums = "INSERT INTO stadium (name, capacity, city_id) VALUES " +
                // Argentina
                "('Estadio MÁS Monumental', 84500, 1), " +
                "('Estadio Alberto J. Armando (La Bombonera)', 54000, 1), " +
                "('Estadio José Amalfitani', 49540, 1), " +
                "('Estadio Pedro Bidegain (Nuevo Gasómetro)', 47964, 1), " +
                "('Estadio Mario Alberto Kempes', 57000, 2), " +
                "('Estadio Gigante de Arroyito', 41654, 3), " +
                "('Estadio Malvinas Argentinas', 42500, 4), " +
                // Brasil
                "('Estadio Maracaná', 78838, 5), " +
                "('Estadio Nilton Santos', 46931, 5), " +
                "('MorumBIS', 66795, 6), " +
                "('Neo Química Arena', 49205, 6), " +
                "('Allianz Parque', 43713, 6), " +
                "('Estadio Mineirão', 61846, 7), " +
                "('Arena do Grêmio', 55662, 8), " +
                "('Estadio Beira-Rio', 50128, 8), " +
                // Uruguay
                "('Estadio Centenario', 60000, 9), " +
                "('Estadio Campeón del Siglo', 40000, 9), " +
                "('Estadio Gran Parque Central', 34000, 9), " +
                // Chile
                "('Estadio Nacional Julio Martínez Prádanos', 48665, 10), " +
                "('Estadio Monumental David Arellano', 47347, 10), " +
                // Perú
                "('Estadio Monumental U', 80093, 11), " +
                "('Estadio Nacional del Perú', 50000, 11), " +
                // Colombia
                "('Estadio Nemesio Camacho El Campín', 36344, 12), " +
                "('Estadio Atanasio Girardot', 40943, 13), " +
                // Ecuador
                "('Estadio Rodrigo Paz Delgado', 41575, 14), " +
                "('Estadio Monumental Banco Pichincha', 59283, 15), " +
                // Paraguay
                "('Estadio Defensores del Chaco', 42352, 16), " +
                "('Estadio General Pablo Rojas (La Nueva Olla)', 45000, 16), " +
                // Bolivia
                "('Estadio Hernando Siles', 41143, 17);";
        statement.executeUpdate(insertStadiums);

        System.out.println("✅ Base de datos vacía detectada. Se crearon 17 ciudades y 29 estadios de prueba.");
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