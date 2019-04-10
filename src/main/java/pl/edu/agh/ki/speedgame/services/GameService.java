package pl.edu.agh.ki.speedgame.services;

import com.google.gson.Gson;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.ki.speedgame.exceptions.CannotRemoveGameException;
import pl.edu.agh.ki.speedgame.exceptions.GameWithSuchIdExistException;
import pl.edu.agh.ki.speedgame.exceptions.LackOfAccessException;
import pl.edu.agh.ki.speedgame.exceptions.NoMoreAvailableTasksException;
import pl.edu.agh.ki.speedgame.exceptions.NoSuchGameException;
import pl.edu.agh.ki.speedgame.exceptions.NoSuchUserException;
import pl.edu.agh.ki.speedgame.exceptions.SuchUserExistException;
import pl.edu.agh.ki.speedgame.exceptions.WrongTaskException;
import pl.edu.agh.ki.speedgame.model.Game;
import pl.edu.agh.ki.speedgame.model.User;
import pl.edu.agh.ki.speedgame.model.orm.Task;
import pl.edu.agh.ki.speedgame.model.requests.LastResultResponse;
import pl.edu.agh.ki.speedgame.model.requests.TaskConfig;
import pl.edu.agh.ki.speedgame.repository.TaskRepository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class GameService implements IGameService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private FolderScanService folderScanService;

    private List<Game> games;
    private Map<String, TaskConfig> mapTaskNameConfig;
    private Gson gson = new Gson();

    public GameService() {
        this.games = Collections.synchronizedList(new ArrayList<Game>());
        this.mapTaskNameConfig = new ConcurrentHashMap<>();
    }

    @PostConstruct
    public void setUp() {
        taskRepository.saveAll(
                folderScanService.getTaskNames()
                        .stream()
                        .map(Task::new)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public List<TaskConfig> getTasks(List<String> jsons) {
//        System.out.println(jsons.stream().map(j -> gson.fromJson(j, TaskConfig.class)).collect(Collectors.toList()));
//        System.out.println((TaskConfig)((Object)jsons.stream().map(j -> gson.fromJson(j, TaskConfig.class)).collect(Collectors.toList()).get(1)));
        List<TaskConfig> taskConfigList = jsons.stream().map(j -> gson.fromJson(j, TaskConfig.class)).collect(Collectors.toList());
        taskConfigList.forEach(tc -> mapTaskNameConfig.putIfAbsent(tc.getName(), tc));

        return taskConfigList;
    }


    @Override
    public void addGame(Game game) throws GameWithSuchIdExistException {
        if (games.stream().filter(g -> g.getId().equals(game.getId())).count() > 0) {
            throw new GameWithSuchIdExistException("Gra z id = " + game.getId() + " już istnieje");
        }
        games.add(game);
    }

    @Override
    public void addUser(String name, int age, String group, String cookie) throws SuchUserExistException {
        if (games.stream().anyMatch(g -> g.getUserByCookie(cookie).isPresent())) {
            throw new SuchUserExistException("Użytkownik z ciasteczkiem " + cookie + " już istanieje");
        }

        Optional<Game> game = games.stream().filter(g -> g.getId().equals(group)).findFirst();
        if (game.isPresent()) {
            game.get().addUser(name, age, cookie);
        }
    }

    @Override
    public String getConfig(String task, String cookie) throws NoSuchUserException, JSONException {
        JSONObject config = new JSONObject();
        for (Game game : games) {
            Optional<User> user = game.getUserByCookie(cookie);
            if (user.isPresent()) {
                config.put("group", game.getId());
                config.put("nick", user.get().getNick());
                config.put("age", user.get().getAge());
                try {
                    config.put("config", game.getTasksConfig().stream().filter(t -> t.getName().equals(task)).findFirst().get().getConfig());
                } catch (Exception e) {
                    System.out.println("there is no config for " + task);
                    config.put("config", new ArrayList<>());
                }
                return config.toString();
            }
        }
        throw new NoSuchUserException("Nie ma użytkownika z ciastaczkiem = " + cookie);
    }

    @Override
    public String getRandomTask(String cookie) throws NoMoreAvailableTasksException, NoSuchUserException {
        Optional<Game> gameOptional = games.stream().filter(g -> g.getUserByCookie(cookie).isPresent()).findFirst();
        if (!gameOptional.isPresent()) {
            throw new NoSuchUserException("Nie ma użytkownika z ciasteczkiem = " + cookie);
        }

        Game game = gameOptional.get();
        Optional<User> userOptional = game.getUserByCookie(cookie);
        if (!userOptional.isPresent()) {
            throw new NoSuchUserException("Nie ma użytkownika z ciasteczkiem = " + cookie);
        }

        User user = userOptional.get();
        return user.getRandomTask();
    }

    @Override
    public boolean isCreator(String groupId, String cookie) {
        return games
                .stream()
                .filter(g -> g.getId().equals(groupId))
                .findFirst()
                .map(g -> g.getCreatorCookie().equals(cookie))
                .orElse(false);
    }

    @Override
    public void removeGame(String groupId, String cookie) throws CannotRemoveGameException {
        Optional<Game> game = games.stream().filter(g -> g.getId().equals(groupId) && g.getCreatorCookie().equals(cookie)).findAny();
        if (game.isPresent() && game.get().getCreatorCookie().equals(cookie)) {
            games.remove(game.get());
        } else {
            throw new CannotRemoveGameException("Nie można usunąć gry o id = " + groupId);
        }
    }

    @Override
    public void addResult(String taskName, String cookie, String nick, String group, double result) throws NoSuchGameException, NoSuchUserException {
        Optional<Game> game = games.stream().filter(g -> g.getId().equals(group)).findFirst();
        if (!game.isPresent()) {
            throw new NoSuchGameException("Nie ma gry o id = " + group);
        }

        Optional<User> user = game.get().getUserList().stream().filter(u -> u.getNick().equals(nick) && u.getCookieValue().equals(cookie)).findFirst();
        if (!user.isPresent()) {
            throw new NoSuchUserException("Nie ma użytkownika = " + user + ", z ciasteczkiem = " + cookie);
        }

        user.get().addUserResult(result, taskName);
    }

    @Override
    public Map<String, Double> getResults(String groupId, String cookie) throws NoSuchGameException, LackOfAccessException {
        Optional<Game> gameOptional = games.stream().filter(g -> g.getId().equals(groupId)).findAny();
        if (!gameOptional.isPresent())
            throw new NoSuchGameException("Nie ma gry z id = " + groupId);
        Game game = gameOptional.get();
        if (!game.getCreatorCookie().equals(cookie))
            throw new LackOfAccessException("Nie można pobrać wyników ze względu na niewłaściwe ciasteczko");

        return game.getUserList().stream().collect(Collectors.groupingBy(User::getNick, Collectors.summingDouble(User::getScore)));
    }

    @Override
    public String generateRandomGroupId() {
        RandomStringGenerator randomStringGenerator = new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .filteredBy(CharacterPredicates.ASCII_UPPERCASE_LETTERS)
                .build();
        String groupId;
        while (containsGroupId(groupId = randomStringGenerator.generate(10))) {
        }

        return groupId;
    }

    @Override
    public LastResultResponse getLastResults(String cookie) {
        Optional<Game> gameOptional = games.stream().filter(g -> g.getUserByCookie(cookie).isPresent()).findFirst();
        if (gameOptional.isPresent()) {
            Game game = gameOptional.get();
            Optional<User> userOptional = game.getUserByCookie(cookie);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                LastResultResponse lastResultResponse = new LastResultResponse();
                lastResultResponse.setLastResult(user.getLastResult());
                lastResultResponse.setScore(user.getScore());
                return lastResultResponse;
            }
        }

        return new LastResultResponse();
    }

    @Override
    public TaskConfig getTaskConfig(String taskName) {
        return mapTaskNameConfig.getOrDefault(taskName, new TaskConfig(taskName, new ArrayList<>()));
    }

    @Override
    public void checkUserCurrentTask(String task, String cookie) throws WrongTaskException {
        for (Game game : games) {
            for (User user : game.getUserList()) {
                if (user.getCookieValue().equals(cookie)) {
                    if (!user.getCurrentTask().equals(task)) {
                        throw new WrongTaskException("Zła gra: " + task);
                    }
                    return;
                }
            }
        }
    }


    private boolean containsGroupId(String groupId) {
        return games.stream().filter(g -> g.getId().equals(groupId)).count() > 0;
    }
}
