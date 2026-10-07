package DAO;

import model.place.City;
import model.place.Stadium;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StadiumDAO {
    private CityDAO cityDAO = new CityDAO();

    public void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS stadium (" +
                "id SERIAL PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, " +
                "capacity INT NOT NULL, " +
                "city_id INT NOT NULL REFERENCES city(id))";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void insertSampleData() throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM stadium")) {

            // Insert only if the table is empty
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.executeUpdate("INSERT INTO stadium (name, capacity, city_id) VALUES " +
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
                        "('Estadio Hernando Siles', 41143, 17)");
            }
        }
    }

    public void insertStadium(String name, int capacity, long idCity) throws SQLException {
        String sql = "INSERT INTO stadium (name, capacity, city_id) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setInt(2, capacity);
            pstmt.setLong(3, idCity);
            pstmt.executeUpdate();
        }
    }

    public void updateStadium(long id, String newName, int newCapacity, long idCity) throws SQLException {
        String sql = "UPDATE stadium SET name = ?, capacity = ?, city_id = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setInt(2, newCapacity);
            pstmt.setLong(3, idCity);
            pstmt.setLong(4, id);
            pstmt.executeUpdate();
        }
    }

    public void deleteStadium(long id) throws SQLException {
        String sql = "DELETE FROM stadium WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        }
    }

    public ArrayList<Stadium> getAllStadiums() throws SQLException {
        String sql = "SELECT id, name, capacity, city_id FROM stadium ORDER BY id";
        ArrayList<Stadium> stadiums = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                int capacity = rs.getInt("capacity");
                long cityid = rs.getLong("city_id");
                City city = cityDAO.getCityById(cityid);
                Stadium stadium = new Stadium(id, name, capacity, city);
                stadiums.add(stadium);
            }
        }
        return stadiums;
    }

    public Stadium getStadiumById(long id) throws SQLException {
        String sql = "SELECT name, capacity, city_id FROM stadium WHERE id = ?";
        Stadium stadium = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    int capacity = rs.getInt("capacity");
                    long cityid = rs.getLong("city_id");
                    City city = cityDAO.getCityById(cityid);
                    stadium = new Stadium(id, name, capacity, city);
                }
            }
        }

        return stadium;
    }

}
