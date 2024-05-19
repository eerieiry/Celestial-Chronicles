package celestialchronicles.celestialchronicles;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static celestialchronicles.celestialchronicles.Database.*;

public class LevelDAO {

    public int level;
    public double bestTime;
    public static double bestScore;
    public boolean unlocked;

    public void getLevel(int levelNumber) throws SQLException {
        String sql = "SELECT * FROM levels WHERE level = ?";

        try {
            Database.connectToDatabase();
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

                pstmt.setInt(1, levelNumber);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    this.level = rs.getInt("level");
                    this.bestTime = rs.getDouble("best_time");
                    this.bestScore = rs.getInt("best_score");
                    this.unlocked = rs.getBoolean("unlocked");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateLevel() throws SQLException {
        String sql = "UPDATE levels SET best_time = ?,  best_score = ?, mode = ?, unlocked = ? WHERE level = ?";

        try (Connection conn = connection;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, this.bestTime);
            pstmt.setDouble(2, this.bestScore);
            pstmt.setBoolean(3, this.unlocked);
            pstmt.setInt(4, this.level);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unlockNextLevel(int currentLevel) throws SQLException {
        int nextLevel = currentLevel + 1;
        String sql = "UPDATE levels SET unlocked = TRUE WHERE level_number = ?";

        try (Connection conn = connection;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, nextLevel);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
