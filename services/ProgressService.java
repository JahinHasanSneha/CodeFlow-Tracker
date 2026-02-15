// ProgressService.java
package services;

import dao.ProgressDAO;

import java.time.LocalDate;
import java.util.Map;

public class ProgressService {
    private ProgressDAO progressDAO;

    public ProgressService() {
        this.progressDAO = new ProgressDAO();
    }

    public boolean updateDailyProgress(LocalDate date, int problemsSolved) {
        return progressDAO.updateDailyProgress(date, problemsSolved);
    }

    public Map<LocalDate, Integer> getDailyProgress(int days) {
        return progressDAO.getDailyProgress(days);
    }

    public int getTotalSolved() {
        return progressDAO.getTotalSolved();
    }

    public int getCurrentStreak() {
        return progressDAO.getCurrentStreak();
    }

    public int getWeekProgress() {
        return progressDAO.getWeekProgress();
    }

    public double getAveragePerDay() {
        return progressDAO.getAveragePerDay();
    }
}