package dao;

import models.Project;
import database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProjectDAO {

    public boolean addProject(Project project) {
        String sql = "INSERT INTO projects (id, title, description, due_date, color, progress) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, project.getId() != null ? project.getId() : UUID.randomUUID().toString());
            pstmt.setString(2, project.getTitle());
            pstmt.setString(3, project.getDescription());
            pstmt.setString(4, project.getDueDate().toString());
            pstmt.setString(5, project.getColor());
            pstmt.setInt(6, project.getProgress());

            int result = pstmt.executeUpdate();
            System.out.println("✅ Project added: " + project.getTitle());
            return result > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error adding project: " + e.getMessage());
            return false;
        }
    }

    public boolean updateProject(Project project) {
        String sql = "UPDATE projects SET title = ?, description = ?, due_date = ?, color = ?, progress = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, project.getTitle());
            pstmt.setString(2, project.getDescription());
            pstmt.setString(3, project.getDueDate().toString());
            pstmt.setString(4, project.getColor());
            pstmt.setInt(5, project.getProgress());
            pstmt.setString(6, project.getId());

            int result = pstmt.executeUpdate();
            System.out.println("✅ Project updated: " + project.getTitle());
            return result > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error updating project: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteProject(String id) {
        String sql = "DELETE FROM projects WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            int result = pstmt.executeUpdate();
            System.out.println("✅ Project deleted: " + id);
            return result > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error deleting project: " + e.getMessage());
            return false;
        }
    }

    public Project getProjectById(String id) {
        String sql = "SELECT * FROM projects WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractProjectFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting project: " + e.getMessage());
        }

        return null;
    }

    public List<Project> getAllProjects() {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects ORDER BY due_date";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                projects.add(extractProjectFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting all projects: " + e.getMessage());
        }

        return projects;
    }

    public List<Project> getUpcomingProjects(int days) {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects WHERE due_date BETWEEN date('now') AND date('now', ?) ORDER BY due_date";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, '+' + days + " days");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                projects.add(extractProjectFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting upcoming projects: " + e.getMessage());
        }

        return projects;
    }

    public List<Project> getProjectsForDate(LocalDate date) {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects WHERE due_date = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, date.toString());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                projects.add(extractProjectFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting projects for date: " + e.getMessage());
        }

        return projects;
    }

    public List<Project> getProjectsDueThisWeek() {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects WHERE due_date BETWEEN date('now') AND date('now', '+7 days') ORDER BY due_date";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                projects.add(extractProjectFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting projects due this week: " + e.getMessage());
        }

        return projects;
    }

    public List<Project> getOverdueProjects() {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects WHERE due_date < date('now') ORDER BY due_date";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                projects.add(extractProjectFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting overdue projects: " + e.getMessage());
        }

        return projects;
    }

    public int getProjectCount() {
        String sql = "SELECT COUNT(*) FROM projects";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting project count: " + e.getMessage());
        }

        return 0;
    }

    public int getCompletedProjectsCount() {
        String sql = "SELECT COUNT(*) FROM projects WHERE progress = 100";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting completed projects count: " + e.getMessage());
        }

        return 0;
    }

    private Project extractProjectFromResultSet(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String title = rs.getString("title");
        String description = rs.getString("description");
        LocalDate dueDate = LocalDate.parse(rs.getString("due_date"));
        String color = rs.getString("color");
        int progress = rs.getInt("progress");

        return new Project(id, title, description, dueDate, color, progress);
    }
}