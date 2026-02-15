// TaskManager.java (updated)
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import models.Task;
import models.Priority;
import services.TaskService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class TaskManager {
    private ObservableList<Task> tasks;
    private VBox view;
    private ListView<Task> taskListView;
    private TaskService taskService;

    public TaskManager() {
        taskService = new TaskService();
        tasks = FXCollections.observableArrayList();
        loadTasksFromDatabase();
        createView();
    }

    private void loadTasksFromDatabase() {
        tasks.clear();
        tasks.addAll(taskService.getAllTasks());
    }

    private void createView() {
        view = new VBox(20);
        view.setPadding(new Insets(30));

        // Header with add button
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("📋 Task Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#f5e0dc"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Button addTaskBtn = new Button("➕ Add New Task");
        addTaskBtn.setStyle("-fx-background-color: #a6e3a1; -fx-text-fill: #1e1e2e; " +
                "-fx-padding: 10 20; -fx-cursor: hand; -fx-font-weight: bold; " +
                "-fx-background-radius: 6;");
        addTaskBtn.setOnAction(e -> showAddTaskDialog());

        header.getChildren().addAll(title, spacer, addTaskBtn);

        // Filter buttons
        HBox filterBox = new HBox(10);
        ToggleGroup filterGroup = new ToggleGroup();

        RadioButton allFilter = new RadioButton("All Tasks");
        RadioButton todayFilter = new RadioButton("Today");
        RadioButton activeFilter = new RadioButton("Active");
        RadioButton completedFilter = new RadioButton("Completed");

        allFilter.setToggleGroup(filterGroup);
        todayFilter.setToggleGroup(filterGroup);
        activeFilter.setToggleGroup(filterGroup);
        completedFilter.setToggleGroup(filterGroup);

        allFilter.setSelected(true);

        styleRadioButton(allFilter);
        styleRadioButton(todayFilter);
        styleRadioButton(activeFilter);
        styleRadioButton(completedFilter);

        allFilter.setOnAction(e -> refreshTaskList(null));
        todayFilter.setOnAction(e -> refreshTaskList("today"));
        activeFilter.setOnAction(e -> refreshTaskList("active"));
        completedFilter.setOnAction(e -> refreshTaskList("completed"));

        filterBox.getChildren().addAll(allFilter, todayFilter, activeFilter, completedFilter);

        // Task list
        taskListView = new ListView<>();
        taskListView.setCellFactory(lv -> new TaskListCell());
        taskListView.setItems(tasks);
        taskListView.setStyle("-fx-background-color: #1e1e2e; -fx-border-color: #313244;");
        VBox.setVgrow(taskListView, javafx.scene.layout.Priority.ALWAYS);

        view.getChildren().addAll(header, filterBox, taskListView);
    }

    private void styleRadioButton(RadioButton rb) {
        rb.setTextFill(Color.web("#cdd6f4"));
        rb.setStyle("-fx-font-size: 13px;");
    }

    private void refreshTaskList(String filter) {
        if (filter == null) {
            taskListView.setItems(tasks);
        } else if (filter.equals("today")) {
            ObservableList<Task> todayTasks = FXCollections.observableArrayList(
                    tasks.stream().filter(Task::isDueToday).collect(Collectors.toList())
            );
            taskListView.setItems(todayTasks);
        } else if (filter.equals("active")) {
            ObservableList<Task> activeTasks = FXCollections.observableArrayList(
                    tasks.stream().filter(t -> !t.isCompleted()).collect(Collectors.toList())
            );
            taskListView.setItems(activeTasks);
        } else if (filter.equals("completed")) {
            ObservableList<Task> completedTasks = FXCollections.observableArrayList(
                    tasks.stream().filter(Task::isCompleted).collect(Collectors.toList())
            );
            taskListView.setItems(completedTasks);
        }
    }

    private void showAddTaskDialog() {
        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("Add New Task");
        dialog.setHeaderText("Create a new task");

        ButtonType addButtonType = new ButtonType("Add Task", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField titleField = new TextField();
        titleField.setPromptText("Task title");

        TextArea descField = new TextArea();
        descField.setPromptText("Description");
        descField.setPrefRowCount(3);

        ComboBox<Priority> priorityCombo = new ComboBox<>();
        priorityCombo.getItems().addAll(Priority.HIGH, Priority.MEDIUM, Priority.LOW);
        priorityCombo.setValue(Priority.MEDIUM);

        DatePicker dueDatePicker = new DatePicker(LocalDate.now());

        TextField categoryField = new TextField();
        categoryField.setPromptText("Category (e.g., LeetCode, Project)");

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new Label("Priority:"), 0, 2);
        grid.add(priorityCombo, 1, 2);
        grid.add(new Label("Due Date:"), 0, 3);
        grid.add(dueDatePicker, 1, 3);
        grid.add(new Label("Category:"), 0, 4);
        grid.add(categoryField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new Task(
                        titleField.getText(),
                        descField.getText(),
                        priorityCombo.getValue(),
                        dueDatePicker.getValue(),
                        categoryField.getText()
                );
            }
            return null;
        });

        dialog.showAndWait().ifPresent(task -> {
            if (taskService.addTask(task)) {
                tasks.add(task);
                taskListView.refresh();
            } else {
                showAlert("Error", "Failed to add task to database.");
            }
        });
    }

    public void saveTask(Task task) {
        taskService.updateTask(task);
        taskListView.refresh();
    }

    public List<Task> getTodayTasks() {
        return taskService.getTasksByDate(LocalDate.now());
    }

    public int getCompletedToday() {
        return taskService.getCompletedToday();
    }

    public int getActiveTasks() {
        return taskService.getActiveCount();
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

    // Custom cell for task list
    private class TaskListCell extends ListCell<Task> {
        @Override
        protected void updateItem(Task task, boolean empty) {
            super.updateItem(task, empty);

            if (empty || task == null) {
                setGraphic(null);
                setStyle("-fx-background-color: transparent;");
            } else {
                HBox cell = new HBox(15);
                cell.setPadding(new Insets(12));
                cell.setAlignment(Pos.CENTER_LEFT);
                cell.setStyle("-fx-background-color: #313244; -fx-background-radius: 8;");

                CheckBox checkBox = new CheckBox();
                checkBox.setSelected(task.isCompleted());
                checkBox.setOnAction(e -> {
                    task.setCompleted(checkBox.isSelected());
                    taskService.updateTask(task);
                    updateItem(task, false);
                });

                VBox taskInfo = new VBox(5);

                Label titleLabel = new Label(task.getTitle());
                titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                titleLabel.setTextFill(Color.web("#cdd6f4"));
                if (task.isCompleted()) {
                    titleLabel.setStyle("-fx-strikethrough: true; -fx-text-fill: #6c7086;");
                }

                Label descLabel = new Label(task.getDescription());
                descLabel.setFont(Font.font("Arial", 11));
                descLabel.setTextFill(Color.web("#bac2de"));
                descLabel.setWrapText(true);

                HBox metaInfo = new HBox(10);

                Label categoryLabel = new Label("📁 " + task.getCategory());
                categoryLabel.setFont(Font.font("Arial", 10));
                categoryLabel.setTextFill(Color.web("#89b4fa"));

                Label dueDateLabel = new Label("📅 " + task.getDueDate().toString());
                dueDateLabel.setFont(Font.font("Arial", 10));
                dueDateLabel.setTextFill(task.isOverdue() ? Color.web("#f38ba8") : Color.web("#a6e3a1"));

                metaInfo.getChildren().addAll(categoryLabel, dueDateLabel);

                taskInfo.getChildren().addAll(titleLabel, descLabel, metaInfo);
                HBox.setHgrow(taskInfo, javafx.scene.layout.Priority.ALWAYS);

                VBox priorityBox = new VBox(5);
                priorityBox.setAlignment(Pos.CENTER);

                Label priorityLabel = new Label(task.getPriority().toString());
                priorityLabel.setStyle("-fx-background-color: " + getPriorityColor(task.getPriority()) +
                        "; -fx-padding: 5 10; -fx-background-radius: 4; -fx-text-fill: #1e1e2e;");
                priorityLabel.setFont(Font.font("Arial", FontWeight.BOLD, 10));

                Button deleteBtn = new Button("🗑️");
                deleteBtn.setStyle("-fx-background-color: #f38ba8; -fx-text-fill: white; " +
                        "-fx-cursor: hand; -fx-background-radius: 4; -fx-padding: 5 10;");
                deleteBtn.setOnAction(e -> {
                    if (taskService.deleteTask(task.getId())) {
                        tasks.remove(task);
                        taskListView.refresh();
                    } else {
                        showAlert("Error", "Failed to delete task.");
                    }
                });

                priorityBox.getChildren().addAll(priorityLabel, deleteBtn);

                cell.getChildren().addAll(checkBox, taskInfo, priorityBox);
                setGraphic(cell);
                setStyle("-fx-background-color: transparent; -fx-padding: 5;");
            }
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
    }
}