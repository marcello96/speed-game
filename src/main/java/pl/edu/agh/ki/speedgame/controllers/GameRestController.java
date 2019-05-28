package pl.edu.agh.ki.speedgame.controllers;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.ki.speedgame.exceptions.NoSuchUserException;
import pl.edu.agh.ki.speedgame.model.requests.LastResultResponse;
import pl.edu.agh.ki.speedgame.services.GameService;

import java.util.Map;

@RestController
public class GameRestController {

    private final GameService gameService;

    public GameRestController(GameService gameService) {
        this.gameService = gameService;
    }

    @RequestMapping("/{task_name}/config")
    @ResponseBody
    public String getConfig(@PathVariable(value = "task_name") final String taskName, @CookieValue("JSESSIONID") String cookie) throws NoSuchUserException {
        return gameService.getConfig(taskName, cookie);
    }

    @RequestMapping("/game/results")
    @ResponseBody
    public Map<String, Double> getResults() {
        return gameService.getResults();
    }

    @RequestMapping("/last/results")
    @ResponseBody
    public LastResultResponse getLastResults(@CookieValue("JSESSIONID") String cookie) {
        return gameService.getLastResults(cookie);
    }
}
