package DAO;

import model.place.City;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CityDAO {

    public void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS city (" +
                "id SERIAL PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, " +
                "country VARCHAR(100) NOT NULL)";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void insertSampleData() throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM city")) {

            // Insert only if the table is empty
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.executeUpdate("INSERT INTO city (name, country) VALUES " +
                        "('Buenos Aires', 'Argentina'), " +
                        "('Córdoba', 'Argentina'), " +
                        "('Rosario', 'Argentina'), " +
                        "('Mendoza', 'Argentina'), " +
                        "('Rio de Janeiro', 'Brasil'), " +
                        "('São Paulo', 'Brasil'), " +
                        "('Belo Horizonte', 'Brasil'), " +
                        "('Porto Alegre', 'Brasil'), " +
                        "('Montevideo', 'Uruguay'), " +
                        "('Santiago', 'Chile'), " +
                        "('Lima', 'Perú'), " +
                        "('Bogotá', 'Colombia'), " +
                        "('Medellín', 'Colombia'), " +
                        "('Quito', 'Ecuador'), " +
                        "('Guayaquil', 'Ecuador'), " +
                        "('Asunción', 'Paraguay'), " +
                        "('La Paz', 'Bolivia')");
            }
        }
    }

    public void insertCity(String name, String country) throws SQLException {
        String sql = "INSERT INTO city (name, country) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, country);
            pstmt.executeUpdate();
        }
    }

    public void updateCity(long id, String newName, String newCountry) throws SQLException {
        String sql = "UPDATE city SET name = ?, country = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setString(2, newCountry);
            pstmt.setLong(3, id);
            pstmt.executeUpdate();
        }
    }

    public boolean deleteCity(long id) throws SQLException {
        String countSql = "SELECT COUNT(*) FROM stadium WHERE city_id = ?";
        String deleteSql = "DELETE FROM city WHERE id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            // A city with stadiums cannot be deleted
            try (PreparedStatement countStmt = conn.prepareStatement(countSql)) {
                countStmt.setLong(1, id);
                try (ResultSet rs = countStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return false;
                    }
                }
            }
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setLong(1, id);
                deleteStmt.executeUpdate();
            }
            return true;
        }
    }

    public List<City> getAllCities() throws SQLException {
        String sql = "SELECT id, name, country FROM city ORDER BY id";
        List<City> cities = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                String country = rs.getString("country");
                City city = new City(id, name, country);
                cities.add(city);
            }
        }
        return cities;
    }

    public City getCityById(long id) throws SQLException {
        String sql = "SELECT name, country FROM city WHERE id = ?";
        City city = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    String country = rs.getString("country");
                    city = new City(id, name, country);
                }
            }
        }

        return city;
    }
}
