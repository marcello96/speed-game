package pl.edu.agh.ki.speedgame.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.edu.agh.ki.speedgame.exceptions.NoSuchGameException;
import pl.edu.agh.ki.speedgame.exceptions.NoSuchUserException;
import pl.edu.agh.ki.speedgame.exceptions.SuchUserExistException;
import pl.edu.agh.ki.speedgame.model.Game;
import pl.edu.agh.ki.speedgame.model.domain.TaskResult;
import pl.edu.agh.ki.speedgame.model.requests.CreateGameInput;
import pl.edu.agh.ki.speedgame.model.requests.CreateGameInputConfig;
import pl.edu.agh.ki.speedgame.model.requests.JoinGameInput;
import pl.edu.agh.ki.speedgame.model.requests.TaskMark;
import pl.edu.agh.ki.speedgame.services.FolderScanService;
import pl.edu.agh.ki.speedgame.services.GameService;
import pl.edu.agh.ki.speedgame.services.MarkService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class SpeedGameController {

    private static final String SESSION_COOKIE_NAME = "JSESSIONID";

    private final GameService gameService;
    private final FolderScanService folderScanService;
    private final MarkService markService;

    public SpeedGameController(GameService gameService, FolderScanService folderScanService, MarkService markService) {
        this.gameService = gameService;
        this.folderScanService = folderScanService;
        this.markService = markService;
    }

    @RequestMapping("/")
    public String main() {
        return "redirect:/joinGame";
    }

    @RequestMapping("/createGame")
    public String createGame(Model model) {
        CreateGameInput createGameInput = new CreateGameInput(gameService.getTasks(folderScanService.scanFolder()));
        model.addAttribute("createGameInput", createGameInput);

        return "createGame";
    }

    @GetMapping(value = "/joinGame")
    public String joinGame(Model model) {
        model.addAttribute("joinGameInput", new JoinGameInput());
        return "joinGame";
    }

    @PostMapping(value = "/joinGame")
    public String getTask(@ModelAttribute JoinGameInput joinGameInput, @CookieValue(SESSION_COOKIE_NAME) String cookie) throws SuchUserExistException {
        gameService.addUser(joinGameInput.nick, joinGameInput.age, cookie);
        return "redirect:/newtask";
    }

    @GetMapping(value = "/newtask")
    public String getNewTask() {
        return "redirect:/tasks/new";
    }

    @GetMapping("/tasks/new")
    public String getFile(@CookieValue(SESSION_COOKIE_NAME) String cookie) throws NoSuchUserException {
        return gameService.getRandomTask(cookie) + "/index";
    }

    @RequestMapping("/manage")
    public String manage(@ModelAttribute("createGameInput") CreateGameInputConfig createGameInputConfig) {
        if (createGameInputConfig.getTasksConfig() != null && !createGameInputConfig.getTasksConfig().isEmpty()) {
            List<String> userChoice = createGameInputConfig.getTasksConfig();
            gameService.addGame(new Game(userChoice.stream().map(gameService::getTaskConfig).collect(Collectors.toList())));
        }
        return "manage";
    }

    @PostMapping(value = "/{task_name}/end")
    @ResponseBody
    public String endGame(@PathVariable(value = "task_name") final String taskName, @RequestBody TaskResult taskResult, @CookieValue(SESSION_COOKIE_NAME) String cookie, Model model) throws NoSuchGameException, NoSuchUserException {
        gameService.addResult(taskName, cookie, taskResult.getNick(), taskResult.getResult());
        model.addAttribute("result", taskResult);
        return "/taskResult?taskName=" + taskName;
    }

    @GetMapping(value = "/taskResult")
    public String taskResult(@RequestParam(value = "taskName") final String taskName, Model model) {
        model.addAttribute("taskMark", new TaskMark(taskName));
        return "taskResult";
    }

    @PostMapping(value = "/taskResult")
    public String addMark(@ModelAttribute @Valid TaskMark taskMark, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "taskResult";
        }

        markService.addMark(taskMark.getTaskName(), taskMark.getMark());
        return "redirect:/newtask";
    }

    @GetMapping(value = "/ratings")
    public String ratings(Model model) {
        model.addAttribute("ratings", markService.getAllMarks());
        return "ratings";
    }
}