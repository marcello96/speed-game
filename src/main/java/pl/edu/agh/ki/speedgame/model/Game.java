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

import static java.util.stream.Collectors.toList;

@Data
@Slf4j
public class Game {
    private String id;
    private List<TaskConfig> tasksConfig;
    private List<String> tasks;
    private List<User> userList;
    private Gson gson;
    private Random random;

    public Game(String id, List<TaskConfig> tasksConfig) {
        this.random = new Random();
        this.gson = new Gson();
        this.id = id;
        this.userList = Collections.synchronizedList(new ArrayList<>());
        this.tasksConfig = Collections.synchronizedList(tasksConfig);
        this.tasks = Collections.synchronizedList(tasksConfig.stream().map(TaskConfig::getName).collect(toList()));
    }

    public void addUser(String name, int age, String cookie) throws SuchUserExistException {
        if (userList.stream().anyMatch(u -> u.getNick().equals(name))) {
            throw new SuchUserExistException("Użytkownik = " + name + " już istnieje");
        }
        userList.add(new User(name, age, cookie, new ArrayList<>(tasks)));
    }

    public void removeUserByCookie(String cookie) {
        List<User> usersToRemove = userList.stream()
                .filter(user -> user.getCookieValue().equals(cookie))
                .collect(toList());

        userList.removeAll(usersToRemove);
    }

    public Optional<User> getUserByCookie(String cookie) {
        return userList.stream().filter(u -> u.getCookieValue().equals(cookie)).findFirst();
    }

    public void updateTasks(List<TaskConfig> tasksConfig) {
        this.tasksConfig.clear();
        this.tasksConfig.addAll(tasksConfig);
        this.tasks.clear();
        this.tasks.addAll(tasksConfig.stream().map(TaskConfig::getName).collect(toList()));
    }
}
