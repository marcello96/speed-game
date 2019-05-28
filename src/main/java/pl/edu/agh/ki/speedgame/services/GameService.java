package pl.edu.agh.ki.speedgame.services;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import pl.edu.agh.ki.speedgame.exceptions.NoSuchUserException;
import pl.edu.agh.ki.speedgame.exceptions.SuchUserExistException;
import pl.edu.agh.ki.speedgame.model.Game;
import pl.edu.agh.ki.speedgame.model.User;
import pl.edu.agh.ki.speedgame.model.dao.Mark;
import pl.edu.agh.ki.speedgame.model.requests.LastResultResponse;
import pl.edu.agh.ki.speedgame.model.requests.TaskConfig;
import pl.edu.agh.ki.speedgame.repository.MarkRepository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingDouble;

@Service
@Slf4j
public class GameService {
    private final MarkRepository markRepository;
    private final UserService userService;
    private final FolderScanService folderScanService;

    private Game theGame;
    private Lock theGameLock;
    private Map<String, TaskConfig> mapTaskNameConfig;
    private Gson gson = new Gson();

    public GameService(MarkRepository markRepository, UserService userService, FolderScanService folderScanService) {
        this.markRepository = markRepository;
        this.userService = userService;
        this.folderScanService = folderScanService;
        this.mapTaskNameConfig = new ConcurrentHashMap<>();
        this.theGameLock = new ReentrantLock();
    }

    @PostConstruct
    public void setUp() {
        theGameLock.lock();
        Set<String> existingGamesNames = StreamSupport.stream(markRepository.findAll().spliterator(), false)
                                                                    .map(Mark::getName)
                                                                    .collect(Collectors.toSet());
        markRepository.saveAll(
                folderScanService.getTaskNames()
                        .stream()
                        .filter(name -> !existingGamesNames.contains(name))
                        .map(Mark::new)
                        .collect(Collectors.toList())
        );
        theGame = new Game(getTasks(folderScanService.scanFolder()));
        theGameLock.unlock();
    }

    public List<TaskConfig> getTasks(List<String> jsons) {
        List<TaskConfig> taskConfigList = jsons.stream().map(j -> gson.fromJson(j, TaskConfig.class)).collect(Collectors.toList());
        taskConfigList.forEach(tc -> mapTaskNameConfig.putIfAbsent(tc.getName(), tc));

        return taskConfigList;
    }


    public void addGame(Game game) {
        theGameLock.lock();
        if (this.theGame == null) {
            this.theGame = game;
        } else {
            this.theGame.updateTasks(game.getTasksConfig());
        }
        theGameLock.unlock();
    }

    public void addUser(String name, int age, String cookie) throws SuchUserExistException {
        theGameLock.lock();
        try {
            Optional<User> previousUser = theGame.getUserByCookie(cookie);
            if (!previousUser.isPresent()) {
                theGame.addUser(name, age, cookie);
            } else if (!previousUser.get().getNick().equals(name)) {
                theGame.removeUserByCookie(cookie);
                theGame.addUser(name, age, cookie);
            }
        } finally {
            theGameLock.unlock();
        }
    }

    public String getConfig(String task, String cookie) throws NoSuchUserException {
        JSONObject config = new JSONObject();

        theGameLock.lock();
        try {
            Optional<User> user = theGame.getUserByCookie(cookie);
            if (user.isPresent()) {
                config.put("group", Game.GAME_ID);
                config.put("nick", user.get().getNick());
                config.put("age", user.get().getAge());
                config.put(
                        "config",
                        theGame.getTasksConfig()
                                .stream()
                                .filter(t -> t.getName().equals(task))
                                .findFirst()
                                .map(TaskConfig::getConfig)
                                .orElse(new ArrayList<>())
                );
                return config.toString();
            }
        } finally {
            theGameLock.unlock();
        }

        throw new NoSuchUserException("Nie ma użytkownika z ciastaczkiem = " + cookie);
    }

    public String getRandomTask(String cookie) throws NoSuchUserException {
        theGameLock.lock();
        try {
            Optional<User> userOptional = theGame.getUserByCookie(cookie);
            if (!userOptional.isPresent()) {
                throw new NoSuchUserException("Nie ma użytkownika z ciasteczkiem = " + cookie);
            }

            return userService.getTaskNameIfAvailable(userOptional.get());
        } finally {
            theGameLock.unlock();
        }
    }

    public void addResult(String taskName, String cookie, String nick, double result) throws NoSuchUserException {
        theGameLock.lock();
        try {
            Optional<User> user = theGame.getUserList().stream().filter(u -> u.getNick().equals(nick) && u.getCookieValue().equals(cookie)).findFirst();
            if (!user.isPresent()) {
                throw new NoSuchUserException("Nie ma użytkownika = " + user + ", z ciasteczkiem = " + cookie);
            }

            user.get().addUserResult(result, taskName);
        } finally {
            theGameLock.unlock();
        }
    }

    public Map<String, Double> getResults() {
        theGameLock.lock();
        try {
            return theGame.getUserList()
                    .stream()
                    .collect(groupingBy(User::getNick, summingDouble(User::getScore)));
        } finally {
            theGameLock.unlock();
        }
    }

    public LastResultResponse getLastResults(String cookie) {
        theGameLock.lock();
        try {
            Optional<User> userOptional = theGame.getUserByCookie(cookie);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                LastResultResponse lastResultResponse = new LastResultResponse();
                lastResultResponse.setLastResult(user.getLastResult());
                lastResultResponse.setScore(user.getScore());
                return lastResultResponse;
            }

            return new LastResultResponse();
        } finally {
            theGameLock.unlock();
        }
    }

    public TaskConfig getTaskConfig(String taskName) {
        return mapTaskNameConfig.getOrDefault(taskName, new TaskConfig(taskName, new ArrayList<>()));
    }
}
