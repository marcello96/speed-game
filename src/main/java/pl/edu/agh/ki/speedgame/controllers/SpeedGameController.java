package pl.edu.agh.ki.speedgame.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.edu.agh.ki.speedgame.exceptions.CannotRemoveGameException;
import pl.edu.agh.ki.speedgame.exceptions.GameWithSuchIdExistException;
import pl.edu.agh.ki.speedgame.exceptions.NoGameSelectedException;
import pl.edu.agh.ki.speedgame.exceptions.NoMoreAvailableTasksException;
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
import pl.edu.agh.ki.speedgame.services.ManagementService;
import pl.edu.agh.ki.speedgame.services.MarkService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class SpeedGameController {

    private static final String SESSION_COOKIE_NAME = "JSESSIONID";

    private final GameService gameService;
    private final FolderScanService folderScanService;
    private final MarkService markService;
    private final ManagementService managementService;

    public SpeedGameController(GameService gameService, FolderScanService folderScanService, MarkService markService, ManagementService managementService) {
        this.gameService = gameService;
        this.folderScanService = folderScanService;
        this.markService = markService;
        this.managementService = managementService;
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
        gameService.addUser(joinGameInput.nick, joinGameInput.age, joinGameInput.groupId, cookie);
        return "redirect:/newtask";
    }

    @GetMapping(value = "/newtask")
    public String getNewTask() {
        return "redirect:/tasks/new";
    }

    @GetMapping("/tasks/new")
    public String getFile(@CookieValue(SESSION_COOKIE_NAME) String cookie) throws NoSuchUserException {
        try {
            return gameService.getRandomTask(cookie) + "/index";
        } catch (NoMoreAvailableTasksException e) {
            return "redirect:/end";
        }
    }

    @RequestMapping("/manage")
    public String manage(@ModelAttribute("createGameInput") CreateGameInputConfig createGameInputConfig, @CookieValue(SESSION_COOKIE_NAME) String cookie, Model model) throws GameWithSuchIdExistException, NoGameSelectedException {
        List<String> userChoice = managementService.extractUserChoice(createGameInputConfig);
        String groupId = managementService.extractGroupId(createGameInputConfig);
        gameService.addGame(new Game(groupId, userChoice.stream().map(gameService::getTaskConfig).collect(Collectors.toList()), cookie, createGameInputConfig.getTaskNumber()));
        model.addAttribute("group_id", groupId);
        return "manage";
    }


    @PostMapping(value = "/{task_name}/end")
    @ResponseBody
    public String endGame(@PathVariable(value = "task_name") final String taskName, @RequestBody TaskResult taskResult, @CookieValue(SESSION_COOKIE_NAME) String cookie, Model model) throws NoSuchGameException, NoSuchUserException {
        gameService.addResult(taskName, cookie, taskResult.getNick(), taskResult.getGroup(), taskResult.getResult());
        model.addAttribute("result", taskResult);
        return "/taskResult?taskName=" + taskName;
    }

    @GetMapping(value = "/taskResult")
    public String taskResult(@RequestParam(value = "taskName") final String taskName, Model model) {
        model.addAttribute("taskMark", new TaskMark(taskName));
        return "taskResult";
    }

    @PostMapping(value = "/taskResult")
    public String addMark(@ModelAttribute TaskMark taskMark) {
        markService.addMark(taskMark.getTaskName(), taskMark.getMark());
        return "redirect:/newtask";
    }

    @GetMapping(value = "/end")
    public String end(@CookieValue(SESSION_COOKIE_NAME) String cookie, Model model) {
        model.addAttribute("result", "Udało Ci się skończyć wszystkie zadania. \nCałkowity rezultat = " + gameService.getLastResults(cookie).getScore());
        return "end";
    }

    @GetMapping(value = "/{group_id}/remove")
    public String endGame(@CookieValue(SESSION_COOKIE_NAME) String cookie, @PathVariable(value = "group_id") final String groupId) throws CannotRemoveGameException {
        gameService.removeGame(groupId, cookie);
        return "redirect:/";
    }
}