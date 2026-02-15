// CodeFlowTracker.java (updated)
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import database.DatabaseConnection;
import models.Task;
import models.Project;
import models.Priority;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CodeFlowTracker extends Application {

    private BorderPane mainLayout;
    private TaskManager taskManager;
    private ProgressTracker progressTracker;
    private CalendarView calendarView;
    private LeetCodeIntegration leetCodeIntegration;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("CodeFlow Tracker - Your Programming Companion");

        // Test database connection
        DatabaseConnection.testConnection();

        // Initialize managers
        taskManager = new TaskManager();
        progressTracker = new ProgressTracker();
        calendarView = new CalendarView();
        leetCodeIntegration = new LeetCodeIntegration();

        // Create main layout
        mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #1e1e2e;");

        // Create top navigation
        HBox topNav = createTopNavigation();
        mainLayout.setTop(topNav);

        // Show dashboard by default
        showDashboard();

        Scene scene = new Scene(mainLayout, 1200, 800);
        scene.getStylesheets().add(getStylesheet());
        primaryStage.setScene(scene);
        primaryStage.show();

        // Add shutdown hook to close database connection
        primaryStage.setOnCloseRequest(e -> DatabaseConnection.closeConnection());
    }

    private HBox createTopNavigation() {
        HBox nav = new HBox(15);
        nav.setPadding(new Insets(15, 20, 15, 20));
        nav.setStyle("-fx-background-color: #181825; -fx-border-color: #313244; -fx-border-width: 0 0 2 0;");
        nav.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("CodeFlow Tracker");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#cba6f7"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Button dashboardBtn = createNavButton("Dashboard", "📊");
        Button tasksBtn = createNavButton("Tasks", "✓");
        Button progressBtn = createNavButton("Progress", "📈");
        Button calendarBtn = createNavButton("Calendar", "📅");
        Button leetcodeBtn = createNavButton("LeetCode", "💻");

        dashboardBtn.setOnAction(e -> showDashboard());
        tasksBtn.setOnAction(e -> showTasks());
        progressBtn.setOnAction(e -> showProgress());
        calendarBtn.setOnAction(e -> showCalendar());
        leetcodeBtn.setOnAction(e -> showLeetCode());

        nav.getChildren().addAll(title, spacer, dashboardBtn, tasksBtn, progressBtn, calendarBtn, leetcodeBtn);
        return nav;
    }

    private Button createNavButton(String text, String icon) {
        Button btn = new Button(icon + " " + text);
        btn.setStyle("-fx-background-color: #313244; -fx-text-fill: #cdd6f4; " +
                "-fx-padding: 8 16; -fx-cursor: hand; -fx-font-size: 13px; " +
                "-fx-background-radius: 6;");

        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: #45475a; -fx-text-fill: #cdd6f4; " +
                        "-fx-padding: 8 16; -fx-cursor: hand; -fx-font-size: 13px; " +
                        "-fx-background-radius: 6;"));

        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: #313244; -fx-text-fill: #cdd6f4; " +
                        "-fx-padding: 8 16; -fx-cursor: hand; -fx-font-size: 13px; " +
                        "-fx-background-radius: 6;"));

        return btn;
    }

    private void showDashboard() {
        VBox dashboard = new VBox(20);
        dashboard.setPadding(new Insets(30));

        Label welcomeLabel = new Label("Welcome to CodeFlow Tracker! 👋");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        welcomeLabel.setTextFill(Color.web("#f5e0dc"));

        // Stats cards
        HBox statsBox = new HBox(20);
        statsBox.getChildren().addAll(
                createStatCard("Tasks Completed Today", String.valueOf(taskManager.getCompletedToday()), "#a6e3a1"),
                createStatCard("Active Tasks", String.valueOf(taskManager.getActiveTasks()), "#f9e2af"),
                createStatCard("Current Streak", progressTracker.getCurrentStreak() + " days", "#fab387"),
                createStatCard("Total Problems Solved", String.valueOf(progressTracker.getTotalSolved()), "#cba6f7")
        );

        // Today's tasks preview
        VBox todaysTasks = createTodayTasksPreview();

        // Upcoming projects
        VBox upcomingProjects = createUpcomingProjectsPreview();

        dashboard.getChildren().addAll(welcomeLabel, statsBox, todaysTasks, upcomingProjects);

        ScrollPane scrollPane = new ScrollPane(dashboard);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #1e1e2e; -fx-background-color: #1e1e2e;");

        mainLayout.setCenter(scrollPane);
    }

    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: #313244; -fx-padding: 20; -fx-background-radius: 10;");
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(250);

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        valueLabel.setTextFill(Color.web(color));

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", 12));
        titleLabel.setTextFill(Color.web("#bac2de"));
        titleLabel.setWrapText(true);
        titleLabel.setAlignment(Pos.CENTER);

        card.getChildren().addAll(valueLabel, titleLabel);
        return card;
    }

    private VBox createTodayTasksPreview() {
        VBox container = new VBox(10);

        Label header = new Label("📋 Today's Tasks");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        header.setTextFill(Color.web("#f5e0dc"));

        VBox tasksList = new VBox(8);
        tasksList.setStyle("-fx-background-color: #313244; -fx-padding: 15; -fx-background-radius: 10;");

        for (Task task : taskManager.getTodayTasks()) {
            tasksList.getChildren().add(createTaskPreviewItem(task));
        }

        if (taskManager.getTodayTasks().isEmpty()) {
            Label emptyLabel = new Label("No tasks for today. Add some to get started!");
            emptyLabel.setTextFill(Color.web("#bac2de"));
            tasksList.getChildren().add(emptyLabel);
        }

        container.getChildren().addAll(header, tasksList);
        return container;
    }

    private HBox createTaskPreviewItem(Task task) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);

        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(task.isCompleted());
        checkBox.setOnAction(e -> {
            task.setCompleted(checkBox.isSelected());
            taskManager.saveTask(task);
            showDashboard(); // Refresh
        });

        Label taskLabel = new Label(task.getTitle());
        taskLabel.setTextFill(Color.web("#cdd6f4"));
        taskLabel.setFont(Font.font("Arial", 13));
        if (task.isCompleted()) {
            taskLabel.setStyle("-fx-strikethrough: true; -fx-text-fill: #6c7086;");
        }

        Label priorityLabel = new Label(task.getPriority().toString());
        priorityLabel.setStyle("-fx-background-color: " + getPriorityColor(task.getPriority()) +
                "; -fx-padding: 3 8; -fx-background-radius: 4; -fx-text-fill: #1e1e2e;");
        priorityLabel.setFont(Font.font("Arial", FontWeight.BOLD, 10));

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        item.getChildren().addAll(checkBox, taskLabel, spacer, priorityLabel);
        return item;
    }

    private VBox createUpcomingProjectsPreview() {
        VBox container = new VBox(10);

        Label header = new Label("🗓️ Upcoming Projects");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        header.setTextFill(Color.web("#f5e0dc"));

        VBox projectsList = new VBox(8);
        projectsList.setStyle("-fx-background-color: #313244; -fx-padding: 15; -fx-background-radius: 10;");

        for (Project project : calendarView.getUpcomingProjects(7)) {
            projectsList.getChildren().add(createProjectPreviewItem(project));
        }

        if (calendarView.getUpcomingProjects(7).isEmpty()) {
            Label emptyLabel = new Label("No upcoming projects in the next 7 days.");
            emptyLabel.setTextFill(Color.web("#bac2de"));
            projectsList.getChildren().add(emptyLabel);
        }

        container.getChildren().addAll(header, projectsList);
        return container;
    }

    private HBox createProjectPreviewItem(Project project) {
        HBox item = new HBox(15);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(8));

        VBox dateBox = new VBox(2);
        dateBox.setAlignment(Pos.CENTER);
        dateBox.setStyle("-fx-background-color: #45475a; -fx-padding: 8; -fx-background-radius: 6;");

        Label dayLabel = new Label(project.getDueDate().format(DateTimeFormatter.ofPattern("dd")));
        dayLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        dayLabel.setTextFill(Color.web("#f5e0dc"));

        Label monthLabel = new Label(project.getDueDate().format(DateTimeFormatter.ofPattern("MMM")));
        monthLabel.setFont(Font.font("Arial", 10));
        monthLabel.setTextFill(Color.web("#bac2de"));

        dateBox.getChildren().addAll(dayLabel, monthLabel);

        VBox details = new VBox(4);
        Label titleLabel = new Label(project.getTitle());
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        titleLabel.setTextFill(Color.web("#cdd6f4"));

        Label descLabel = new Label(project.getDescription());
        descLabel.setFont(Font.font("Arial", 11));
        descLabel.setTextFill(Color.web("#bac2de"));

        details.getChildren().addAll(titleLabel, descLabel);

        item.getChildren().addAll(dateBox, details);
        return item;
    }

    private void showTasks() {
        mainLayout.setCenter(taskManager.getView());
    }

    private void showProgress() {
        mainLayout.setCenter(progressTracker.getView());
    }

    private void showCalendar() {
        mainLayout.setCenter(calendarView.getView());
    }

    private void showLeetCode() {
        mainLayout.setCenter(leetCodeIntegration.getView());
    }

    private String getPriorityColor(Priority priority) {
        if (priority == Priority.HIGH) {
            return "#f38ba8";
        } else if (priority == Priority.MEDIUM) {
            return "#f9e2af";
        } else {
            return "#a6e3a1";
        }
    }

    private String getStylesheet() {
        return "data:text/css," +
                ".scroll-pane { -fx-background-color: #1e1e2e; }" +
                ".scroll-pane .viewport { -fx-background-color: #1e1e2e; }" +
                ".scroll-bar { -fx-background-color: #313244; }" +
                ".scroll-bar .thumb { -fx-background-color: #45475a; }";
    }

    public static void main(String[] args) {
        launch(args);
    }
}