package pl.edu.agh.ki.speedgame.model;

import lombok.Data;
import pl.edu.agh.ki.speedgame.exceptions.NoMoreAvailableTasksException;

import java.util.List;

import static pl.edu.agh.ki.speedgame.utils.InFunUtils.RED;
import static pl.edu.agh.ki.speedgame.utils.InFunUtils.RESET;

@Data
public class User {
    private String nick;
    private int age;
    private String cookieValue;
    private double score;
    private double lastResult;
    private List<String> availableTasks;
    private int completedNumber;
    private String currentTask;

    public User(String nick, int age, String cookieValue, List<String> taskList) {
        this.nick = nick;
        this.age = age;
        this.cookieValue = cookieValue;
        this.availableTasks = taskList;
        this.lastResult = 0;
    }

    public void addUserResult(double result, String task) {
        if (!currentTask.equals(task))
            System.out.println(RED + "Given result = " + result + " it's from wrong task = " + task + " but current task is = " + currentTask + RESET);
        this.score += result;
        this.lastResult = result;
    }

    public String getRandomTask() throws NoMoreAvailableTasksException {
        if (availableTasks.size() > 0) {
            currentTask = availableTasks.remove(0);
            return currentTask;
        } else {
            throw new NoMoreAvailableTasksException("Nie ma już więcej dostępnych zadań");
        }
    }
}