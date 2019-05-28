package pl.edu.agh.ki.speedgame.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import static pl.edu.agh.ki.speedgame.utils.InFunUtils.RED;
import static pl.edu.agh.ki.speedgame.utils.InFunUtils.RESET;

@Data
@Slf4j
public class User {
    private String nick;
    private int age;
    private String cookieValue;
    private double score;
    private double lastResult;
    private String currentTask;
    private Game game;

    public User(String nick, int age, String cookieValue, Game game) {
        this.nick = nick;
        this.age = age;
        this.cookieValue = cookieValue;
        this.lastResult = 0;
        this.game = game;
    }

    public void addUserResult(double result, String task) {
        if (!currentTask.equals(task))
            log.info(RED + "Given result = " + result + " it's from wrong task = " + task + " but current task is = " + currentTask + RESET);
        this.score += result;
        this.lastResult = result;
    }
}