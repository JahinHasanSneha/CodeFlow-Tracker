import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import models.Project;
import services.ProjectService;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class CalendarView {
    private ObservableList<Project> projects;
    private VBox view;
    private GridPane calendarGrid;
    private Label monthYearLabel;
    private YearMonth currentYearMonth;
    private ProjectService projectService;
    private ScrollPane mainScrollPane;

    public CalendarView() {
        projectService = new ProjectService();
        projects = FXCollections.observableArrayList();
        currentYearMonth = YearMonth.now();
        loadProjectsFromDatabase();
        createView();
    }

    private void loadProjectsFromDatabase() {
        projects.clear();
        projects.addAll(projectService.getAllProjects());
    }

    private void createView() {
        // Main content container
        VBox contentContainer = new VBox(20);
        contentContainer.setPadding(new Insets(30));
        contentContainer.setStyle("-fx-background-color: #1e1e2e;");

        // Header
        Label title = new Label("📅 Project Calendar & Reminders");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#f5e0dc"));

        // Calendar navigation
        HBox navBox = new HBox(15);
        navBox.setAlignment(Pos.CENTER);

        Button prevBtn = new Button("◀ Previous");
        styleNavButton(prevBtn);
        prevBtn.setOnAction(e -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            updateCalendar();
        });

        monthYearLabel = new Label();
        monthYearLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        monthYearLabel.setTextFill(Color.web("#cdd6f4"));

        Button nextBtn = new Button("Next ▶");
        styleNavButton(nextBtn);
        nextBtn.setOnAction(e -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            updateCalendar();
        });

        Button todayBtn = new Button("Today");
        styleNavButton(todayBtn);
        todayBtn.setOnAction(e -> {
            currentYearMonth = YearMonth.now();
            updateCalendar();
        });

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, javafx.scene.layout.Priority.ALWAYS);
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, javafx.scene.layout.Priority.ALWAYS);

        navBox.getChildren().addAll(prevBtn, spacer1, monthYearLabel, spacer2, nextBtn, todayBtn);

        // Calendar grid
        calendarGrid = new GridPane();
        calendarGrid.setHgap(5);
        calendarGrid.setVgap(5);
        calendarGrid.setPadding(new Insets(10));
        calendarGrid.setStyle("-fx-background-color: #313244; -fx-background-radius: 10;");

        updateCalendar();

        // Add project button
        Button addProjectBtn = new Button("➕ Add Project");
        addProjectBtn.setStyle("-fx-background-color: #a6e3a1; -fx-text-fill: #1e1e2e; " +
                "-fx-padding: 10 20; -fx-cursor: hand; -fx-font-weight: bold; " +
                "-fx-background-radius: 6;");
        addProjectBtn.setOnAction(e -> showAddProjectDialog());

        // Project list
        Label projectsTitle = new Label("📋 Upcoming Projects");
        projectsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        projectsTitle.setTextFill(Color.web("#f5e0dc"));

        VBox projectList = createProjectList();

        // Make project list scrollable if too many items
        ScrollPane projectScrollPane = new ScrollPane(projectList);
        projectScrollPane.setFitToWidth(true);
        projectScrollPane.setPrefHeight(300);
        projectScrollPane.setStyle("-fx-background: #1e1e2e; -fx-background-color: #1e1e2e; -fx-border-color: #313244; -fx-border-radius: 10;");
        projectScrollPane.getStyleClass().add("scroll-pane");

        HBox headerBox = new HBox(15);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, javafx.scene.layout.Priority.ALWAYS);
        headerBox.getChildren().addAll(title, headerSpacer, addProjectBtn);

        // Add all components to content container
        contentContainer.getChildren().addAll(headerBox, navBox, calendarGrid, projectsTitle, projectScrollPane);

        // Create main scroll pane that wraps everything
        mainScrollPane = new ScrollPane(contentContainer);
        mainScrollPane.setFitToWidth(true);
        mainScrollPane.setStyle("-fx-background: #1e1e2e; -fx-background-color: #1e1e2e;");
        mainScrollPane.getStyleClass().add("main-scroll-pane");

        // Set the view to the scroll pane
        view = new VBox(mainScrollPane);
        VBox.setVgrow(mainScrollPane, javafx.scene.layout.Priority.ALWAYS);
    }

    private void updateCalendar() {
        calendarGrid.getChildren().clear();

        monthYearLabel.setText(currentYearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) +
                " " + currentYearMonth.getYear());

        // Add day headers
        String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = 0; i < 7; i++) {
            Label dayLabel = new Label(dayNames[i]);
            dayLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            dayLabel.setTextFill(Color.web("#bac2de"));
            dayLabel.setAlignment(Pos.CENTER);
            dayLabel.setPrefWidth(100);
            calendarGrid.add(dayLabel, i, 0);
        }

        // Get first day of month and number of days
        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7; // 0 = Sunday
        int daysInMonth = currentYearMonth.lengthOfMonth();

        // Add calendar cells
        int dayCounter = 1;
        for (int row = 1; row <= 6 && dayCounter <= daysInMonth; row++) {
            for (int col = 0; col < 7; col++) {
                if (row == 1 && col < dayOfWeek) {
                    continue;
                }

                if (dayCounter > daysInMonth) {
                    break;
                }

                LocalDate date = currentYearMonth.atDay(dayCounter);
                VBox dayCell = createDayCell(date);
                calendarGrid.add(dayCell, col, row);
                dayCounter++;
            }
        }
    }

    private VBox createDayCell(LocalDate date) {
        VBox cell = new VBox(3);
        cell.setPrefSize(100, 80);
        cell.setPadding(new Insets(5));
        cell.setAlignment(Pos.TOP_LEFT);

        boolean isToday = date.equals(LocalDate.now());
        if (isToday) {
            cell.setStyle("-fx-background-color: #45475a; -fx-border-color: #89b4fa; " +
                    "-fx-border-width: 2; -fx-background-radius: 5; -fx-border-radius: 5;");
        } else {
            cell.setStyle("-fx-background-color: #1e1e2e; -fx-border-color: #45475a; " +
                    "-fx-border-width: 1; -fx-background-radius: 5; -fx-border-radius: 5;");
        }

        Label dayLabel = new Label(String.valueOf(date.getDayOfMonth()));
        dayLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        dayLabel.setTextFill(isToday ? Color.web("#89b4fa") : Color.web("#cdd6f4"));

        cell.getChildren().add(dayLabel);

        // Add project indicators
        List<Project> dayProjects = getProjectsForDate(date);
        for (Project project : dayProjects) {
            Label projectIndicator = new Label("• " + truncate(project.getTitle(), 12));
            projectIndicator.setFont(Font.font("Arial", 9));
            projectIndicator.setTextFill(Color.web(project.getColor()));
            projectIndicator.setWrapText(true);
            cell.getChildren().add(projectIndicator);
        }

        return cell;
    }

    private String truncate(String text, int length) {
        return text.length() > length ? text.substring(0, length) + "..." : text;
    }

    private List<Project> getProjectsForDate(LocalDate date) {
        return projectService.getProjectsForDate(date);
    }

    private VBox createProjectList() {
        VBox listContainer = new VBox(10);
        listContainer.setPadding(new Insets(5));

        List<Project> sortedProjects = projects.stream()
                .sorted((p1, p2) -> p1.getDueDate().compareTo(p2.getDueDate()))
                .collect(Collectors.toList());

        for (Project project : sortedProjects) {
            listContainer.getChildren().add(createProjectCard(project));
        }

        if (sortedProjects.isEmpty()) {
            Label emptyLabel = new Label("No projects scheduled yet. Click 'Add Project' to get started!");
            emptyLabel.setTextFill(Color.web("#bac2de"));
            emptyLabel.setFont(Font.font("Arial", 13));
            emptyLabel.setPadding(new Insets(20));
            listContainer.getChildren().add(emptyLabel);
        }

        return listContainer;
    }

    private VBox createProjectCard(Project project) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #313244; -fx-background-radius: 10;");

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(project.getTitle());
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.web("#cdd6f4"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Label dateLabel = new Label("📅 " + project.getDueDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        dateLabel.setFont(Font.font("Arial", 12));
        dateLabel.setTextFill(Color.web(project.getColor()));

        long daysUntil = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), project.getDueDate());
        Label daysLabel = new Label(daysUntil + " days left");
        daysLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        daysLabel.setTextFill(daysUntil <= 3 ? Color.web("#f38ba8") : Color.web("#a6e3a1"));

        header.getChildren().addAll(titleLabel, spacer, dateLabel, daysLabel);

        Label descLabel = new Label(project.getDescription());
        descLabel.setFont(Font.font("Arial", 12));
        descLabel.setTextFill(Color.web("#bac2de"));
        descLabel.setWrapText(true);

        // Progress bar
        HBox progressBox = new HBox(10);
        progressBox.setAlignment(Pos.CENTER_LEFT);

        ProgressBar progressBar = new ProgressBar(project.getProgress() / 100.0);
        progressBar.setPrefWidth(200);
        progressBar.setStyle("-fx-accent: " + project.getColor() + ";");

        Label progressLabel = new Label(project.getProgress() + "%");
        progressLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        progressLabel.setTextFill(Color.web("#cdd6f4"));

        Slider progressSlider = new Slider(0, 100, project.getProgress());
        progressSlider.setShowTickLabels(false);
        progressSlider.setShowTickMarks(false);
        progressSlider.setPrefWidth(150);
        progressSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            project.setProgress(newVal.intValue());
            progressBar.setProgress(newVal.intValue() / 100.0);
            progressLabel.setText(newVal.intValue() + "%");
            projectService.updateProject(project);
        });

        progressBox.getChildren().addAll(new Label("Progress: "), progressBar, progressSlider, progressLabel);

        Button deleteBtn = new Button("🗑️ Delete");
        deleteBtn.setStyle("-fx-background-color: #f38ba8; -fx-text-fill: white; -fx-cursor: hand; " +
                "-fx-padding: 5 15; -fx-background-radius: 4;");
        deleteBtn.setOnAction(e -> {
            if (projectService.deleteProject(project.getId())) {
                projects.remove(project);
                updateCalendar();
                // Refresh the project list
                VBox newProjectList = createProjectList();
                ScrollPane projectScrollPane = new ScrollPane(newProjectList);
                projectScrollPane.setFitToWidth(true);
                projectScrollPane.setPrefHeight(300);
                projectScrollPane.setStyle("-fx-background: #1e1e2e; -fx-background-color: #1e1e2e; -fx-border-color: #313244; -fx-border-radius: 10;");
                projectScrollPane.getStyleClass().add("scroll-pane");

                // Replace the old project list in the view
                VBox contentContainer = (VBox) mainScrollPane.getContent();
                contentContainer.getChildren().set(4, projectScrollPane);
            } else {
                showAlert("Error", "Failed to delete project.");
            }
        });

        card.getChildren().addAll(header, descLabel, progressBox, deleteBtn);
        return card;
    }

    private void showAddProjectDialog() {
        Dialog<Project> dialog = new Dialog<>();
        dialog.setTitle("Add New Project");
        dialog.setHeaderText("Schedule a new project");

        ButtonType addButtonType = new ButtonType("Add Project", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField titleField = new TextField();
        titleField.setPromptText("Project title");

        TextArea descField = new TextArea();
        descField.setPromptText("Description");
        descField.setPrefRowCount(3);

        DatePicker dueDatePicker = new DatePicker(LocalDate.now().plusDays(7));

        ComboBox<String> colorCombo = new ComboBox<>();
        colorCombo.getItems().addAll("#89b4fa", "#f9e2af", "#a6e3a1", "#f38ba8", "#cba6f7");
        colorCombo.setValue("#89b4fa");

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new Label("Due Date:"), 0, 2);
        grid.add(dueDatePicker, 1, 2);
        grid.add(new Label("Color:"), 0, 3);
        grid.add(colorCombo, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new Project(
                        titleField.getText(),
                        descField.getText(),
                        dueDatePicker.getValue(),
                        colorCombo.getValue()
                );
            }
            return null;
        });

        dialog.showAndWait().ifPresent(project -> {
            if (projectService.addProject(project)) {
                projects.add(project);
                updateCalendar();
                // Refresh the project list
                VBox newProjectList = createProjectList();
                ScrollPane projectScrollPane = new ScrollPane(newProjectList);
                projectScrollPane.setFitToWidth(true);
                projectScrollPane.setPrefHeight(300);
                projectScrollPane.setStyle("-fx-background: #1e1e2e; -fx-background-color: #1e1e2e; -fx-border-color: #313244; -fx-border-radius: 10;");
                projectScrollPane.getStyleClass().add("scroll-pane");

                // Replace the old project list in the view
                VBox contentContainer = (VBox) mainScrollPane.getContent();
                contentContainer.getChildren().set(4, projectScrollPane);
            } else {
                showAlert("Error", "Failed to add project to database.");
            }
        });
    }

    private void styleNavButton(Button btn) {
        btn.setStyle("-fx-background-color: #45475a; -fx-text-fill: #cdd6f4; " +
                "-fx-padding: 8 15; -fx-cursor: hand; -fx-background-radius: 6;");
    }

    public List<Project> getUpcomingProjects(int days) {
        return projectService.getUpcomingProjects(days);
    }

    public VBox getView() {
        return view;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}