// ProgressTracker.java (updated)
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import services.ProgressService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ProgressTracker {
    private VBox view;
    private Map<LocalDate, Integer> dailyProgress;
    private int totalSolved;
    private int currentStreak;
    private int weekProgress;
    private double averagePerDay;
    private ProgressService progressService;

    public ProgressTracker() {
        progressService = new ProgressService();
        dailyProgress = new HashMap<>();
        loadProgressFromDatabase();
        createView();
    }

    private void loadProgressFromDatabase() {
        dailyProgress = progressService.getDailyProgress(30); // Last 30 days
        totalSolved = progressService.getTotalSolved();
        currentStreak = progressService.getCurrentStreak();
        weekProgress = progressService.getWeekProgress();
        averagePerDay = progressService.getAveragePerDay();
    }

    private void createView() {
        view = new VBox(20);
        view.setPadding(new Insets(30));

        Label title = new Label("📈 Coding Progress Tracker");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#f5e0dc"));

        // Stats cards
        HBox statsBox = new HBox(20);
        statsBox.getChildren().addAll(
                createStatCard("Current Streak", currentStreak + " days", "#a6e3a1"),
                createStatCard("Total Solved", String.valueOf(totalSolved), "#cba6f7"),
                createStatCard("This Week", String.valueOf(weekProgress), "#89b4fa"),
                createStatCard("Average/Day", String.format("%.1f", averagePerDay), "#f9e2af")
        );

        // Progress chart
        VBox chartBox = createProgressChart();

        // Activity heatmap
        VBox heatmapBox = createActivityHeatmap();

        // Add entry button
        Button addEntryBtn = new Button("➕ Log Today's Progress");
        addEntryBtn.setStyle("-fx-background-color: #a6e3a1; -fx-text-fill: #1e1e2e; " +
                "-fx-padding: 12 24; -fx-cursor: hand; -fx-font-weight: bold; " +
                "-fx-background-radius: 6; -fx-font-size: 14px;");
        addEntryBtn.setOnAction(e -> showAddProgressDialog());

        view.getChildren().addAll(title, statsBox, addEntryBtn, chartBox, heatmapBox);
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

    private VBox createProgressChart() {
        VBox container = new VBox(10);

        Label header = new Label("📊 Problems Solved Over Time");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        header.setTextFill(Color.web("#f5e0dc"));

        // Create line chart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel("Problems Solved");

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Daily Progress");
        lineChart.setLegendVisible(false);
        lineChart.setPrefHeight(300);

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        List<LocalDate> sortedDates = new ArrayList<>(dailyProgress.keySet());
        Collections.sort(sortedDates);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");
        for (LocalDate date : sortedDates) {
            series.getData().add(new XYChart.Data<>(
                    date.format(formatter),
                    dailyProgress.get(date)
            ));
        }

        lineChart.getData().add(series);

        // Style the chart
        lineChart.setStyle("-fx-background-color: #313244; -fx-background-radius: 10;");

        container.getChildren().addAll(header, lineChart);
        return container;
    }

    private VBox createActivityHeatmap() {
        VBox container = new VBox(10);

        Label header = new Label("🔥 Activity Heatmap (Last 14 Days)");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        header.setTextFill(Color.web("#f5e0dc"));

        GridPane heatmap = new GridPane();
        heatmap.setHgap(5);
        heatmap.setVgap(5);
        heatmap.setPadding(new Insets(15));
        heatmap.setStyle("-fx-background-color: #313244; -fx-background-radius: 10;");

        // Add day labels
        String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

        int dayCounter = 0;
        for (int i = 13; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            int problems = dailyProgress.getOrDefault(date, 0);

            VBox dayCell = new VBox(3);
            dayCell.setPrefSize(60, 60);
            dayCell.setAlignment(Pos.CENTER);
            dayCell.setStyle("-fx-background-color: " + getHeatmapColor(problems) +
                    "; -fx-background-radius: 6; -fx-border-color: #45475a; " +
                    "-fx-border-width: 1; -fx-border-radius: 6;");

            Label dayLabel = new Label(dayNames[date.getDayOfWeek().getValue() % 7]);
            dayLabel.setFont(Font.font("Arial", FontWeight.BOLD, 10));
            dayLabel.setTextFill(Color.web("#cdd6f4"));

            Label dateLabel = new Label(date.format(DateTimeFormatter.ofPattern("MMM dd")));
            dateLabel.setFont(Font.font("Arial", 8));
            dateLabel.setTextFill(Color.web("#bac2de"));

            Label countLabel = new Label(problems + " solved");
            countLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
            countLabel.setTextFill(Color.web("#f5e0dc"));

            dayCell.getChildren().addAll(dayLabel, dateLabel, countLabel);

            int col = dayCounter % 7;
            int row = dayCounter / 7;
            heatmap.add(dayCell, col, row);

            dayCounter++;
        }

        // Legend
        HBox legend = new HBox(10);
        legend.setAlignment(Pos.CENTER);
        legend.setPadding(new Insets(10, 0, 0, 0));

        Label legendLabel = new Label("Less");
        legendLabel.setTextFill(Color.web("#bac2de"));
        legendLabel.setFont(Font.font("Arial", 11));

        for (int i = 0; i <= 5; i++) {
            StackPane colorBox = new StackPane();
            colorBox.setPrefSize(20, 20);
            colorBox.setStyle("-fx-background-color: " + getHeatmapColor(i) + "; -fx-background-radius: 3;");
            legend.getChildren().add(colorBox);
        }

        Label legendLabel2 = new Label("More");
        legendLabel2.setTextFill(Color.web("#bac2de"));
        legendLabel2.setFont(Font.font("Arial", 11));

        legend.getChildren().add(0, legendLabel);
        legend.getChildren().add(legendLabel2);

        container.getChildren().addAll(header, heatmap, legend);
        return container;
    }

    private String getHeatmapColor(int count) {
        if (count == 0) return "#1e1e2e";
        if (count == 1) return "#45475a";
        if (count == 2) return "#585b70";
        if (count == 3) return "#a6e3a1";
        if (count == 4) return "#94e2d5";
        return "#89b4fa";
    }

    private void showAddProgressDialog() {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Log Progress");
        dialog.setHeaderText("How many problems did you solve today?");

        ButtonType addButtonType = new ButtonType("Log Progress", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Spinner<Integer> problemsSpinner = new Spinner<>(0, 20,
                dailyProgress.getOrDefault(LocalDate.now(), 0));
        problemsSpinner.setEditable(true);
        problemsSpinner.setPrefWidth(200);

        Label currentLabel = new Label("Current for today: " +
                dailyProgress.getOrDefault(LocalDate.now(), 0));
        currentLabel.setFont(Font.font("Arial", 12));

        content.getChildren().addAll(
                new Label("Number of problems:"),
                problemsSpinner,
                currentLabel
        );

        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return problemsSpinner.getValue();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(count -> {
            if (progressService.updateDailyProgress(LocalDate.now(), count)) {
                loadProgressFromDatabase();
                createView();
                view.getParent().requestLayout();
            } else {
                showAlert("Error", "Failed to update progress.");
            }
        });
    }

    public int getTotalSolved() {
        return totalSolved;
    }

    public int getCurrentStreak() {
        return currentStreak;
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