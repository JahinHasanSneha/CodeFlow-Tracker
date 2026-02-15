package dao;

import models.LeetCodeProblem;
import database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LeetCodeProblemDAO {

    private static final DateTimeFormatter SQLITE_DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public boolean addProblem(LeetCodeProblem problem) {
        String sql = "INSERT INTO leetcode_problems (id, title, tags, difficulty, solved, url, solved_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, problem.getId() != null ? problem.getId() : UUID.randomUUID().toString());
            pstmt.setString(2, problem.getTitle());
            pstmt.setString(3, problem.getTags());
            pstmt.setString(4, problem.getDifficulty());
            pstmt.setBoolean(5, problem.isSolved());
            pstmt.setString(6, problem.getUrl());
            pstmt.setString(7, problem.getSolvedAt() != null ? problem.getSolvedAt().format(SQLITE_DATETIME_FORMATTER) : null);

            int result = pstmt.executeUpdate();
            System.out.println("✅ LeetCode problem added: " + problem.getTitle());
            return result > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error adding problem: " + e.getMessage());
            return false;
        }
    }

    public boolean updateProblem(LeetCodeProblem problem) {
        String sql = "UPDATE leetcode_problems SET title = ?, tags = ?, difficulty = ?, " +
                "solved = ?, url = ?, solved_at = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, problem.getTitle());
            pstmt.setString(2, problem.getTags());
            pstmt.setString(3, problem.getDifficulty());
            pstmt.setBoolean(4, problem.isSolved());
            pstmt.setString(5, problem.getUrl());
            pstmt.setString(6, problem.getSolvedAt() != null ? problem.getSolvedAt().format(SQLITE_DATETIME_FORMATTER) : null);
            pstmt.setString(7, problem.getId());

            int result = pstmt.executeUpdate();
            System.out.println("✅ LeetCode problem updated: " + problem.getTitle());
            return result > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error updating problem: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteProblem(String id) {
        String sql = "DELETE FROM leetcode_problems WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            int result = pstmt.executeUpdate();
            System.out.println("✅ LeetCode problem deleted: " + id);
            return result > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error deleting problem: " + e.getMessage());
            return false;
        }
    }

    public List<LeetCodeProblem> getAllProblems() {
        List<LeetCodeProblem> problems = new ArrayList<>();
        String sql = "SELECT * FROM leetcode_problems ORDER BY difficulty, title";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                problems.add(extractProblemFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting all problems: " + e.getMessage());
        }

        return problems;
    }

    public List<LeetCodeProblem> getProblemsByDifficulty(String difficulty) {
        List<LeetCodeProblem> problems = new ArrayList<>();
        String sql = "SELECT * FROM leetcode_problems WHERE difficulty = ? ORDER BY title";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, difficulty);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                problems.add(extractProblemFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting problems by difficulty: " + e.getMessage());
        }

        return problems;
    }

    public List<LeetCodeProblem> getSolvedProblems() {
        List<LeetCodeProblem> problems = new ArrayList<>();
        String sql = "SELECT * FROM leetcode_problems WHERE solved = 1 ORDER BY solved_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                problems.add(extractProblemFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting solved problems: " + e.getMessage());
        }

        return problems;
    }

    public List<LeetCodeProblem> getUnsolvedProblems() {
        List<LeetCodeProblem> problems = new ArrayList<>();
        String sql = "SELECT * FROM leetcode_problems WHERE solved = 0 ORDER BY difficulty, title";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                problems.add(extractProblemFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting unsolved problems: " + e.getMessage());
        }

        return problems;
    }

    public int getSolvedCount() {
        String sql = "SELECT COUNT(*) FROM leetcode_problems WHERE solved = 1";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting solved count: " + e.getMessage());
        }

        return 0;
    }

    public int getCountByDifficulty(String difficulty) {
        String sql = "SELECT COUNT(*) FROM leetcode_problems WHERE difficulty = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, difficulty);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting count by difficulty: " + e.getMessage());
        }

        return 0;
    }

    private LeetCodeProblem extractProblemFromResultSet(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String title = rs.getString("title");
        String tags = rs.getString("tags");
        String difficulty = rs.getString("difficulty");
        boolean solved = rs.getInt("solved") == 1;
        String url = rs.getString("url");

        String solvedAtStr = rs.getString("solved_at");
        LocalDateTime solvedAt = solvedAtStr != null ?
                LocalDateTime.parse(solvedAtStr, SQLITE_DATETIME_FORMATTER) : null;

        return new LeetCodeProblem(id, title, tags, difficulty, solved, url, solvedAt);
    }
}