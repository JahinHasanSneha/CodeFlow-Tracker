// LeetCodeService.java
package services;

import dao.LeetCodeProblemDAO;
import models.LeetCodeProblem;

import java.util.List;

public class LeetCodeService {
    private LeetCodeProblemDAO leetCodeDAO;

    public LeetCodeService() {
        this.leetCodeDAO = new LeetCodeProblemDAO();
    }

    public boolean addProblem(LeetCodeProblem problem) {
        return leetCodeDAO.addProblem(problem);
    }

    public boolean updateProblem(LeetCodeProblem problem) {
        return leetCodeDAO.updateProblem(problem);
    }

    public boolean deleteProblem(String id) {
        return leetCodeDAO.deleteProblem(id);
    }

    public List<LeetCodeProblem> getAllProblems() {
        return leetCodeDAO.getAllProblems();
    }

    public List<LeetCodeProblem> getProblemsByDifficulty(String difficulty) {
        return leetCodeDAO.getProblemsByDifficulty(difficulty);
    }

    public List<LeetCodeProblem> getSolvedProblems() {
        return leetCodeDAO.getSolvedProblems();
    }

    public List<LeetCodeProblem> getUnsolvedProblems() {
        return leetCodeDAO.getUnsolvedProblems();
    }

    public int getSolvedCount() {
        return leetCodeDAO.getSolvedCount();
    }

    public int getCountByDifficulty(String difficulty) {
        return leetCodeDAO.getCountByDifficulty(difficulty);
    }

    public void refreshProblems(List<LeetCodeProblem> problems) {
        problems.clear();
        problems.addAll(getAllProblems());
    }
}