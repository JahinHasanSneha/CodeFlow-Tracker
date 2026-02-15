// LeetCodeProblem.java (updated)
package models;

import java.time.LocalDateTime;
import java.util.UUID;

public class LeetCodeProblem {
    private String id;
    private String title;
    private String tags;
    private String difficulty;
    private boolean solved;
    private String url;
    private LocalDateTime solvedAt;

    public LeetCodeProblem() {
        this.id = UUID.randomUUID().toString();
    }

    public LeetCodeProblem(String title, String tags, String difficulty, boolean solved, String url) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.tags = tags;
        this.difficulty = difficulty;
        this.solved = solved;
        this.url = url;
        if (solved) {
            this.solvedAt = LocalDateTime.now();
        }
    }

    public LeetCodeProblem(String id, String title, String tags, String difficulty,
                           boolean solved, String url, LocalDateTime solvedAt) {
        this.id = id;
        this.title = title;
        this.tags = tags;
        this.difficulty = difficulty;
        this.solved = solved;
        this.url = url;
        this.solvedAt = solvedAt;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getTags() { return tags; }
    public String getDifficulty() { return difficulty; }
    public boolean isSolved() { return solved; }
    public String getUrl() { return url; }
    public LocalDateTime getSolvedAt() { return solvedAt; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setTags(String tags) { this.tags = tags; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public void setUrl(String url) { this.url = url; }

    public void setSolved(boolean solved) {
        this.solved = solved;
        if (solved && solvedAt == null) {
            this.solvedAt = LocalDateTime.now();
        } else if (!solved) {
            this.solvedAt = null;
        }
    }
}