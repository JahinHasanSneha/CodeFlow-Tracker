package database;

import java.sql.*;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:codeflow_tracker.db";
    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null || isConnectionClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(URL);
                System.out.println("✅ Database connected successfully!");

                // Create tables if they don't exist
                createTables();

            } catch (ClassNotFoundException e) {
                System.err.println("❌ SQLite JDBC Driver not found: " + e.getMessage());
                e.printStackTrace();
            } catch (SQLException e) {
                System.err.println("❌ Database connection failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return connection;
    }

    private static boolean isConnectionClosed() {
        try {
            return connection == null || connection.isClosed();
        } catch (SQLException e) {
            return true;
        }
    }

    private static void createTables() {
        String createProjectsTable = """
            CREATE TABLE IF NOT EXISTS projects (
                id VARCHAR(36) PRIMARY KEY,
                title VARCHAR(255) NOT NULL,
                description TEXT,
                due_date DATE NOT NULL,
                color VARCHAR(20) DEFAULT '#89b4fa',
                progress INTEGER DEFAULT 0,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;

        String createTasksTable = """
            CREATE TABLE IF NOT EXISTS tasks (
                id VARCHAR(36) PRIMARY KEY,
                title VARCHAR(255) NOT NULL,
                description TEXT,
                priority VARCHAR(20) DEFAULT 'MEDIUM',
                due_date DATE,
                completed BOOLEAN DEFAULT FALSE,
                category VARCHAR(100),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                completed_at TIMESTAMP NULL,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;

        String createLeetCodeTable = """
            CREATE TABLE IF NOT EXISTS leetcode_problems (
                id VARCHAR(36) PRIMARY KEY,
                title VARCHAR(255) NOT NULL,
                tags VARCHAR(500),
                difficulty VARCHAR(20) DEFAULT 'Easy',
                solved BOOLEAN DEFAULT FALSE,
                url VARCHAR(500),
                solved_at TIMESTAMP NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;

        String createProgressTable = """
            CREATE TABLE IF NOT EXISTS daily_progress (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                progress_date DATE UNIQUE NOT NULL,
                problems_solved INTEGER DEFAULT 0,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createProjectsTable);
            stmt.execute(createTasksTable);
            stmt.execute(createLeetCodeTable);
            stmt.execute(createProgressTable);
            System.out.println("✅ Tables created/verified successfully!");

            // Insert sample data if tables are empty
            insertSampleDataIfNeeded();

        } catch (SQLException e) {
            System.err.println("❌ Error creating tables: " + e.getMessage());
        }
    }

    private static void insertSampleDataIfNeeded() {
        try {
            // Check if projects table is empty
            String checkProjects = "SELECT COUNT(*) FROM projects";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(checkProjects)) {

                if (rs.next() && rs.getInt(1) == 0) {
                    // Insert sample projects
                    String insertProjects = """
                        INSERT INTO projects (id, title, description, due_date, color, progress) VALUES
                        ('p1', 'E-commerce Website', 'Build a full-stack e-commerce platform with React and Spring Boot', 
                         date('now', '+5 days'), '#89b4fa', 25),
                        ('p2', 'Machine Learning Project', 'Implement image classification using CNN', 
                         date('now', '+12 days'), '#f9e2af', 10),
                        ('p3', 'Mobile App Development', 'Create a fitness tracking app with Flutter', 
                         date('now', '+20 days'), '#a6e3a1', 5),
                        ('p4', 'Database Optimization', 'Optimize queries and indexing for better performance', 
                         date('now', '+3 days'), '#f38ba8', 70)
                    """;
                    stmt.execute(insertProjects);
                    System.out.println("✅ Sample projects inserted!");
                }
            }

            // Check if tasks table is empty
            String checkTasks = "SELECT COUNT(*) FROM tasks";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(checkTasks)) {

                if (rs.next() && rs.getInt(1) == 0) {
                    String insertTasks = """
                        INSERT INTO tasks (id, title, description, priority, due_date, category) VALUES
                        ('t1', 'Solve 3 LeetCode Problems', 'Focus on dynamic programming', 'HIGH', date('now'), 'LeetCode'),
                        ('t2', 'Complete Java Project Module', 'Finish authentication system', 'HIGH', date('now'), 'Project'),
                        ('t3', 'Review Data Structures', 'Trees and graphs revision', 'MEDIUM', date('now', '+1 day'), 'Study'),
                        ('t4', 'Write Technical Blog Post', 'About recent project learnings', 'LOW', date('now', '+3 days'), 'Writing')
                    """;
                    stmt.execute(insertTasks);
                    System.out.println("✅ Sample tasks inserted!");
                }
            }

            // Check if leetcode table is empty
            String checkLeetCode = "SELECT COUNT(*) FROM leetcode_problems";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(checkLeetCode)) {

                if (rs.next() && rs.getInt(1) == 0) {
                    String insertLeetCode = """
                        INSERT INTO leetcode_problems (id, title, tags, difficulty, solved, url) VALUES
                        ('l1', 'Two Sum', 'Array, Hash Table', 'Easy', 1, 'https://leetcode.com/problems/two-sum/'),
                        ('l2', 'Longest Substring Without Repeating Characters', 'String, Sliding Window', 'Medium', 1, 'https://leetcode.com/problems/longest-substring-without-repeating-characters/'),
                        ('l3', 'Median of Two Sorted Arrays', 'Array, Binary Search', 'Hard', 0, 'https://leetcode.com/problems/median-of-two-sorted-arrays/'),
                        ('l4', 'Valid Parentheses', 'Stack, String', 'Easy', 1, 'https://leetcode.com/problems/valid-parentheses/'),
                        ('l5', 'Merge Two Sorted Lists', 'Linked List, Recursion', 'Easy', 0, 'https://leetcode.com/problems/merge-two-sorted-lists/')
                    """;
                    stmt.execute(insertLeetCode);
                    System.out.println("✅ Sample LeetCode problems inserted!");
                }
            }

            // Check if progress table is empty
            String checkProgress = "SELECT COUNT(*) FROM daily_progress";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(checkProgress)) {

                if (rs.next() && rs.getInt(1) == 0) {
                    String insertProgress = """
                        INSERT INTO daily_progress (progress_date, problems_solved) VALUES
                        (date('now', '-13 days'), 3),
                        (date('now', '-12 days'), 5),
                        (date('now', '-11 days'), 2),
                        (date('now', '-10 days'), 4),
                        (date('now', '-9 days'), 1),
                        (date('now', '-8 days'), 3),
                        (date('now', '-7 days'), 4),
                        (date('now', '-6 days'), 2),
                        (date('now', '-5 days'), 5),
                        (date('now', '-4 days'), 3),
                        (date('now', '-3 days'), 4),
                        (date('now', '-2 days'), 2),
                        (date('now', '-1 day'), 3),
                        (date('now'), 1)
                    """;
                    stmt.execute(insertProgress);
                    System.out.println("✅ Sample progress data inserted!");
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error inserting sample data: " + e.getMessage());
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("📁 Database connection closed.");
            } catch (SQLException e) {
                System.err.println("❌ Error closing connection: " + e.getMessage());
            }
        }
    }

    public static void testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("✅ Connection test successful!");
                System.out.println("📁 Database file: codeflow_tracker.db");
            }
        } catch (SQLException e) {
            System.err.println("❌ Connection test failed: " + e.getMessage());
        }
    }
}