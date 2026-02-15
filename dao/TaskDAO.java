package dao;

import models.Task;
import models.Priority;
import database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskDAO {

    // Formatter for SQLite datetime format
    private static final DateTimeFormatter SQLITE_DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public boolean addTask(Task task) {
        String sql = "INSERT INTO tasks (id, title, description, priority, due_date, completed, category, created_at, completed_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, task.getId() != null ? task.getId() : UUID.randomUUID().toString());
            pstmt.setString(2, task.getTitle());
            pstmt.setString(3, task.getDescription());
            pstmt.setString(4, task.getPriority().name());
            pstmt.setString(5, task.getDueDate() != null ? task.getDueDate().toString() : null);
            pstmt.setBoolean(6, task.isCompleted());
            pstmt.setString(7, task.getCategory());
            pstmt.setString(8, task.getCreatedAt().format(SQLITE_DATETIME_FORMATTER));
            pstmt.setString(9, task.getCompletedAt() != null ? task.getCompletedAt().format(SQLITE_DATETIME_FORMATTER) : null);

            int result = pstmt.executeUpdate();
            System.out.println("✅ Task added: " + task.getTitle());
            return result > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error adding task: " + e.getMessage());
            return false;
        }
    }

    public boolean updateTask(Task task) {
        String sql = "UPDATE tasks SET title = ?, description = ?, priority = ?, due_date = ?, " +
                "completed = ?, category = ?, completed_at = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setString(3, task.getPriority().name());
            pstmt.setString(4, task.getDueDate() != null ? task.getDueDate().toString() : null);
            pstmt.setBoolean(5, task.isCompleted());
            pstmt.setString(6, task.getCategory());
            pstmt.setString(7, task.getCompletedAt() != null ? task.getCompletedAt().format(SQLITE_DATETIME_FORMATTER) : null);
            pstmt.setString(8, task.getId());

            int result = pstmt.executeUpdate();
            System.out.println("✅ Task updated: " + task.getTitle());
            return result > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error updating task: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteTask(String id) {
        String sql = "DELETE FROM tasks WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            int result = pstmt.executeUpdate();
            System.out.println("✅ Task deleted: " + id);
            return result > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error deleting task: " + e.getMessage());
            return false;
        }
    }

    public Task getTaskById(String id) {
        String sql = "SELECT * FROM tasks WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractTaskFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting task: " + e.getMessage());
        }

        return null;
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks ORDER BY due_date, priority";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                tasks.add(extractTaskFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting all tasks: " + e.getMessage());
        }

        return tasks;
    }

    public List<Task> getTasksByDate(LocalDate date) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE due_date = ? ORDER BY priority";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, date.toString());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                tasks.add(extractTaskFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting tasks by date: " + e.getMessage());
        }

        return tasks;
    }

    public List<Task> getActiveTasks() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE completed = 0 ORDER BY due_date, priority";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                tasks.add(extractTaskFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting active tasks: " + e.getMessage());
        }

        return tasks;
    }

    public List<Task> getCompletedTasks() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE completed = 1 ORDER BY completed_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                tasks.add(extractTaskFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting completed tasks: " + e.getMessage());
        }

        return tasks;
    }

    public int getCompletedToday() {
        String sql = "SELECT COUNT(*) FROM tasks WHERE completed = 1 AND date(completed_at) = date('now')";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting completed today: " + e.getMessage());
        }

        return 0;
    }

    public int getActiveCount() {
        String sql = "SELECT COUNT(*) FROM tasks WHERE completed = 0";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting active count: " + e.getMessage());
        }

        return 0;
    }

    private Task extractTaskFromResultSet(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String title = rs.getString("title");
        String description = rs.getString("description");
        Priority priority = Priority.valueOf(rs.getString("priority"));

        String dueDateStr = rs.getString("due_date");
        LocalDate dueDate = dueDateStr != null ? LocalDate.parse(dueDateStr) : null;

        boolean completed = rs.getInt("completed") == 1;

        // Parse datetime strings with custom formatter
        String createdAtStr = rs.getString("created_at");
        LocalDateTime createdAt = createdAtStr != null ?
                LocalDateTime.parse(createdAtStr, SQLITE_DATETIME_FORMATTER) : LocalDateTime.now();

        String completedAtStr = rs.getString("completed_at");
        LocalDateTime completedAt = completedAtStr != null ?
                LocalDateTime.parse(completedAtStr, SQLITE_DATETIME_FORMATTER) : null;

        String category = rs.getString("category");

        return new Task(id, title, description, priority, dueDate, completed, createdAt, completedAt, category);
    }
}