// TaskService.java
package services;

import dao.TaskDAO;
import models.Task;

import java.time.LocalDate;
import java.util.List;

public class TaskService {
    private TaskDAO taskDAO;

    public TaskService() {
        this.taskDAO = new TaskDAO();
    }

    public boolean addTask(Task task) {
        return taskDAO.addTask(task);
    }

    public boolean updateTask(Task task) {
        return taskDAO.updateTask(task);
    }

    public boolean deleteTask(String id) {
        return taskDAO.deleteTask(id);
    }

    public Task getTaskById(String id) {
        return taskDAO.getTaskById(id);
    }

    public List<Task> getAllTasks() {
        return taskDAO.getAllTasks();
    }

    public List<Task> getTasksByDate(LocalDate date) {
        return taskDAO.getTasksByDate(date);
    }

    public List<Task> getActiveTasks() {
        return taskDAO.getActiveTasks();
    }

    public List<Task> getCompletedTasks() {
        return taskDAO.getCompletedTasks();
    }

    public int getCompletedToday() {
        return taskDAO.getCompletedToday();
    }

    public int getActiveCount() {
        return taskDAO.getActiveCount();
    }

    public void refreshTasks(List<Task> tasks) {
        tasks.clear();
        tasks.addAll(getAllTasks());
    }
}