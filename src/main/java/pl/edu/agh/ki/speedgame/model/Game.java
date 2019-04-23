package pl.edu.agh.ki.speedgame.model;

import com.google.gson.Gson;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import pl.edu.agh.ki.speedgame.exceptions.SuchUserExistException;
import pl.edu.agh.ki.speedgame.model.requests.TaskConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Data
@Slf4j
public class Game {
    private String id;
    private List<TaskConfig> tasksConfig;
    private List<String> tasks;
    private List<User> userList;
    private String creatorCookie;
    private int taskNumber;
    private Gson gson;
    private Random random;

    public Game(String id, List<TaskConfig> tasksConfig, String creatorCookie, int taskNumber) {
        this.random = new Random();
        this.gson = new Gson();
        this.id = id;
        this.userList = Collections.synchronizedList(new ArrayList<>());
        this.tasksConfig = tasksConfig;
        this.tasks = createTasksSequence(tasksConfig.stream().map(TaskConfig::getName).collect(Collectors.toList()), taskNumber);
        this.creatorCookie = creatorCookie;
        this.taskNumber = taskNumber;
    }

    private List<String> createTasksSequence(List<String> tasks, int taskNumber) {
        List<String> resultList = new ArrayList<>();
        if (tasks.size() == 1) {
            for (int i = 0; i < taskNumber; i++) {
                resultList.add(tasks.get(0));
            }
            log.info("GENERATED LIST = " + resultList);
            return resultList;
        }

        String currentTask = tasks.remove(random.nextInt(tasks.size()));
        resultList.add(currentTask);
        String previousTask = currentTask;
        for (int i = 0; i < taskNumber - 1; i++) {
            currentTask = tasks.remove(random.nextInt(tasks.size()));
            resultList.add(currentTask);
            tasks.add(previousTask);
            previousTask = currentTask;
        }
        log.info("GENERATED LIST = " + resultList);
        return resultList;
    }

    public void addUser(String name, int age, String cookie) throws SuchUserExistException {
        if (userList.stream().anyMatch(u -> u.getNick().equals(name))) {
            throw new SuchUserExistException("Użytkownika = " + name + " już istnieje");
        }
        userList.add(new User(name, age, cookie, new ArrayList<>(tasks)));
    }


    public Optional<User> getUserByCookie(String cookie) {
        return userList.stream().filter(u -> u.getCookieValue().equals(cookie)).findFirst();
    }
}
