package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DAOUtils {

    private DAOUtils() {
    }

    public static boolean isTableEmpty(String tableName) throws SQLException {
        if (!tableName.equals("city") && !tableName.equals("stadium")) {
            throw new IllegalArgumentException("Invalid table name.");
        }

        String sql = "SELECT COUNT(*) FROM " + tableName;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            return !rs.next() || rs.getInt(1) == 0;
        }
    }
}