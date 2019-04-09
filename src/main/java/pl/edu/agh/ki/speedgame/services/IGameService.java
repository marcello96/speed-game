package pl.edu.agh.ki.speedgame.services;

import org.json.JSONException;
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
import pl.edu.agh.ki.speedgame.model.requests.LastResultResponse;
import pl.edu.agh.ki.speedgame.model.requests.TaskConfig;

import java.util.List;
import java.util.Map;

@Service
public interface IGameService {
    List<TaskConfig> getTasks(List<String> jsons);

    void addGame(Game game) throws GameWithSuchIdExistException;

    void addUser(String name, int age, String group, String cookie) throws SuchUserExistException;

    String getConfig(String task, String cookie) throws NoSuchUserException, JSONException;

    String getRandomTask(String cookie) throws NoMoreAvailableTasksException, NoSuchUserException;

    boolean isCreator(String groupId, String cookie);

    void removeGame(String groupId, String cookie) throws CannotRemoveGameException;

    void addResult(String taskName, String cookie, String nick, String group, double result) throws NoSuchGameException, NoSuchUserException;

    Map<String, Double> getResults(String groupId, String cookie) throws NoSuchGameException, LackOfAccessException;

    String generateRandomGroupId();

    LastResultResponse getLastResults(String cookie);

    TaskConfig getTaskConfig(String taskName);

    void checkUserCurrentTask(String task, String cookie) throws WrongTaskException;
}
