-- DatabaseSetup.sql
CREATE DATABASE IF NOT EXISTS codeflow_tracker;
USE codeflow_tracker;

-- Projects table
CREATE TABLE IF NOT EXISTS projects (
                                        id VARCHAR(36) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    due_date DATE NOT NULL,
    color VARCHAR(20) DEFAULT '#89b4fa',
    progress INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

-- Tasks table
CREATE TABLE IF NOT EXISTS tasks (
                                     id VARCHAR(36) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    priority ENUM('HIGH', 'MEDIUM', 'LOW') DEFAULT 'MEDIUM',
    due_date DATE,
    completed BOOLEAN DEFAULT FALSE,
    category VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

-- LeetCode problems table
CREATE TABLE IF NOT EXISTS leetcode_problems (
                                                 id VARCHAR(36) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    tags VARCHAR(500),
    difficulty ENUM('Easy', 'Medium', 'Hard') DEFAULT 'Easy',
    solved BOOLEAN DEFAULT FALSE,
    url VARCHAR(500),
    solved_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

-- Daily progress table
CREATE TABLE IF NOT EXISTS daily_progress (
                                              id INT AUTO_INCREMENT PRIMARY KEY,
                                              progress_date DATE UNIQUE NOT NULL,
                                              problems_solved INT DEFAULT 0,
                                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_projects_due_date ON projects(due_date);
CREATE INDEX idx_tasks_due_date ON tasks(due_date);
CREATE INDEX idx_tasks_completed ON tasks(completed);
CREATE INDEX idx_leetcode_solved ON leetcode_problems(solved);
CREATE INDEX idx_daily_progress_date ON daily_progress(progress_date);

-- Insert sample data
INSERT INTO projects (id, title, description, due_date, color, progress) VALUES
                                                                             (UUID(), 'E-commerce Website', 'Build a full-stack e-commerce platform with React and Spring Boot',
                                                                              DATE_ADD(CURDATE(), INTERVAL 5 DAY), '#89b4fa', 25),
                                                                             (UUID(), 'Machine Learning Project', 'Implement image classification using CNN',
                                                                              DATE_ADD(CURDATE(), INTERVAL 12 DAY), '#f9e2af', 10),
                                                                             (UUID(), 'Mobile App Development', 'Create a fitness tracking app with Flutter',
                                                                              DATE_ADD(CURDATE(), INTERVAL 20 DAY), '#a6e3a1', 5),
                                                                             (UUID(), 'Database Optimization', 'Optimize queries and indexing for better performance',
                                                                              DATE_ADD(CURDATE(), INTERVAL 3 DAY), '#f38ba8', 70);

INSERT INTO tasks (id, title, description, priority, due_date, category) VALUES
                                                                             (UUID(), 'Solve 3 LeetCode Problems', 'Focus on dynamic programming', 'HIGH', CURDATE(), 'LeetCode'),
                                                                             (UUID(), 'Complete Java Project Module', 'Finish authentication system', 'HIGH', CURDATE(), 'Project'),
                                                                             (UUID(), 'Review Data Structures', 'Trees and graphs revision', 'MEDIUM', DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'Study'),
                                                                             (UUID(), 'Write Technical Blog Post', 'About recent project learnings', 'LOW', DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Writing');

INSERT INTO leetcode_problems (id, title, tags, difficulty, solved, url) VALUES
                                                                             (UUID(), 'Two Sum', 'Array, Hash Table', 'Easy', TRUE, 'https://leetcode.com/problems/two-sum/'),
                                                                             (UUID(), 'Longest Substring Without Repeating Characters', 'String, Sliding Window', 'Medium', TRUE, 'https://leetcode.com/problems/longest-substring-without-repeating-characters/'),
                                                                             (UUID(), 'Median of Two Sorted Arrays', 'Array, Binary Search', 'Hard', FALSE, 'https://leetcode.com/problems/median-of-two-sorted-arrays/'),
                                                                             (UUID(), 'Valid Parentheses', 'Stack, String', 'Easy', TRUE, 'https://leetcode.com/problems/valid-parentheses/'),
                                                                             (UUID(), 'Merge Two Sorted Lists', 'Linked List, Recursion', 'Easy', FALSE, 'https://leetcode.com/problems/merge-two-sorted-lists/');

-- Insert daily progress for the last 14 days
INSERT INTO daily_progress (progress_date, problems_solved) VALUES
                                                                (CURDATE() - INTERVAL 13 DAY, 3),
                                                                (CURDATE() - INTERVAL 12 DAY, 5),
                                                                (CURDATE() - INTERVAL 11 DAY, 2),
                                                                (CURDATE() - INTERVAL 10 DAY, 4),
                                                                (CURDATE() - INTERVAL 9 DAY, 1),
                                                                (CURDATE() - INTERVAL 8 DAY, 3),
                                                                (CURDATE() - INTERVAL 7 DAY, 4),
                                                                (CURDATE() - INTERVAL 6 DAY, 2),
                                                                (CURDATE() - INTERVAL 5 DAY, 5),
                                                                (CURDATE() - INTERVAL 4 DAY, 3),
                                                                (CURDATE() - INTERVAL 3 DAY, 4),
                                                                (CURDATE() - INTERVAL 2 DAY, 2),
                                                                (CURDATE() - INTERVAL 1 DAY, 3),
                                                                (CURDATE(), 1);