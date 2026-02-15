import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import models.LeetCodeProblem;
import services.LeetCodeService;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LeetCodeIntegration {
    private VBox view;
    private ObservableList<LeetCodeProblem> problems;
    private ListView<LeetCodeProblem> problemListView;
    private LeetCodeService leetCodeService;

    public LeetCodeIntegration() {
        leetCodeService = new LeetCodeService();
        problems = FXCollections.observableArrayList();
        loadProblemsFromDatabase();
        createView();
    }

    private void loadProblemsFromDatabase() {
        problems.clear();
        problems.addAll(leetCodeService.getAllProblems());
    }

    private void createView() {
        view = new VBox(20);
        view.setPadding(new Insets(30));

        // Header
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("💻 LeetCode Integration");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#f5e0dc"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Button addProblemBtn = new Button("➕ Add Problem");
        addProblemBtn.setStyle("-fx-background-color: #a6e3a1; -fx-text-fill: #1e1e2e; " +
                "-fx-padding: 10 20; -fx-cursor: hand; -fx-font-weight: bold; " +
                "-fx-background-radius: 6;");
        addProblemBtn.setOnAction(e -> showAddProblemDialog());

        header.getChildren().addAll(title, spacer, addProblemBtn);

        // Stats section
        HBox statsBox = new HBox(20);
        statsBox.getChildren().addAll(
                createStatCard("Total Attempted", String.valueOf(problems.size()), "#89b4fa"),
                createStatCard("Solved", String.valueOf(leetCodeService.getSolvedCount()), "#a6e3a1"),
                createStatCard("Easy", String.valueOf(leetCodeService.getCountByDifficulty("Easy")), "#a6e3a1"),
                createStatCard("Medium", String.valueOf(leetCodeService.getCountByDifficulty("Medium")), "#f9e2af"),
                createStatCard("Hard", String.valueOf(leetCodeService.getCountByDifficulty("Hard")), "#f38ba8")
        );

        // Filter section
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);

        Label filterLabel = new Label("Filter by:");
        filterLabel.setTextFill(Color.web("#cdd6f4"));
        filterLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        ComboBox<String> difficultyFilter = new ComboBox<>();
        difficultyFilter.getItems().addAll("All", "Easy", "Medium", "Hard");
        difficultyFilter.setValue("All");
        difficultyFilter.setStyle("-fx-background-color: #313244; -fx-text-fill: #cdd6f4;");

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All", "Solved", "Unsolved");
        statusFilter.setValue("All");
        statusFilter.setStyle("-fx-background-color: #313244; -fx-text-fill: #cdd6f4;");

        difficultyFilter.setOnAction(e -> applyFilters(difficultyFilter.getValue(), statusFilter.getValue()));
        statusFilter.setOnAction(e -> applyFilters(difficultyFilter.getValue(), statusFilter.getValue()));

        filterBox.getChildren().addAll(filterLabel,
                new Label("Difficulty:"), difficultyFilter,
                new Label("Status:"), statusFilter);

        // Problem list
        problemListView = new ListView<>();
        problemListView.setCellFactory(lv -> new ProblemListCell());
        problemListView.setItems(problems);
        problemListView.setStyle("-fx-background-color: #1e1e2e; -fx-border-color: #313244;");
        VBox.setVgrow(problemListView, javafx.scene.layout.Priority.ALWAYS);

        view.getChildren().addAll(header, statsBox, filterBox, problemListView);
    }

    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: #313244; -fx-padding: 15; -fx-background-radius: 8;");
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(150);

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        valueLabel.setTextFill(Color.web(color));

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", 11));
        titleLabel.setTextFill(Color.web("#bac2de"));
        titleLabel.setWrapText(true);
        titleLabel.setAlignment(Pos.CENTER);

        card.getChildren().addAll(valueLabel, titleLabel);
        return card;
    }

    private void applyFilters(String difficulty, String status) {
        List<LeetCodeProblem> filtered = new ArrayList<>(problems);

        if (!difficulty.equals("All")) {
            filtered.removeIf(p -> !p.getDifficulty().equals(difficulty));
        }

        if (status.equals("Solved")) {
            filtered.removeIf(p -> !p.isSolved());
        } else if (status.equals("Unsolved")) {
            filtered.removeIf(LeetCodeProblem::isSolved);
        }

        problemListView.setItems(FXCollections.observableArrayList(filtered));
    }

    private void showAddProblemDialog() {
        Dialog<LeetCodeProblem> dialog = new Dialog<>();
        dialog.setTitle("Add LeetCode Problem");
        dialog.setHeaderText("Track a new LeetCode problem");

        ButtonType addButtonType = new ButtonType("Add Problem", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField titleField = new TextField();
        titleField.setPromptText("Problem title");

        TextField tagsField = new TextField();
        tagsField.setPromptText("Tags (comma separated)");

        ComboBox<String> difficultyCombo = new ComboBox<>();
        difficultyCombo.getItems().addAll("Easy", "Medium", "Hard");
        difficultyCombo.setValue("Easy");

        TextField urlField = new TextField();
        urlField.setPromptText("LeetCode URL");

        CheckBox solvedCheck = new CheckBox("Already solved?");

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Tags:"), 0, 1);
        grid.add(tagsField, 1, 1);
        grid.add(new Label("Difficulty:"), 0, 2);
        grid.add(difficultyCombo, 1, 2);
        grid.add(new Label("URL:"), 0, 3);
        grid.add(urlField, 1, 3);
        grid.add(solvedCheck, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new LeetCodeProblem(
                        titleField.getText(),
                        tagsField.getText(),
                        difficultyCombo.getValue(),
                        solvedCheck.isSelected(),
                        urlField.getText()
                );
            }
            return null;
        });

        dialog.showAndWait().ifPresent(problem -> {
            if (leetCodeService.addProblem(problem)) {
                problems.add(problem);
                problemListView.refresh();
            } else {
                showAlert("Error", "Failed to add problem to database.");
            }
        });
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

    private void openUrl(String url) {
        try {
            // Try Java AWT Desktop first (works on most systems)
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
                if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
                    desktop.browse(new java.net.URI(url));
                    return;
                }
            }

            // Windows fallback
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                Runtime.getRuntime().exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", url});
            }
            // Mac fallback
            else if (os.contains("mac")) {
                Runtime.getRuntime().exec(new String[]{"open", url});
            }
            // Linux fallback
            else if (os.contains("nix") || os.contains("nux")) {
                Runtime.getRuntime().exec(new String[]{"xdg-open", url});
            }
            else {
                showAlert("Error", "Cannot open browser on this operating system.\nPlease visit:\n" + url);
            }

        } catch (Exception e) {
            System.err.println("Error opening URL: " + e.getMessage());
            showAlert("Error", "Could not open URL. Please visit:\n" + url);
        }
    }

    // Custom cell for problem list
    private class ProblemListCell extends ListCell<LeetCodeProblem> {
        @Override
        protected void updateItem(LeetCodeProblem problem, boolean empty) {
            super.updateItem(problem, empty);

            if (empty || problem == null) {
                setGraphic(null);
                setStyle("-fx-background-color: transparent;");
            } else {
                VBox cell = new VBox(8);
                cell.setPadding(new Insets(12));
                cell.setStyle("-fx-background-color: #313244; -fx-background-radius: 8;");

                HBox topRow = new HBox(10);
                topRow.setAlignment(Pos.CENTER_LEFT);

                CheckBox solvedCheck = new CheckBox();
                solvedCheck.setSelected(problem.isSolved());
                solvedCheck.setOnAction(e -> {
                    problem.setSolved(solvedCheck.isSelected());
                    leetCodeService.updateProblem(problem);
                    updateItem(problem, false);
                });

                Label titleLabel = new Label(problem.getTitle());
                titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                titleLabel.setTextFill(Color.web("#cdd6f4"));
                if (problem.isSolved()) {
                    titleLabel.setStyle("-fx-strikethrough: true;");
                }

                Region spacer = new Region();
                HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

                Label difficultyLabel = new Label(problem.getDifficulty());
                difficultyLabel.setStyle("-fx-background-color: " + getDifficultyColor(problem.getDifficulty()) +
                        "; -fx-padding: 4 10; -fx-background-radius: 4; -fx-text-fill: #1e1e2e;");
                difficultyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));

                topRow.getChildren().addAll(solvedCheck, titleLabel, spacer, difficultyLabel);

                Label tagsLabel = new Label("🏷️ " + problem.getTags());
                tagsLabel.setFont(Font.font("Arial", 11));
                tagsLabel.setTextFill(Color.web("#bac2de"));

                HBox bottomRow = new HBox(15);
                bottomRow.setAlignment(Pos.CENTER_LEFT);

                if (problem.getSolvedAt() != null) {
                    Label solvedAtLabel = new Label("✓ Solved: " +
                            problem.getSolvedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
                    solvedAtLabel.setFont(Font.font("Arial", 10));
                    solvedAtLabel.setTextFill(Color.web("#a6e3a1"));
                    bottomRow.getChildren().add(solvedAtLabel);
                }

                // FIXED: Open button now actually opens the browser
                Button openBtn = new Button("🔗 Open");
                openBtn.setStyle("-fx-background-color: #89b4fa; -fx-text-fill: white; " +
                        "-fx-cursor: hand; -fx-padding: 4 10; -fx-background-radius: 4; " +
                        "-fx-font-size: 11px;");
                openBtn.setOnAction(e -> openUrl(problem.getUrl()));

                Button deleteBtn = new Button("🗑️");
                deleteBtn.setStyle("-fx-background-color: #f38ba8; -fx-text-fill: white; " +
                        "-fx-cursor: hand; -fx-padding: 4 10; -fx-background-radius: 4;");
                deleteBtn.setOnAction(e -> {
                    if (leetCodeService.deleteProblem(problem.getId())) {
                        problems.remove(problem);
                        problemListView.refresh();
                    } else {
                        showAlert("Error", "Failed to delete problem.");
                    }
                });

                Region bottomSpacer = new Region();
                HBox.setHgrow(bottomSpacer, javafx.scene.layout.Priority.ALWAYS);
                bottomRow.getChildren().addAll(bottomSpacer, openBtn, deleteBtn);

                cell.getChildren().addAll(topRow, tagsLabel, bottomRow);
                setGraphic(cell);
                setStyle("-fx-background-color: transparent; -fx-padding: 5;");
            }
        }

        private String getDifficultyColor(String difficulty) {
            if (difficulty.equals("Easy")) {
                return "#a6e3a1";
            } else if (difficulty.equals("Medium")) {
                return "#f9e2af";
            } else if (difficulty.equals("Hard")) {
                return "#f38ba8";
            } else {
                return "#bac2de";
            }
        }
    }
}