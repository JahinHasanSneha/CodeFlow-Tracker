// ProjectService.java
package services;

import dao.ProjectDAO;
import models.Project;

import java.time.LocalDate;
import java.util.List;

public class ProjectService {
    private ProjectDAO projectDAO;

    public ProjectService() {
        this.projectDAO = new ProjectDAO();
    }

    public boolean addProject(Project project) {
        return projectDAO.addProject(project);
    }

    public boolean updateProject(Project project) {
        return projectDAO.updateProject(project);
    }

    public boolean deleteProject(String id) {
        return projectDAO.deleteProject(id);
    }

    public Project getProjectById(String id) {
        return projectDAO.getProjectById(id);
    }

    public List<Project> getAllProjects() {
        return projectDAO.getAllProjects();
    }

    public List<Project> getUpcomingProjects(int days) {
        return projectDAO.getUpcomingProjects(days);
    }

    public List<Project> getProjectsForDate(LocalDate date) {
        return projectDAO.getProjectsForDate(date);
    }

    public void refreshProjects(List<Project> projects) {
        projects.clear();
        projects.addAll(getAllProjects());
    }
}