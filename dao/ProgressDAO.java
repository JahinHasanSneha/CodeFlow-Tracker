package dao;

import database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ProgressDAO {

    public boolean updateDailyProgress(LocalDate date, int problemsSolved) {
        String sql = "INSERT OR REPLACE INTO daily_progress (progress_date, problems_solved) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, date.toString());
            pstmt.setInt(2, problemsSolved);

            int result = pstmt.executeUpdate();
            System.out.println("✅ Daily progress updated for " + date);
            return result > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error updating daily progress: " + e.getMessage());
            return false;
        }
    }

    public Map<LocalDate, Integer> getDailyProgress(int days) {
        Map<LocalDate, Integer> progress = new HashMap<>();
        String sql = "SELECT progress_date, problems_solved FROM daily_progress " +
                "WHERE progress_date >= date('now', ?) " +
                "ORDER BY progress_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, '-' + days + " days");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                progress.put(
                        LocalDate.parse(rs.getString("progress_date")),
                        rs.getInt("problems_solved")
                );
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting daily progress: " + e.getMessage());
        }

        return progress;
    }

    public int getTotalSolved() {
        String sql = "SELECT COALESCE(SUM(problems_solved), 0) FROM daily_progress";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting total solved: " + e.getMessage());
        }

        return 0;
    }

    public int getCurrentStreak() {
        int streak = 0;
        LocalDate currentDate = LocalDate.now();

        String sql = "SELECT problems_solved FROM daily_progress WHERE progress_date = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {

            for (int i = 0; i < 365; i++) {
                LocalDate checkDate = currentDate.minusDays(i);

                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, checkDate.toString());
                    ResultSet rs = pstmt.executeQuery();

                    if (rs.next() && rs.getInt("problems_solved") > 0) {
                        streak++;
                    } else {
                        break;
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error calculating streak: " + e.getMessage());
        }

        return streak;
    }

    public int getWeekProgress() {
        String sql = "SELECT COALESCE(SUM(problems_solved), 0) FROM daily_progress " +
                "WHERE progress_date >= date('now', '-7 days')";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting week progress: " + e.getMessage());
        }

        return 0;
    }

    public double getAveragePerDay() {
        String sql = "SELECT COALESCE(AVG(problems_solved), 0) FROM daily_progress " +
                "WHERE progress_date >= date('now', '-30 days')";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting average per day: " + e.getMessage());
        }

        return 0.0;
    }

    public int getProgressForDate(LocalDate date) {
        String sql = "SELECT problems_solved FROM daily_progress WHERE progress_date = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, date.toString());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("problems_solved");
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting progress for date: " + e.getMessage());
        }

        return 0;
    }
}